package org.androware.flow.builder;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jkirkley on 9/2/16.
 */
public class StartFlowForm {
    private FileChooserWidget flowFileChooserWidget;
    private JPanel rootPanel;
    private JButton openButton;
    private ClassChooserWidget resourceClassChooser;
    private JTextPane errorMsgTextPane;

    public void init(Project project) {
        flowFileChooserWidget.init(project, "Choose Flow", FileTypeRegistry.getInstance().getFileTypeByExtension(".js"));
        resourceClassChooser.getBrowseClassesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TreeClassChooser treeClassChooser = TreeClassChooserFactory.getInstance(project).createProjectScopeChooser("Choose Resource Class");

                treeClassChooser.showDialog();

                PsiClass psiClass = treeClassChooser.getSelected();

                if (psiClass != null) {
                    PSIclassUtils.resourceClass = psiClass;

                    resourceClassChooser.populate(psiClass.getQualifiedName());
                } else {
                    setErrorMsg("Cannot find Resource class: ");
                }

            }
        });

        //resourceClassChooser.init(project, "Choose Resource Class");
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JButton getOpenButton() {
        return openButton;
    }

    public void setErrorMsg(String msg) {
        errorMsgTextPane.setText(msg);
    }

    public String getFileName() {
        return flowFileChooserWidget.getFileName();
    }

    public String getResourceClassName() {
        return resourceClassChooser.getClassName();
    }

}
