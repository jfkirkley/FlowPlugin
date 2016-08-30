package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.thaiopensource.xml.dtd.om.Def;
import org.androware.flow.plugin.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.ui.plaf.beg.BegResources.m;
import static javax.swing.UIManager.put;

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
    DefaultMutableTreeNode rootNode;

    Map map;

    public static class MapNodeWrapper {

        MapNodeWrapper parent;
        Object value;
        Map map;

        public MapNodeWrapper(MapNodeWrapper parent, Object value) {
            this.parent = parent;
            this.value = value;
            if (parent != null) {
                parent.setChild(value, null);
            }
        }

        public void setChild(Object childName, Object value) {
            if (map == null) {
                map = new HashMap();
                if (parent != null) {
                    parent.setChild(value, map);
                }
            }
            map.put(childName, value);
        }

    }

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
            DefaultMutableTreeNode returnNode = node.getNextSibling() != null ? node.getNextSibling() : node.getPreviousSibling() != null ? node.getPreviousSibling() : (DefaultMutableTreeNode) node.getParent();
            DefaultTreeModel model = (DefaultTreeModel) mapTree.getModel();
            model.removeNodeFromParent(node);

            return returnNode;
        }
        return null;
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target) {

        DefaultTreeModel model = (DefaultTreeModel) mapTree.getModel();
        rootNode = new DefaultMutableTreeNode("Map");
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
                anyObjectForm.clear();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultMutableTreeNode newSelectNode = deleteCurrNode();
                if (newSelectNode != null) {
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

    private boolean hasLeaf(DefaultMutableTreeNode node) {
        int numChildren = node.getChildCount();

        for (int i = 0; i < numChildren; ++i) {
            DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (cnode.isLeaf()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOneLeafChild(DefaultMutableTreeNode node) {
        return node.getChildCount() == 1 && node.getFirstChild().isLeaf();
    }

    private void buildMap(DefaultMutableTreeNode node, Map map) {
        Object v = node.getUserObject();

        int numChildren = node.getChildCount();

        if (numChildren == 1) {
            DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) node.getFirstChild();
            if (cnode.isLeaf()) {
                map.put(v, cnode.getUserObject());
            } else {
                Map newMap = new HashMap();
                map.put(v, newMap);
                buildMap(cnode, newMap);
            }
        } else {

            if (hasLeaf(node)) {
                List childList = new ArrayList();
                map.put(v, childList);
                for (int i = 0; i < numChildren; ++i) {
                    DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (cnode.isLeaf()) {
                        childList.add(cnode.getUserObject());
                    } else {
                        Map newMap = new HashMap();
                        childList.add(newMap);
                        buildMap(cnode, newMap);
                    }
                }
            } else {
                Map newMap = new HashMap();
                map.put(v, newMap);

                for (int i = 0; i < numChildren; ++i) {
                    DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) node.getChildAt(i);
                    buildMap(cnode, newMap);
                }
            }
        }
    }

    private Object buildMap2(DefaultMutableTreeNode node, Map map) {
        Object v = node.getUserObject();
        int numChildren = node.getChildCount();

        if(numChildren > 0) {
            if (map == null) {
                map = new HashMap();
            }
            for (int i = 0; i < numChildren; ++i) {
                DefaultMutableTreeNode cnode = (DefaultMutableTreeNode) node.getChildAt(i);
                if(hasOneLeafChild(cnode)) {
                    map.put(cnode.getUserObject(), ((DefaultMutableTreeNode) cnode.getFirstChild()).getUserObject());
                } else {
                    map.put(cnode.getUserObject(), buildMap2(cnode, null));
                }
            }
            return map;
        }
        return v;
    }

    @Override
    public void done() {
        buildMap2(rootNode, map);
    }

    @Override
    public void populate(Map object) {
        populateTree(map, (DefaultMutableTreeNode) mapTree.getModel().getRoot());
    }
}
