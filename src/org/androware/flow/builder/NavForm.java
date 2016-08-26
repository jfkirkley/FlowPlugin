package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.NavBase;

import javax.swing.*;

/**
 * Created by jkirkley on 8/18/16.
 */
public class NavForm  implements CRUDForm<NavBase> {
    private JCheckBox checkBox1;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;

    public JPanel getNavPanel() {
        return navPanel;
    }

    private JPanel navPanel;
    private JComboBox comboBox4;
    private JComboBox comboBox5;

    @Override
    public void init(Project project, ToolWindow toolWindow, NavBase target, FormAssembler<CRUDForm> formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, NavBase target) {

    }

    @Override
    public JPanel getRootPanel() {
        return navPanel;
    }

    @Override
    public NavBase getTarget() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(NavBase object) {

    }
}
