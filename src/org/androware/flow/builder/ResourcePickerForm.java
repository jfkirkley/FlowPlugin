package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.lang.reflect.Field;

/**
 * Created by jkirkley on 8/26/16.
 */
public class ResourcePickerForm implements CRUDForm{
    private JList groupList;
    private JList fieldsList;
    private JPanel rootPanel;


    @Override
    public void init(Project project, ToolWindow toolWindow, Object target, FormAssembler formAssembler) {

    }

    public static class ClassWrap {
        Class clazz;
        ClassWrap(Class clazz){
            this.clazz = clazz;
        }

        public String toString() {
            return clazz.getSimpleName();
        }
    }

    public static class FieldWrap{
        Field field;
        FieldWrap(Field field){
            this.field = field;
        }
        public String toString() {
            return field.getName();
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target) {

        final Class[] groupClasses = ResEx.class.getDeclaredClasses();
        DefaultListModel<ClassWrap> model = new DefaultListModel<>();
        for(Class c: groupClasses){
            model.addElement(new ClassWrap(c));
        }
        groupList.setModel(model);

        groupList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        fieldsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        final DefaultListModel<FieldWrap> fieldsModel = new DefaultListModel<>();
        fieldsList.setModel(fieldsModel);

        groupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()) {
                    Class groupClass = groupClasses[groupList.getSelectedIndex()];

                    fieldsModel.removeAllElements();
                    Field fields[] = groupClass.getFields();
                    for (Field field : fields) {
                        fieldsModel.addElement(new FieldWrap(field));
                    }
                    //fieldsList.
                }
            }
        });

        fieldsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

            }
        });
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Object getTarget() {
        return fieldsList.getSelectedValue();
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(Object object) {

    }
}