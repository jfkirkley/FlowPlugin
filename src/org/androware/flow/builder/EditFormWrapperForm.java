package org.androware.flow.builder;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jkirkley on 8/20/16.
 */
public class EditFormWrapperForm {
    private JButton doneButton;
    private JButton cancelButton;
    private JPanel rootPanel;
    private JPanel contentPanel;


    public <T> void init(Project project, ToolWindow toolWindow, JPanel editorPanel, final CompFactory.CRUDObjectEditor<T> crudObjectEditor ) {

        final Content[] previousContents = toolWindow.getContentManager().getContents();

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                crudObjectEditor.done();
                toolWindow.getContentManager().removeAllContents(false);
                for(Content content: previousContents) {
                    toolWindow.getContentManager().addContent(content);
                }
                crudObjectEditor.done();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                crudObjectEditor.cancel();
                toolWindow.getContentManager().removeAllContents(false);
                for(Content content: previousContents) {
                    toolWindow.getContentManager().addContent(content);
                }
            }
        });

        contentPanel.add(editorPanel);

        toolWindow.getContentManager().removeAllContents(false);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(rootPanel, "", false);
        toolWindow.getContentManager().addContent(content);

    }
}
