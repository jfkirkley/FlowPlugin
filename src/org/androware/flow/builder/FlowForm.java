package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.*;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by jkirkley on 8/31/16.
 */
public class FlowForm {
    private JList layoutList;
    private ComboBoxCRUDForm stepsComboBoxCRUDForm;
    private ComboBoxCRUDForm objectLoadersComboBoxCRUDForm;
    private ClassChooserWidget processorClassChooser;
    private JList fragmentContainerList;
    private JButton stepGenButton;
    private JButton startNavButton;

    private JPanel rootPanel;
    private JTextField nameTextField;
    private JButton saveButton;


    ToolWindow toolWindow;

    FlowBase flowBase;

    public FlowForm(final Project project, ToolWindow toolWindow, FlowBase flowBase) {

        CompFactory.setTextfieldVal(nameTextField, flowBase, "name");


        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String flowName = nameTextField.getText();
                try {
                    JsonObjectWriter jsonObjectWriter = new JsonObjectWriter(new FileOutputStream(new File( Utils.HACK_ROOT_DIR + "_res_raw_" + flowName + ".js")));

                    jsonObjectWriter.write(flowBase);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.toolWindow = toolWindow;
        this.flowBase = flowBase;

        processorClassChooser.init(project, "Choose Flow processor class", new ReflectionUtils.FieldSetter(flowBase, "processor"));

        layoutList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        CompFactory.fillListWithResourceGroup(layoutList, "layout");
        CompFactory.setFieldSetterOnSelect(layoutList, new ReflectionUtils.FieldSetter(flowBase, "layout"), flowBase.layout);

        fragmentContainerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        CompFactory.setFieldSetterOnSelect(fragmentContainerList, new ReflectionUtils.FieldSetter(flowBase, "fragmentContainer"), null);

        layoutList.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent listSelectionEvent) {
                        Object val = layoutList.getSelectedValue();
                        if(val != null) {
                            String layout = val.toString();
                            CompFactory.fillListWithWidgetIdsFromLayout(fragmentContainerList, layout);
                        }
                    }
                });

        objectLoadersComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<ObjectLoaderSpecBase>(project, toolWindow, ObjectLoaderSpecForm.class, ObjectLoaderSpecBase.class),
                new ReflectionUtils.FieldSetter(flowBase, "objectLoaderSpecs")
        );

        stepsComboBoxCRUDForm.init(
                project,
                new CompFactory.DefaultCRUDEditorImpl<StepBase>(project, toolWindow, StepForm.class, StepBase.class),
                new ReflectionUtils.FieldSetter(flowBase, "steps")
        );

        CompFactory.mkAddEditToggleWidget(project, toolWindow, stepGenButton,
                ConstructorSpecForm.class, ConstructorSpec.class, new ReflectionUtils.FieldSetter(flowBase, "stepGeneratorSpec"));

        CompFactory.mkAddEditToggleWidget(project, toolWindow, startNavButton,
                NavForm.class, NavBase.class, new ReflectionUtils.FieldSetter(flowBase, "startNav"),
                new NavForm.NavFormAssembler(flowBase, null)
        );

    }


    public JPanel getRootPanel() {
        return rootPanel;
    }


}
