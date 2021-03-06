package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.StepBase;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static groovy.xml.Entity.reg;

/**
 * Created by jkirkley on 8/26/16.
 */
public class TwoWayMapperForm implements CRUDForm<Map> {
    private SimpleTypeListForm simpleTypeListForm;
    private JPanel rootPanel;
    private JList widgetIDList;

    private JList beanList;
    private JList fieldList;
    public static final String KEY_VALUE_SEPERATOR = " --> ";

    Map map;

    public static class ThisFormAssembler implements FormAssembler<TwoWayMapperForm> {
        StepBase stepBase;
        FlowBase flowBase;
        JComboBox stepLayoutCombo;
        public ThisFormAssembler(FlowBase flowBase, StepBase stepBase, JComboBox stepLayoutCombo) {
            this.stepBase = stepBase;
            this.flowBase = flowBase;
            this.stepLayoutCombo = stepLayoutCombo;
        }


        @Override
        public void assemble(Project project, ToolWindow toolWindow, TwoWayMapperForm form) {
            String layout = stepBase.layout;
            if (layout != null) {
                CompFactory.fillListWithWidgetIdsFromLayout(form.getWidgetIDList(), layout);
            }
            stepLayoutCombo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String layout = stepLayoutCombo.getSelectedItem().toString();
                    CompFactory.fillListWithWidgetIdsFromLayout(form.getWidgetIDList(), layout);
                }
            });

            //Map<String, String> registry = flowBase.buildRegistry(stepBase);
            CompFactory.fillJList(form.getBeanList(), new ArrayList<>(flowBase.buildRegistry(stepBase).keySet()));

            form.getBeanList().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {
                    if (!listSelectionEvent.getValueIsAdjusting()) {
                        String beanId = (String)form.getBeanList().getSelectedValue();

                        Map<String, String> registry = flowBase.buildRegistry(stepBase);
                        System.out.println(beanId + " : " + registry);
                        if(registry.containsKey(beanId)) {  // SWING IS FUCKING USELESS!!!!!!!!!!!!!!!!!!!!!!
                            //Class beanClass = ReflectionUtils.getClass(registry.get(beanId));


                            PsiClass psibeanClass = PSIclassUtils.getClass(registry.get(beanId));
                            PSIclassUtils.fillListWithAllClassMembers(form.getFieldList(), psibeanClass);
                            //CompFactory.fillListWithAllClassMembers(form.getFieldList(), beanClass);
                        }
                    }
                }
            });

            form.getSimpleTypeListForm().getAddButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String widgetId = (String)form.getWidgetIDList().getSelectedValue();
                    String beanId = (String)form.getBeanList().getSelectedValue();
                    CompFactory.ObjectWrap objectWrap = (CompFactory.ObjectWrap)form.getFieldList().getSelectedValue();

                    if(widgetId != null && beanId != null && objectWrap != null) {
                        form.addMappedItem(widgetId, beanId + "." + objectWrap.toString());
                        //form.getTarget().put(widgetId, beanId + "." + objectWrap.toString());
                        //form.getSimpleTypeListForm().getTypeListComboBox().addItem(widgetId + KEY_VALUE_SEPERATOR + beanId + "." + objectWrap.toString());
                    }
                }
            });

            form.getSimpleTypeListForm().getDeleteButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JComboBox comboBox = form.getSimpleTypeListForm().getTypeListComboBox();
                    String item = (String)comboBox.getSelectedItem();
                    String tks[] = item.split(KEY_VALUE_SEPERATOR);
                    form.getTarget().remove(tks[0]);

                }
            });
        }
    }

    public void addMappedItem(String widgetId, String beanSpec) {
        map.put(widgetId, beanSpec);
        simpleTypeListForm.getTypeListComboBox().addItem(widgetId + KEY_VALUE_SEPERATOR + beanSpec);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target, FormAssembler formAssembler, CRUDForm parentForm) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target, FormAssembler formAssembler) {
        this.map = target;

        simpleTypeListForm.init(project, toolWindow, null, null);
        formAssembler.assemble(project, toolWindow, this);

        for(Object k: map.keySet()) {
            addMappedItem((String)k, (String)map.get(k));
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map target) {

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Map getTarget() {
        return map;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(Map object) {

    }

    public JList getWidgetIDList() {
        return widgetIDList;
    }

    public JList getBeanList() {
        return beanList;
    }

    public JList getFieldList() {
        return fieldList;
    }

    public SimpleTypeListForm getSimpleTypeListForm() {
        return simpleTypeListForm;
    }


    @Override
    public void done() {

    }

    @Override
    public void handleChildValue(Object childValue) {

    }
}
