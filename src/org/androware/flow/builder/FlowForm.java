package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import jdk.nashorn.internal.scripts.JO;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.*;
import org.androware.flow.plugin.FlowToolWindowFactory;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.androware.flow.builder.PSIclassUtils.fillListWithResourceGroup;


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
    private JButton closeButton;
    private JCheckBox isRootCheckBox;


    ToolWindow toolWindow;

    FlowBase flowBase;

    public FlowForm(final Project project, ToolWindow toolWindow, FlowBase flowBase) {

        CompFactory.setTextfieldVal(nameTextField, flowBase, "name");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(closeButton, "Are you sure you wish to close this flow?");
                System.out.println("answer: " + answer);
                if(answer == JOptionPane.YES_OPTION) {
                    FlowToolWindowFactory.instance.createToolWindowContent(project, toolWindow);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String flowName = nameTextField.getText();
                try {
                    String baseDir = project.getBaseDir().getCanonicalPath();

                    String SRC_DIR = "/app/src/main/";
                    String path = baseDir + SRC_DIR + "res/raw/" + flowName + ".js";

                    System.out.println(path);

                    JsonObjectWriter jsonObjectWriter = new JsonObjectWriter(new FileOutputStream(new File( path )));

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
        PSIclassUtils.fillListWithResourceGroup(layoutList, "layout");
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

        CompFactory.setFieldSetterOnAction(isRootCheckBox, new ReflectionUtils.FieldSetter(flowBase, "isRoot"), flowBase.isRoot);
    }


    public JPanel getRootPanel() {
        return rootPanel;
    }


}
