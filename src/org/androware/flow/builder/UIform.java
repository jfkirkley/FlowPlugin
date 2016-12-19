package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.AdapterViewSpec;
import org.androware.flow.base.NavBase;
import org.androware.flow.base.StepBase;
import org.androware.flow.base.UI;

import javax.swing.*;



/**
 * Created by jkirkley on 8/22/16.
 */
public class UIform implements CRUDForm<UI> {
    private ComboBoxCRUDForm adapterViewComboBoxCRUDForm;
    private JPanel rootPanel;
    private UI ui;

    @Override
    public void init(Project project, ToolWindow toolWindow, UI target, FormAssembler formAssembler, CRUDForm parentForm) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, UI target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, UI target) {
        ui = (UI)target;
        adapterViewComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl<AdapterViewSpec>(project, toolWindow, AdapterViewSpecForm.class, AdapterViewSpec.class), ui.adapterViews);
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public UI getTarget() {
        return ui;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(UI object) {

    }

    @Override
    public void done() {

    }

    @Override
    public void handleChildValue(Object childValue) {

    }
}
