package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.aop.AOP;
import org.androware.aop.NotifyAspect;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/20/16.
 */
public class ComboBoxCRUDForm<T> {
    public JComboBox getComboBox() {
        return comboBox;
    }


    private JComboBox comboBox;
    private JButton addMutton;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel rootPanel;

    public CompFactory.JComboBoxCRUDWrapper getjComboBoxCRUDWrapper() {
        return jComboBoxCRUDWrapper;
    }

    private CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper;

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper) {
        this.jComboBoxCRUDWrapper = jComboBoxCRUDWrapper;
        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, ReflectionUtils.FieldSetter fieldSetter, CompFactory.JComboBoxCRUDWrapper.Listener listener) {
        jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, fieldSetter, listener, false);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, ReflectionUtils.FieldSetter fieldSetter, boolean useEntries) {
        //CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, fieldSetter, useEntries);
        try {
            jComboBoxCRUDWrapper = AOP.t(CompFactory.JComboBoxCRUDWrapper.class, comboBox, fieldSetter, useEntries);

            CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, ReflectionUtils.FieldSetter fieldSetter) {
        jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, fieldSetter);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }


    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, List<T> items) {
        jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, items);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, Map<String, T> itemMap) {
        jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, itemMap);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, T[] items) {
        jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, Arrays.asList(items));

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

}
