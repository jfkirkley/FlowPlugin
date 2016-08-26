package org.androware.flow.builder;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import org.androware.flow.base.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.fileTypes.StdFileTypes.JS;

/**
 * Created by jkirkley on 8/18/16.
 */
public class StepForm {
    private JButton addSaverButton;
    private JButton addCustomizerButton;

    private JPanel rootPanel;
    private JButton editUIButton;
    private JButton twoWayMapperButton;
    private ClassChooserWidget transitionClassChooserPanel;
    private ClassChooserWidget parentContainerClassChooserPanel;
    private ClassChooserWidget processorClassChooserPanel;
    private ComboBoxCRUDForm objectLoadersComboBoxCRUDForm;
    private ComboBoxCRUDForm navsComboBoxCRUDForm;
    private FileChooserWidget targetFlowFileChooserWidget;
    private FileChooserWidget layoutFileChooserWidget;
    private JTextField textField1;

    ToolWindow toolWindow;
    StepBase stepBase;

    public StepForm(final Project project, ToolWindow toolWindow, StepBase stepBase) {
        this.stepBase = stepBase;
        this.toolWindow = toolWindow;

        transitionClassChooserPanel.init(project, "Choose Step Transition Class");
        parentContainerClassChooserPanel.init(project, "Choose Parent Container Class");
        processorClassChooserPanel.init(project, "Choose Step processor class");

        targetFlowFileChooserWidget.init(project, "Choose Target Flow", FileTypeRegistry.getInstance().getFileTypeByExtension(".js"));

        layoutFileChooserWidget.init(project, "Choose Layout", StdFileTypes.XML);

        objectLoadersComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl<ObjectLoaderSpecBase>(project, toolWindow, ObjectLoaderSpecForm.class, ObjectLoaderSpecBase.class), stepBase.objectLoaderSpecs);

        navsComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl<NavBase>(project, toolWindow, NavForm.class, NavBase.class), stepBase.navMap);

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addSaverButton,
                ObjectSaverSpecForm.class, ObjectSaverSpecBase.class, stepBase.objectSaverSpec,
                new CreateObjectListener<ObjectSaverSpecBase>() {
                    @Override
                    public void onCreate(ObjectSaverSpecBase object) {
                        stepBase.objectSaverSpec = object;
                    }
                });

        CompFactory.mkAddEditToggleWidget(project, toolWindow, editUIButton,
                UIform.class, UI.class, stepBase.ui,
                new CreateObjectListener<UI>() {
                    @Override
                    public void onCreate(UI object) {
                        stepBase.ui = object;
                    }
                });

        twoWayMapperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                System.out.println("hello");

            }
        });

    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

}
