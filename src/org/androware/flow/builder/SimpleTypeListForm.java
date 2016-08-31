package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jkirkley on 8/25/16.
 */
public class SimpleTypeListForm<T> implements CRUDForm<List<T>> {

    private JComboBox typeListComboBox;
    private JButton addButton;

    private JButton deleteButton;
    private JPanel rootPanel;
    List<T> targetList;

    public class TreeClassChooserFormAssembler implements FormAssembler<SimpleTypeListForm> {
        @Override
        public void assemble(Project project, ToolWindow toolWindow, SimpleTypeListForm form) {
            CompFactory.addTreeClassChooserAction(project, addButton, typeListComboBox, targetList, "");
        }
    }

    public class FileChooserFormAssembler implements FormAssembler<SimpleTypeListForm> {
        FileType fileType;
        public FileChooserFormAssembler(FileType fileType) {
            this.fileType = fileType;
        }

        @Override
        public void assemble(Project project, ToolWindow toolWindow, SimpleTypeListForm form) {
            CompFactory.addFileChooserAction(project, addButton, typeListComboBox, targetList, "", fileType);
        }
    }

    public class AnyObjectFormAssembler implements FormAssembler<SimpleTypeListForm> {

        AnyObjectForm anyObjectForm;
        List items;

        public AnyObjectFormAssembler(AnyObjectForm anyObjectForm){
            this.anyObjectForm = anyObjectForm;
        }

        public AnyObjectFormAssembler(AnyObjectForm anyObjectForm, List items){
            this(anyObjectForm);
            this.items = items;
        }

        @Override
        public void assemble(Project project, ToolWindow toolWindow, SimpleTypeListForm form) {
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Object v = anyObjectForm.getTarget();
                    typeListComboBox.addItem(v);
                    anyObjectForm.clear();
                    if(items != null) {
                        items.add(v);
                    }
                }
            });
        }
    }


    @Override
    public void populate(List<T> object) {
        if(targetList != null) {
            CompFactory.fillCombo(typeListComboBox, targetList);
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, List<T> target, FormAssembler formAssembler) {
        targetList = target;
        if(formAssembler != null) {
            formAssembler.assemble(project, toolWindow, this);
        }
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(typeListComboBox.getSelectedIndex() != -1) {
                    Object item = typeListComboBox.getSelectedItem();
                    typeListComboBox.removeItem(item);
                    targetList.remove(item);
                }
            }
        });
        populate(target);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, List<T> target) {
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public List<T> getTarget() {
        return targetList;
    }

    @Override
    public void clear() {
    }

    public JComboBox getTypeListComboBox() {
        return typeListComboBox;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    @Override
    public void done() {

    }

}
