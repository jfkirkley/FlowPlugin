package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by jkirkley on 8/29/16.
 */
public class WidgetIdPickerForm implements CRUDForm{
    private JList layoutList;
    private JList widgetList;
    private JPanel rootPanel;

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target, FormAssembler formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target) {
        CompFactory.fillListWithResourceGroup(layoutList, "layout");

        layoutList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layoutList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()) {
                    String layout = layoutList.getSelectedValue().toString();
                    CompFactory.fillListWithWidgetIdsFromLayout(widgetList, layout);
                }
            }
        });

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Object getTarget() {
        return widgetList.getSelectedValue();
    }

    @Override
    public void clear() {
        widgetList.setSelectedIndex(-1);
        layoutList.setSelectedIndex(-1);
    }

    @Override
    public void populate(Object object) {

    }

    @Override
    public void done() {

    }
}
