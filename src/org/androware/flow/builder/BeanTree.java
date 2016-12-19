package org.androware.flow.builder;

import com.intellij.psi.*;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.StepBase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.datatype.DatatypeConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.intellij.openapi.application.JetBrainsProtocolHandler.getParameters;

/**
 * Created by jkirkley on 12/16/16.
 */
public class BeanTree {
    private JTree beanTree;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private JPanel rootPanel;

    private NodeObjectWrapper selectedNodeWrapper = null;


    public void init(PsiClass beanClass) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeObjectWrapper(beanClass, beanClass.getName()));
        DefaultTreeModel model = new DefaultTreeModel(top);

        model.setRoot(top);

        try {
            PsiField fields[] = beanClass.getFields();
            for (PsiField field : fields) {
                addField(field, top);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        beanTree.setModel(model);
        model.reload();

        beanTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Object object = beanTree.getLastSelectedPathComponent();

                if (object != null && object instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) object).getUserObject();
                    if (userObject instanceof NodeObjectWrapper) {
                        selectedNodeWrapper = (NodeObjectWrapper) userObject;
                    }
                }
            }
        });

    }

    public class NodeObjectWrapper {
        Object object;
        String name;

        public NodeObjectWrapper(Object object, String name) {
            this.object = object;
            this.name = name;
        }

        public String toString() {
            return name;
        }

        public Object getObject() {
            return object;
        }
    }


    public void addField(PsiField field, DefaultMutableTreeNode parent) {

        PsiType psiType = field.getType();
        String psiTypeRep = PSIclassUtils.getTypeName(psiType);
        System.out.println("---------------------------");

        PsiClassType psiClassType = psiType instanceof PsiClassType ? (PsiClassType) psiType : null;
        PsiArrayType psiArrayType = psiType instanceof PsiArrayType ? (PsiArrayType) psiType : null;

        System.out.println(field.getType());
        System.out.println(field.getName());
        System.out.println(field.getText());
        System.out.println(field.getNameIdentifier());
        System.out.println(psiClassType);
        System.out.println(psiArrayType);

        System.out.println("---------------------------");


        String nodeName = field.getName();
        PsiType keyType = null;
        PsiType fieldType = psiType;
        //

        if (psiType instanceof PsiClassType) {

            PsiType[] params = psiClassType.getParameters();
            for (PsiType psiType1 : params) {
                System.out.println("p: " + psiType1);
            }
        }
        //}


        if (psiTypeRep.startsWith("Map<") || psiTypeRep.startsWith("HashMap<") || psiTypeRep.startsWith("TreeMap<")) {

            keyType = PSIclassUtils.getParameterType(psiClassType, 0);
            fieldType = PSIclassUtils.getParameterType(psiClassType, 1);
            psiClassType = fieldType instanceof PsiClassType ? (PsiClassType) fieldType : null;
            nodeName += " ( Map< " + PSIclassUtils.getTypeName(keyType) + ", " + PSIclassUtils.getTypeName(fieldType)+ " > )";

        } else if (psiTypeRep.startsWith("List<")) {

            fieldType = PSIclassUtils.getParameterType(psiClassType, 0);
            psiClassType = fieldType instanceof PsiClassType ? (PsiClassType) fieldType : null;
            nodeName += " ( List< " + PSIclassUtils.getTypeName(fieldType) + " > )";

        } else if (psiArrayType != null) {

            fieldType = psiArrayType.getComponentType();
            nodeName += " ( " + PSIclassUtils.getTypeName(fieldType) + "[] )";

        } else {

            nodeName += " ( " + psiTypeRep + " ) ";
        }

        DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(new NodeObjectWrapper(field, nodeName));

        parent.add(fieldNode);
        System.out.println(">- " + fieldType);
        if (keyType != null) {
            DefaultMutableTreeNode keyNode = new DefaultMutableTreeNode(new NodeObjectWrapper(keyType, "key (" + PSIclassUtils.getTypeName(keyType) + " )"));
            fieldNode.add(keyNode);
        }

        if (!PSIclassUtils.isPrimitiveOrString(fieldType) && psiClassType != null) {
            PsiClass psiClass = psiClassType.resolve();
            if (psiClass != null) {
                PsiField fields[] = psiClass.getFields();
                for (PsiField f : fields) {
                    addField(f, fieldNode);
                }
            }
        }
    }

    public Object getSelectedNode() {
        if (selectedNodeWrapper != null) {
            return selectedNodeWrapper.getObject();
        }
        return null;
    }
}
