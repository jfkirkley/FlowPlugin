package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * Created by jkirkley on 8/22/16.
 */
public class FileChooserWidget implements CRUDForm<String>{
    private JTextField fileTextField;
    private JButton browseButton;
    private JPanel rootPanel;

    public void init(Project project, String title, FileType fileType) {
        CompFactory.addFileChooserAction(project, browseButton, fileTextField, title, fileType);
    }

    public String getFileName(){
        return fileTextField.getText();
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target) {
        this.init(project, "Choose File", null);
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public String getTarget() {
        return fileTextField.getText();
    }

    @Override
    public void clear() {
        fileTextField.setText("");
    }

    @Override
    public void populate(String object) {

    }

    public boolean hasValue(){
        return fileTextField.getText().length() > 0;
    }

    @Override
    public void done() {

    }
}
