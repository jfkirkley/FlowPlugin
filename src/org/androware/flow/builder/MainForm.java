package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.NavBase;
import org.androware.flow.base.ObjectLoaderSpecBase;
import org.androware.flow.base.StepBase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/16/16.
 */
public class MainForm {
    private JPanel panel1;
    private JTree flowTree;
    private JTable table1;
    private JSplitPane mainSplitPane;
    Project project;
    ToolWindow toolWindow;
    FlowBase flowBase;

    public JPanel loadPanelForType(NodeObjectWrapper nodeObjectWrapper) {

        Object object = nodeObjectWrapper.getObject();
        if(object instanceof NavBase) {
            NavForm navPanel = new NavForm();
            return navPanel.getNavPanel();
        } else if(object instanceof StepBase){
            StepForm stepForm = new StepForm(project, toolWindow, (StepBase)object, flowBase);

            return stepForm.getRootPanel();
        }
        return new JPanel();
    }

    public MainForm(Project project, ToolWindow toolWindow, FlowBase flowBase) {

        this.project = project;
        this.toolWindow = toolWindow;
        this.flowBase = flowBase;

        flowTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Object object = flowTree.getLastSelectedPathComponent();

                if(object != null && object instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode)object).getUserObject();
                    if(userObject instanceof NodeObjectWrapper) {
                        mainSplitPane.setRightComponent(loadPanelForType((NodeObjectWrapper)userObject));
                    }
                }
            }
        });
    }

    public JPanel getRootPanel() {

        return panel1;
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

    private void addObjLoaderNodes(DefaultMutableTreeNode root, List<ObjectLoaderSpecBase> loaderSpecBaseList) {
        if(loaderSpecBaseList != null) {
            int i = 0;
            for(ObjectLoaderSpecBase objectLoaderSpecBase: loaderSpecBaseList) {
                root.add( new DefaultMutableTreeNode(new NodeObjectWrapper(objectLoaderSpecBase, objectLoaderSpecBase.objectId == null? "Loader " + (i++): objectLoaderSpecBase.objectId )));
            }
        }

    }

    private void addStepNavs(DefaultMutableTreeNode root, Map<String, NavBase> navBaseMap) {
        if(navBaseMap != null) {
            int i = 0;
            for(String k: navBaseMap.keySet()) {
                NavBase navBase = navBaseMap.get(k);
                root.add( new DefaultMutableTreeNode(new NodeObjectWrapper(navBase, k)));
            }
        }
    }

    private DefaultMutableTreeNode mkNode(Object o, String n) {
        return new DefaultMutableTreeNode( new NodeObjectWrapper(o,n));
    }

    private void tryAdd(DefaultMutableTreeNode parent, Object o, String n) {
        if(o!=null) {
            parent.add(mkNode(o,n));
        }
    }

    public void buildFlowTree(FlowBase  flowBase) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeObjectWrapper(flowBase, "Flow"));

        DefaultTreeModel model = new DefaultTreeModel(top);

        DefaultMutableTreeNode loaderNode = new DefaultMutableTreeNode("ObjectLoaders");
        top.add(loaderNode);

        addObjLoaderNodes(loaderNode, flowBase.objectLoaderSpecs);

        DefaultMutableTreeNode stepsNode = new DefaultMutableTreeNode("Steps");

        if(flowBase.steps != null) {
            int i = 0;
            for(String name: flowBase.steps.keySet()) {

                StepBase stepBase = flowBase.steps.get(name);
                DefaultMutableTreeNode stepNode = new DefaultMutableTreeNode(new NodeObjectWrapper(stepBase, name));
                stepsNode.add( stepNode );
                loaderNode = new DefaultMutableTreeNode("ObjectLoaders");
                addObjLoaderNodes(loaderNode, stepBase.objectLoaderSpecs);
                stepNode.add(loaderNode);

                addObjLoaderNodes(loaderNode, flowBase.objectLoaderSpecs);

                tryAdd(stepNode, stepBase.twoWayMapper, "TwoWayMapper");
                tryAdd(stepNode, stepBase.objectSaverSpec, "ObjectSaver");

                DefaultMutableTreeNode navs = new DefaultMutableTreeNode("navs");
                stepNode.add(navs);

                addStepNavs(navs, stepBase.navMap);

            }
        }
        top.add(stepsNode);

        model.setRoot(top);

        flowTree.setModel(model);

        model.reload();

        flowTree.setSelectionPath(new TreePath(top.getLastLeaf().getPath()));

        mainSplitPane.setDividerLocation(300);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
