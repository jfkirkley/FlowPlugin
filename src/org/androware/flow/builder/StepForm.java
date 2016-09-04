package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.androware.androbeans.utils.ReflectionUtils.ensureFieldExists;
import static org.androware.flow.builder.PSIclassUtils.fillComboWithResourceGroup;
import static org.androware.flow.builder.PSIclassUtils.setComboItemWithResourceGroupField;
import static org.androware.flow.builder.ResEx.attr.layout;

/**
 * Created by jkirkley on 8/18/16.
 */
public class StepForm implements CRUDForm<StepBase> {

    private JButton addCustomizerButton;

    private JPanel rootPanel;


    private ClassChooserWidget transitionClassChooserPanel;
    private ClassChooserWidget parentContainerClassChooserPanel;
    private ClassChooserWidget processorClassChooserPanel;
    private ComboBoxCRUDForm objectLoadersComboBoxCRUDForm;
    private ComboBoxCRUDForm navsComboBoxCRUDForm;


    private ComboBoxCRUDForm adapterViewComboBoxCRUDForm;
    private ObjectSaverSpecForm objectSaverSpecForm;
    private JComboBox layoutComboBox;
    private TwoWayMapperForm twoWayMapperForm;

    private JComboBox targetFlowComboBox;
    private JTextField nameTextField;
    private JList parentContainerIDList;


    ToolWindow toolWindow;
    StepBase stepBase;
    FlowBase flowBase;

    public StepForm() {

    }


    public void init(Project project, ToolWindow toolWindow, StepBase stepBase, FlowBase flowBase) {

        this.stepBase = stepBase;
        this.toolWindow = toolWindow;
        this.flowBase = flowBase;

        transitionClassChooserPanel.init(project, "Choose Step Transition Class", new ReflectionUtils.FieldSetter(stepBase, "transitionClassName"));

        if(flowBase.layout != null) {
            parentContainerIDList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            CompFactory.fillListWithWidgetIdsFromLayout(parentContainerIDList, flowBase.layout);
            CompFactory.setFieldSetterOnSelect(parentContainerIDList, new ReflectionUtils.FieldSetter(stepBase, "parentContainer"), stepBase.parentContainer);
        }

        processorClassChooserPanel.init(project, "Choose Step processor class", new ReflectionUtils.FieldSetter(stepBase, "processor"));

        CompFactory.setTextfieldVal(nameTextField, stepBase, "name");


        objectLoadersComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<ObjectLoaderSpecBase>(project, toolWindow, ObjectLoaderSpecForm.class, ObjectLoaderSpecBase.class),
                new ReflectionUtils.FieldSetter(stepBase, "objectLoaderSpecs")
        );

        navsComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<NavBase>(project, toolWindow, NavForm.class, NavBase.class, null, null, new NavForm.NavFormAssembler(flowBase, stepBase)),
                new ReflectionUtils.FieldSetter(stepBase, "navMap")
        );

        adapterViewComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<AdapterViewSpec>(project, toolWindow, AdapterViewSpecForm.class, AdapterViewSpec.class, null, null, new AdapterViewSpecForm.ThisFormAssembler(flowBase, stepBase)),
                new ReflectionUtils.FieldSetter(new ReflectionUtils.FieldSetter(stepBase, "ui"), "adapterViews")
        );

        objectSaverSpecForm.init(project, toolWindow, (ObjectSaverSpecBase)ReflectionUtils.ensureFieldExists(stepBase, "objectSaverSpec"));

        setUpCombo(layoutComboBox, "layout", "layout", stepBase.layout);
        setUpCombo(targetFlowComboBox, "raw", "targetFlow", stepBase.targetFlow);

        if(stepBase.twoWayMapper == null) {
            stepBase.twoWayMapper = new TwoWayMapperBase(new HashMap());
        }

        twoWayMapperForm.init(project, toolWindow, stepBase.twoWayMapper.componentId2BeanFieldMap, new TwoWayMapperForm.ThisFormAssembler(flowBase, stepBase, layoutComboBox));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addCustomizerButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(stepBase, "viewCustomizerSpec"));


    }

    public void setUpCombo(JComboBox jComboBox, String resGroup, String name, Object value) {
        PSIclassUtils.fillComboWithResourceGroup(jComboBox, resGroup);

        if(value != null) {
            PSIclassUtils.setComboItemWithResourceGroupField(jComboBox, resGroup, (String)value);
            //jComboBox.setSelectedItem(new CompFactory.FieldWrap(ReflectionUtils.getField(StepBase.class, name));
        } else {
            jComboBox.setSelectedIndex(-1);
        }
        CompFactory.setFieldSetterOnAction(jComboBox, new ReflectionUtils.FieldSetter(stepBase, name));

    }


    @Override
    public void init(Project project, ToolWindow toolWindow, StepBase target, FormAssembler formAssembler) {

    }


    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public StepBase getTarget() {
        stepBase.name = nameTextField.getText();
        return stepBase;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(StepBase object) {

    }

    @Override
    public void done() {
        MainForm.mainForm.addStep(getTarget());
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, StepBase target) {
        init(project, toolWindow, target, FlowBase.currFlowBase);
    }

}
