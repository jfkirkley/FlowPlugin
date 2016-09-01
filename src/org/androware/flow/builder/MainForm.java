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

import java.awt.*;
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
    private JSplitPane mainSplitPane;
    private JScrollPane contentScrollPane;
    private JPanel contentPanel;
    Project project;
    ToolWindow toolWindow;
    FlowBase flowBase;
    DefaultMutableTreeNode stepsNode;

    public static MainForm mainForm;

    public JPanel loadPanelForType(NodeObjectWrapper nodeObjectWrapper) {

        Object object = nodeObjectWrapper.getObject();
        if (object instanceof NavBase) {
            NavForm navPanel = new NavForm();
            return navPanel.getNavPanel();

        } else if (object instanceof StepBase) {

            StepForm stepForm = new StepForm();
            stepForm.init(project, toolWindow, (StepBase) object, flowBase);
            return stepForm.getRootPanel();

        } else if (object instanceof FlowBase) {

            FlowForm flowForm = new FlowForm(project, toolWindow, (FlowBase) object);
            return flowForm.getRootPanel();
        }
        return new JPanel();
    }

    public MainForm(Project project, ToolWindow toolWindow, FlowBase flowBase) {
        mainForm = this;
        this.project = project;
        this.toolWindow = toolWindow;
        this.flowBase = flowBase;

        FlowForm flowForm = new FlowForm(project, toolWindow, flowBase);
        setContent(flowForm.getRootPanel());

        flowTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Object object = flowTree.getLastSelectedPathComponent();

                if (object != null && object instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) object).getUserObject();
                    if (userObject instanceof NodeObjectWrapper) {
                        setContent(loadPanelForType((NodeObjectWrapper) userObject));
                    }
                }
            }
        });
    }

    public void setContent(JPanel content) {
        mainSplitPane.setRightComponent(content);
    }

    public Component getContent() {
        return mainSplitPane.getRightComponent();
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
        if (loaderSpecBaseList != null) {
            int i = 0;
            for (ObjectLoaderSpecBase objectLoaderSpecBase : loaderSpecBaseList) {
                root.add(new DefaultMutableTreeNode(new NodeObjectWrapper(objectLoaderSpecBase, objectLoaderSpecBase.objectId == null ? "Loader " + (i++) : objectLoaderSpecBase.objectId)));
            }
        }

    }

    private void addStepNavs(DefaultMutableTreeNode root, Map<String, NavBase> navBaseMap) {
        if (navBaseMap != null) {
            int i = 0;
            for (String k : navBaseMap.keySet()) {
                NavBase navBase = navBaseMap.get(k);
                root.add(new DefaultMutableTreeNode(new NodeObjectWrapper(navBase, k)));
            }
        }
    }

    private DefaultMutableTreeNode mkNode(Object o, String n) {
        return new DefaultMutableTreeNode(new NodeObjectWrapper(o, n));
    }

    private void tryAdd(DefaultMutableTreeNode parent, Object o, String n) {
        if (o != null) {
            parent.add(mkNode(o, n));
        }
    }

    public void addStep(StepBase stepBase) {
        String newName = stepBase.name;

        String oldName = (String) Utils.getKeyForValue(flowBase.steps, stepBase);
        if(!oldName.equals(newName)) {
            DefaultMutableTreeNode stepNode = addStepNode(stepBase);
            flowBase.steps.remove(oldName);
            flowBase.steps.put(newName, stepBase);

            DefaultTreeModel model = (DefaultTreeModel)flowTree.getModel();
            model.reload();
            flowTree.setSelectionPath(new TreePath(stepNode.getPath()));
        }

    }

    public DefaultMutableTreeNode addStepNode(StepBase stepBase) {

        DefaultMutableTreeNode stepNode = new DefaultMutableTreeNode(new NodeObjectWrapper(stepBase, stepBase.name));
        stepsNode.add(stepNode);
        DefaultMutableTreeNode loaderNode = new DefaultMutableTreeNode("ObjectLoaders");
        addObjLoaderNodes(loaderNode, stepBase.objectLoaderSpecs);
        stepNode.add(loaderNode);

        addObjLoaderNodes(loaderNode, flowBase.objectLoaderSpecs);

        tryAdd(stepNode, stepBase.twoWayMapper, "TwoWayMapper");
        tryAdd(stepNode, stepBase.objectSaverSpec, "ObjectSaver");

        DefaultMutableTreeNode navs = new DefaultMutableTreeNode("navs");
        stepNode.add(navs);

        addStepNavs(navs, stepBase.navMap);

        return stepNode;
    }

    public void buildFlowTree(FlowBase flowBase) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new NodeObjectWrapper(flowBase, "Flow"));

        DefaultTreeModel model = new DefaultTreeModel(top);

        DefaultMutableTreeNode loaderNode = new DefaultMutableTreeNode("ObjectLoaders");
        top.add(loaderNode);

        addObjLoaderNodes(loaderNode, flowBase.objectLoaderSpecs);

        stepsNode = new DefaultMutableTreeNode("Steps");

        if (flowBase.steps != null) {

            for (String name : flowBase.steps.keySet()) {

                StepBase stepBase = flowBase.steps.get(name);
                stepBase.name = name;
                addStepNode(stepBase);
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
