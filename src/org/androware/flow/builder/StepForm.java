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
import static org.androware.flow.builder.ResEx.attr.layout;

/**
 * Created by jkirkley on 8/18/16.
 */
public class StepForm {

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


    ToolWindow toolWindow;
    StepBase stepBase;
    FlowBase flowBase;
    public StepForm(final Project project, ToolWindow toolWindow, StepBase stepBase, FlowBase flowBase) {

        this.stepBase = stepBase;
        this.toolWindow = toolWindow;
        this.flowBase = flowBase;

        transitionClassChooserPanel.init(project, "Choose Step Transition Class", new ReflectionUtils.FieldSetter(stepBase, "transitionClassName"));
        parentContainerClassChooserPanel.init(project, "Choose Parent Container Class", new ReflectionUtils.FieldSetter(stepBase, "parentContainer"));
        processorClassChooserPanel.init(project, "Choose Step processor class", new ReflectionUtils.FieldSetter(stepBase, "processor"));


        objectLoadersComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<ObjectLoaderSpecBase>(project, toolWindow, ObjectLoaderSpecForm.class, ObjectLoaderSpecBase.class),
                (List)ensureFieldExists(stepBase, "objectLoaderSpecs")
        );

        navsComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<NavBase>(project, toolWindow, NavForm.class, NavBase.class, null, null, new NavForm.NavFormAssembler(flowBase, stepBase)),
                (Map)ensureFieldExists(stepBase, "navMap")
        );

        adapterViewComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<AdapterViewSpec>(project, toolWindow, AdapterViewSpecForm.class, AdapterViewSpec.class),
                (Map)ReflectionUtils.ensureFieldExists(ReflectionUtils.ensureFieldExists(stepBase, "ui"), "adapterViews")
        );

        objectSaverSpecForm.init(project, toolWindow, (ObjectSaverSpecBase)ReflectionUtils.ensureFieldExists(stepBase, "objectSaverSpec"));

        setUpCombo(layoutComboBox, "layout", "layout", stepBase.layout);
        setUpCombo(targetFlowComboBox, "raw", "targetFlow", stepBase.targetFlow);


        if(stepBase.twoWayMapper == null) {
            stepBase.twoWayMapper = new TwoWayMapperBase(new HashMap());
        }

        twoWayMapperForm.init(project, toolWindow, stepBase.twoWayMapper.componentId2BeanFieldMap, new TwoWayMapperForm.ThisFormAssembler(flowBase, stepBase));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addCustomizerButton,
                ConstructorSpecForm.class, ConstructorSpec.class, stepBase.viewCustomizerSpec,
                new CreateObjectListener<ConstructorSpec>() {
                    @Override
                    public void onCreate(ConstructorSpec object) {

                        stepBase.viewCustomizerSpec = object;
                    }
                });

    }

    public void setUpCombo(JComboBox jComboBox, String resGroup, String name, Object value) {
        CompFactory.fillComboWithResourceGroup(jComboBox, resGroup);

        if(value != null) {
            CompFactory.setComboItemWithResourceGroupField(jComboBox, resGroup, (String)value);
            //jComboBox.setSelectedItem(new CompFactory.FieldWrap(ReflectionUtils.getField(StepBase.class, name));
        } else {
            jComboBox.setSelectedIndex(-1);
        }
        CompFactory.setFieldSetterOnAction(jComboBox, new ReflectionUtils.FieldSetter(stepBase, name));

    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

}
