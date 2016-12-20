package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.aop.AOP;
import org.androware.flow.base.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.androware.flow.builder.ResEx.attr.layout;

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

    public Map<String,String> beanRegistry;
    private JList beanIdList;

    private JTextField nameTextField;
    private JButton addBeanItemGenButton;

    AdapterViewSpec adapterViewSpec;

    private Project project;
    public StepBase myStepBase;

    public static class ThisFormAssembler implements FormAssembler<AdapterViewSpecForm> {
        StepBase stepBase;
        FlowBase flowBase;

        public ThisFormAssembler(FlowBase flowBase, StepBase stepBase) {
            this.stepBase = stepBase;
            this.flowBase = flowBase;
        }


        @Override
        public void assemble(Project project, ToolWindow toolWindow, AdapterViewSpecForm form) {
            form.myStepBase = stepBase;

            String layout = stepBase.layout;
            AdapterViewSpec adapterViewSpec = form.getTarget();
            if (layout != null) {
                CompFactory.fillComboWidthWdgetIdsFromLayout(form.getViewIDComboBox(), layout);
                CompFactory.setFieldSetterOnAction(form.getViewIDComboBox(), new ReflectionUtils.FieldSetter(adapterViewSpec, "viewId"), adapterViewSpec.viewId);
            }
            form.getLayoutList().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            PSIclassUtils.fillListWithResourceGroup(form.getLayoutList(), "layout");
            CompFactory.setFieldSetterOnSelect(
                    form.getLayoutList(),
                    new ReflectionUtils.FieldSetter(adapterViewSpec, "itemLayoutId"),
                    adapterViewSpec.itemLayoutId != null? new PSIclassUtils.PsiFieldWrap("layout", adapterViewSpec.itemLayoutId): null
            );

            try {
                form.getBeanIdList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                form.beanRegistry = flowBase.buildRegistry(stepBase);
                CompFactory.fillJList(form.getBeanIdList(), new ArrayList<>(form.beanRegistry.keySet()));
                CompFactory.setFieldSetterOnSelect(form.getBeanIdList(), AOP.t(ReflectionUtils.FieldSetter.class, adapterViewSpec, "beanIds"), adapterViewSpec.beanIds);
            } catch (Exception e) {
                e.printStackTrace();
            }

            CompFactory.setFieldSetterOnAction(form.getUseDefaultCheckBox(), new ReflectionUtils.FieldSetter(adapterViewSpec, "useDefault"), adapterViewSpec.useDefault);

            CompFactory.setTextfieldVal(form.getNameTextField(), adapterViewSpec, "name");
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target, FormAssembler formAssembler, CRUDForm parentForm) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target, FormAssembler formAssembler) {
        init(project, toolWindow, target);
        formAssembler.assemble(project, toolWindow, this);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target) {
        adapterViewSpec =  target;
        this.project = project;
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
        itemsComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl(project, toolWindow, MapForm.class, HashMap.class, null, new CompFactory.CopyBuilder(defItemMap)), adapterViewSpec.items);//new ArrayList<Map>());

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addConstructorSpecButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(target, "adapterConstructorSpec"));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, itemGeneratorButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(target, "itemGeneratorSpec"));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addBeanItemGenButton,
                StandardJSONlistGeneratorForm.class, ListMapper.class, null, null, this);

        //form.init(project, toolWindow, new ListMapper(), "pref_list_item", StepBase.class);


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
        if(adapterViewSpec.name != null ) {
            CompFactory.ensureCorrectMapKey(myStepBase.ui, "adapterViews", adapterViewSpec, adapterViewSpec.toString());
        }
    }

    @Override
    public void handleChildValue(Object childValue) {
        if(childValue instanceof ListMapper) {
            ListMapper listMapper = (ListMapper) childValue;

            // 1) build ItemGeneratorSpec for BeanListFieldItemGenerator
            adapterViewSpec.itemGeneratorSpec = new ConstructorSpec();

            adapterViewSpec.itemGeneratorSpec.targetClassName = "org.androware.flow.base.BeanListFieldItemGenerator";
            adapterViewSpec.itemGeneratorSpec.paramClassNames = new ArrayList<>();
            adapterViewSpec.itemGeneratorSpec.paramClassNames.add("java.lang.String");
            adapterViewSpec.itemGeneratorSpec.paramClassNames.add("java.lang.String");
            adapterViewSpec.itemGeneratorSpec.paramObjects = new Object[2];
            adapterViewSpec.itemGeneratorSpec.paramObjects[0] = beanIdList.getSelectedValue();
            adapterViewSpec.itemGeneratorSpec.paramObjects[1] = listMapper.listFieldName;

            // 2) add Field2WidgetIdMapper's to the step twoWayMapper using 'currItem' key
            if( myStepBase.twoWayMapper == null ) {
                myStepBase.twoWayMapper = new TwoWayMapperBase(new HashMap());
            }
            for (Field2WidgetIdMapper field2WidgetIdMapper: listMapper.listItemField2WidgetIdMappers) {
                myStepBase.twoWayMapper.componentId2BeanFieldMap.put(field2WidgetIdMapper.widgetId, "currItem." + field2WidgetIdMapper.fieldName);
            }

            // 3) create ConstructorSpec for BindingArrayAdapter
            adapterViewSpec.adapterConstructorSpec = new ConstructorSpec();
            adapterViewSpec.adapterConstructorSpec.targetClassName = "org.androware.flow.binding.BindingArrayAdapter";
            adapterViewSpec.adapterConstructorSpec.paramClassNames = new ArrayList<>();

            //"paramClassNames": [  "android.app.Activity", "java.util.List", "org.androware.flow.Step", "org.androware.flow.base.AdapterViewSpec"],
            adapterViewSpec.adapterConstructorSpec.paramClassNames.add("android.app.Activity");
            adapterViewSpec.adapterConstructorSpec.paramClassNames.add("java.util.List");
            adapterViewSpec.adapterConstructorSpec.paramClassNames.add("org.androware.flow.Step");
            adapterViewSpec.adapterConstructorSpec.paramClassNames.add("org.androware.flow.base.AdapterViewSpec");
            adapterViewSpec.adapterConstructorSpec.paramObjects = new Object[4];

            //"paramObjects": ["__plugin__context", "__plugin__items", "__plugin__step", "__plugin__adapter_spec" ]
            adapterViewSpec.adapterConstructorSpec.paramObjects[0] = "__plugin__context";
            adapterViewSpec.adapterConstructorSpec.paramObjects[1] = "__plugin__items";
            adapterViewSpec.adapterConstructorSpec.paramObjects[2] = "__plugin__step";
            adapterViewSpec.adapterConstructorSpec.paramObjects[3] = "__plugin__adapter_spec";

        }
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


    public PsiClass getBeanClass() {
        String beanId = (String)beanIdList.getSelectedValue();
        if(beanId != null) {
            String className = (String)beanRegistry.get(beanId);
            System.out.println("cl: " + className);
            return PSIclassUtils.getClass(className);
        }
        return null;
    }
}
