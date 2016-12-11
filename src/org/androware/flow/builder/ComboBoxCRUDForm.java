package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import org.androware.androbeans.utils.ReflectionUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/20/16.
 */
public class ComboBoxCRUDForm <T>{
    public JComboBox getComboBox() {
        return comboBox;
    }

    private JComboBox comboBox;
    private JButton addMutton;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel rootPanel;

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, ReflectionUtils.FieldSetter fieldSetter, CompFactory.JComboBoxCRUDWrapper.Listener listener) {
        CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, fieldSetter, listener);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, ReflectionUtils.FieldSetter fieldSetter) {
        CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, fieldSetter);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }


    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, List<T> items) {
        CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, items);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, Map<String, T> itemMap) {
        CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, itemMap);

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);
    }

    public void init(Project project, CompFactory.CRUDObjectEditor<T> crudObjectEditor, T[] items) {
        CompFactory.JComboBoxCRUDWrapper jComboBoxCRUDWrapper = new CompFactory.JComboBoxCRUDWrapper(comboBox, Arrays.asList(items));

        CompFactory.addCRUDWrapper(project, crudObjectEditor, editButton, addMutton, deleteButton, jComboBoxCRUDWrapper);


    }

}
