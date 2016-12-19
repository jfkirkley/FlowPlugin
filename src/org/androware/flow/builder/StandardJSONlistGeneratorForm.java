package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import org.androware.flow.base.Field2WidgetIdMapper;
import org.androware.flow.base.ListMapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

/**
 * Created by jkirkley on 12/16/16.
 */
public class StandardJSONlistGeneratorForm implements CRUDForm<ListMapper> {
    private JPanel rootPanel;
    private BeanTree beanTree;
    private JList widgetIdList;
    private SimpleTypeListForm mappingComboForm;
    private JButton setListFieldButton;
    private JTextField listField;

    private ListMapper listMapper;

    private CRUDForm parentForm;

    public class MappingComboNoFormAssembler implements FormAssembler<SimpleTypeListForm> {
        @Override
        public void assemble(Project project, ToolWindow toolWindow, SimpleTypeListForm form) {
            if(listMapper.listItemField2WidgetIdMappers != null) {
                for(Field2WidgetIdMapper field2WidgetIdMapper: listMapper.listItemField2WidgetIdMappers) {
                    form.getTypeListComboBox().addItem(field2WidgetIdMapper);
                }
            }

            form.getAddButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object widgetId = widgetIdList.getSelectedValue();


                    Object node = beanTree.getSelectedNode();

                    if( node != null && node instanceof PsiField) {
                        PsiField psiField = (PsiField) node;
                        String fieldName = PSIclassUtils.getFieldIdentifier(psiField);
                        Field2WidgetIdMapper field2WidgetIdMapper = new Field2WidgetIdMapper(fieldName, (String)widgetId);
                        listMapper.listItemField2WidgetIdMappers.add(field2WidgetIdMapper);
                        form.getTypeListComboBox().addItem(field2WidgetIdMapper);
                    } else {
                        JOptionPane.showMessageDialog(rootPanel, "No field selected in the bean tree!");
                    }
                }
            });
        }
    }

    public class ThisFormAssembler implements FormAssembler<StandardJSONlistGeneratorForm> {
        @Override
        public void assemble(Project project, ToolWindow toolWindow, StandardJSONlistGeneratorForm form) {

        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ListMapper target, FormAssembler formAssembler, CRUDForm parentForm) {
        this.parentForm = parentForm;
        AdapterViewSpecForm adapterViewSpecForm = (AdapterViewSpecForm) parentForm;
        PSIclassUtils.PsiFieldWrap fieldWrap = (PSIclassUtils.PsiFieldWrap)adapterViewSpecForm.getLayoutList().getSelectedValue();
        PsiClass beanClass = adapterViewSpecForm.getBeanClass();
        System.out.println("bc: " + beanClass);
        if(fieldWrap != null && beanClass != null) {
            init(project, toolWindow, target, fieldWrap.toString(), beanClass);
        } else {
            JOptionPane.showMessageDialog(rootPanel, "You need to select an item layout and a bean id.");
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ListMapper target, FormAssembler formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ListMapper target) {
        this.listMapper = target;
        if(listMapper.listItemField2WidgetIdMappers == null) {
            listMapper.listItemField2WidgetIdMappers = new ArrayList<>();
        }
        if(listMapper.listFieldName != null) {
            listField.setText(listMapper.listFieldName);
        }
        mappingComboForm.init(project, toolWindow, target.listItemField2WidgetIdMappers, new MappingComboNoFormAssembler());

    }

    public void init(Project project, ToolWindow toolWindow, ListMapper target, String listItemLayoutId, PsiClass beanClass) {
        this.init(project, toolWindow, target);
        beanTree.init(beanClass);
        CompFactory.fillListWithWidgetIdsFromLayout(widgetIdList, listItemLayoutId);

        setListFieldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Object node = beanTree.getSelectedNode();

                if( node != null && node instanceof PsiField) {
                    PsiField psiField = (PsiField) node;
                    if(psiField.getType().toString().contains("List<")) {
                        String fieldName = PSIclassUtils.getFieldIdentifier(psiField);
                        listField.setText(fieldName);
                        listMapper.listFieldName = fieldName;
                    } else {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a list type from the bean tree.");
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPanel, "Please select a list type from the bean tree.");
                }
            }
        });

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public ListMapper getTarget() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(ListMapper object) {

    }

    @Override
    public void done() {
        parentForm.handleChildValue(listMapper);
    }

    @Override
    public void handleChildValue(Object childValue) {

    }
}
