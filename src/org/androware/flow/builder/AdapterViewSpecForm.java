package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.AdapterViewSpec;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.StepBase;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 8/22/16.
 */
public class AdapterViewSpecForm implements CRUDForm<AdapterViewSpec> {
    private JComboBox viewIDComboBox;

    private JCheckBox useDefaultCheckBox;

    private ComboBoxCRUDForm itemsComboBoxCRUDForm;
    private JButton itemGeneratorButton;
    private JPanel rootPanel;
    private JButton addConstructorSpecButton;

    private JList layoutList;


    private JList beanIdList;

    private JTextField nameTextField;

    AdapterViewSpec adapterViewSpec;

    public static class ThisFormAssembler implements FormAssembler<AdapterViewSpecForm> {
        StepBase stepBase;
        FlowBase flowBase;

        public ThisFormAssembler(FlowBase flowBase, StepBase stepBase) {
            this.stepBase = stepBase;
            this.flowBase = flowBase;
        }


        @Override
        public void assemble(Project project, ToolWindow toolWindow, AdapterViewSpecForm form) {
            String layout = stepBase.layout;
            AdapterViewSpec adapterViewSpec = form.getTarget();
            if (layout != null) {
                CompFactory.fillComboWidthWdgetIdsFromLayout(form.getViewIDComboBox(), layout);
                CompFactory.setFieldSetterOnAction(form.getViewIDComboBox(), new ReflectionUtils.FieldSetter(adapterViewSpec, "viewId"), adapterViewSpec.viewId);
            }
            form.getLayoutList().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            CompFactory.fillListWithResourceGroup(form.getLayoutList(), "layout");
            CompFactory.setFieldSetterOnSelect(
                    form.getLayoutList(),
                    new ReflectionUtils.FieldSetter(adapterViewSpec, "itemLayoutId"),
                    adapterViewSpec.itemLayoutId != null? new CompFactory.FieldWrap("layout", adapterViewSpec.itemLayoutId): null
            );

            form.getBeanIdList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            Map<String, String> registry = flowBase.buildRegistry(stepBase);
            CompFactory.fillJList(form.getBeanIdList(), new ArrayList<>(registry.keySet()));
            CompFactory.setFieldSetterOnSelect(form.getBeanIdList(), new ReflectionUtils.FieldSetter(adapterViewSpec, "beanIds"), adapterViewSpec.beanIds);

            CompFactory.setFieldSetterOnAction(form.getUseDefaultCheckBox(), new ReflectionUtils.FieldSetter(adapterViewSpec, "useDefault"), adapterViewSpec.useDefault);

            CompFactory.setTextfieldVal(form.getNameTextField(), adapterViewSpec, "name");
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target, FormAssembler formAssembler) {
        init(project, toolWindow, target);
        formAssembler.assemble(project, toolWindow, this);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target) {
        adapterViewSpec =  target;

        if(adapterViewSpec.beanIds == null) {
            adapterViewSpec.beanIds = new ArrayList<String>();
        }

        Map defItemMap = new HashMap<String, Object>();

        defItemMap.put("target", null);
        defItemMap.put("label", null);
        defItemMap.put("labelFieldId", null);

        if(adapterViewSpec.items == null){
            adapterViewSpec.items = new ArrayList();
        }
        itemsComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl(project, toolWindow, MapForm.class, HashMap.class, null, defItemMap), adapterViewSpec.items);//new ArrayList<Map>());

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addConstructorSpecButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(target, "adapterConstructorSpec"));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, itemGeneratorButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(target, "itemGeneratorSpec"));

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public AdapterViewSpec getTarget() {
        return adapterViewSpec;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(AdapterViewSpec object) {

    }

    @Override
    public void done() {
        CompFactory.setValFromTextfield(nameTextField, adapterViewSpec, "name");
    }

    public JComboBox getViewIDComboBox() {
        return viewIDComboBox;
    }

    public JCheckBox getUseDefaultCheckBox() {
        return useDefaultCheckBox;
    }

    public ComboBoxCRUDForm getItemsComboBoxCRUDForm() {
        return itemsComboBoxCRUDForm;
    }

    public JButton getItemGeneratorButton() {
        return itemGeneratorButton;
    }

    public JButton getAddConstructorSpecButton() {
        return addConstructorSpecButton;
    }

    public AdapterViewSpec getAdapterViewSpec() {
        return adapterViewSpec;
    }

    public JList getLayoutList() {
        return layoutList;
    }

    public JList getBeanIdList() {
        return beanIdList;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

}
