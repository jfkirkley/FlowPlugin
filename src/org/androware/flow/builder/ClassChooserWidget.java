package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ReflectionUtils;

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

    public void init(Project project, String title, ReflectionUtils.FieldSetter fieldSetter) {
        CompFactory.addTreeClassChooserAction(project, browseClassesButton, classNameTextField, title, fieldSetter);
        if(fieldSetter != null) {
            className = (String) fieldSetter.get();
            if (className != null && className.length() > 0) {
                classNameTextField.setText(className);
            }
        }
    }

    public void init(Project project, String title) {
        init(project, title, null);
    }

    public String getClassName(){
        return classNameTextField.getText();
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target) {
        this.className = target;
        this.init(project, "Choose Class", null);
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

    @Override
    public void done() {

    }

}
