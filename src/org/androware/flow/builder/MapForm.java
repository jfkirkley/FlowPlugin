package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.thaiopensource.xml.dtd.om.Def;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.ui.plaf.beg.BegResources.m;

/**
 * Created by jkirkley on 8/23/16.
 */
public class MapForm implements CRUDForm<Map> {
    private JPanel rootPanel;
    private AnyObjectForm anyObjectForm;
    private JButton setButton;
    private JButton clearButton;
    private JTree mapTree;
    private JButton deleteButton;


    Map map;

    private void populateTree(Map map, DefaultMutableTreeNode currNode) {
        for (Object k : map.keySet()) {
            Object v = map.get(k);
            DefaultMutableTreeNode keyNode = new DefaultMutableTreeNode(k);
            currNode.add(keyNode);

            if (v == null) continue;

            if (Utils.isPrimitiveOrString(v)) {

                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(v);
                keyNode.add(newNode);

            } else if (v instanceof Map) {

                DefaultMutableTreeNode newMapNode = new DefaultMutableTreeNode(k.toString());
                keyNode.add(newMapNode);
                populateTree((Map) v, newMapNode);

            } else if (v instanceof List) {

                List list = (List) v;
                for (Object o : list) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(o);
                    keyNode.add(newNode);
                }
            }

        }
    }

    public DefaultMutableTreeNode deleteCurrNode() {
        Object object = mapTree.getLastSelectedPathComponent();

        if (object != null && object instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            DefaultMutableTreeNode returnNode = node.getNextSibling() != null? node.getNextSibling(): node.getPreviousSibling() != null? node.getPreviousSibling(): (DefaultMutableTreeNode) node.getParent();
            DefaultTreeModel model = (DefaultTreeModel) mapTree.getModel();
            model.removeNodeFromParent(node);

            return returnNode;
        }
        return null;
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target, FormAssembler<CRUDForm> formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target) {

        DefaultTreeModel model = (DefaultTreeModel) mapTree.getModel();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Map");
        model.setRoot(rootNode);

        map = target instanceof Map ? (Map) target : new HashMap<>();

        // ok doki -- populate 'er up!
        populate(map);

        mapTree.setSelectionPath(new TreePath(rootNode.getFirstLeaf().getPath()));

        anyObjectForm.init(project, toolWindow, null);

        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Object value = anyObjectForm.getTarget();
                TreePath path = mapTree.getSelectionPath();
                Object object = mapTree.getLastSelectedPathComponent();

                if (object != null && object instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(value);
                    node.add(newNode);

                    DefaultTreeModel model = (DefaultTreeModel) mapTree.getModel();
                    model.reload();

                    mapTree.expandPath(path);
                    mapTree.setSelectionPath(path);
                }

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultMutableTreeNode newSelectNode = deleteCurrNode();
                if(newSelectNode != null) {
                    mapTree.setSelectionPath(new TreePath(newSelectNode.getPath()));
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                anyObjectForm.clear();
            }
        });

        mapTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Object object = mapTree.getLastSelectedPathComponent();

                if (object != null && object instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
                    Object userObject = node.getUserObject();

                    anyObjectForm.setTarget(userObject);
                }
            }
        });

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Map getTarget() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(Map object) {
        populateTree(map, (DefaultMutableTreeNode)mapTree.getModel().getRoot());
    }
}
