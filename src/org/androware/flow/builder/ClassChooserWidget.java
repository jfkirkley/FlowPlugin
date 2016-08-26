package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

import static com.intellij.ui.LoadingNode.getText;

/**
 * Created by jkirkley on 8/20/16.
 */
public class ClassChooserWidget implements CRUDForm<String>{
    private JTextField classNameTextField;
    private JButton browseClassesButton;
    private JPanel classChooserPanel;

    String className;

    public void init(Project project, String title) {
        CompFactory.addTreeClassChooserAction(project, browseClassesButton, classNameTextField, title);
    }

    public String getClassName(){
        return classNameTextField.getText();
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target, FormAssembler<CRUDForm> formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target) {
        this.className = target;
        this.init(project, "Choose Class");
    }

    @Override
    public JPanel getRootPanel() {
        return classChooserPanel;
    }

    @Override
    public String getTarget() {
        return getClassName();
    }

    @Override
    public void clear() {
        classNameTextField.setText("");
    }

    @Override
    public void populate(String object) {

    }

    public boolean hasValue(){
        return classNameTextField.getText().length() > 0;
    }

}
