package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.BooleanGetter;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkirkley on 8/23/16.
 */
public class AnyObjectForm implements CRUDForm {
    private JPanel rootPanel;
    private JTextField stringField;
    private FileChooserWidget fileChooserWidget;
    private ClassChooserWidget classChooserWidget;
    private JRadioButton trueRadioButton;
    private JRadioButton falseRadioButton;

    ButtonGroup buttonGroup;
    private JTextField numberField;

    Object target;

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target) {

        setTarget(target);

        fileChooserWidget.init(project, "Choose File", StdFileTypes.XML);
        classChooserWidget.init(project, "Choose a Class");

        buttonGroup = new ButtonGroup();
        buttonGroup.add(trueRadioButton);
        buttonGroup.add(falseRadioButton);

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Object getTarget() {
        String fileName = fileChooserWidget.getFileName();
        if(fileName != null && fileName.length() > 0) {
            return fileName;
        }
        String className = classChooserWidget.getClassName();
        if(className != null && className.length() > 0) {
            return className;
        }
        String string = stringField.getText();
        if(string != null && string.length() > 0){
            return string;
        }
        String number = numberField.getText();
        if(number != null && number.length() > 0){
            try {
                return Integer.parseInt(number);
            } catch(NumberFormatException e) {
                try {
                    return Long.parseLong(number);
                } catch(NumberFormatException e1) {
                    try {
                        return Double.parseDouble(number);
                    } catch (NumberFormatException e2) {
                    }
                }
            }
        }
        if(falseRadioButton.isSelected()) {
            return false;
        }
        if(trueRadioButton.isSelected()) {
            return true;
        }
        return null;
    }

    @Override
    public void clear() {
        stringField.setText(null);
        fileChooserWidget.clear();
        classChooserWidget.clear();

        numberField.setText(null);
        buttonGroup.clearSelection();
    }

    @Override
    public void populate(Object object) {

    }

    public void setTarget(Object target) {
        this.target = target;

        if(target instanceof String){
            stringField.setText((String)target);
        }

        if(target instanceof Boolean){
            boolean b = (boolean)target;
            if(b) {
                trueRadioButton.setSelected(true);
            } else {
                falseRadioButton.setSelected(false);
            }
        }

        if(target instanceof Number){
            numberField.setText(target.toString());
        }
    }

}
