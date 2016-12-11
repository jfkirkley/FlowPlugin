package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static org.androware.flow.builder.PSIclassUtils.fillListWithResourceGroup;

/**
 * Created by jkirkley on 8/29/16.
 */
public class WidgetIdPickerForm implements CRUDForm {
    private JList layoutList;
    private JList widgetList;
    private JPanel rootPanel;

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target, FormAssembler formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Object target) {
        PSIclassUtils.fillListWithResourceGroup(layoutList, "layout");

        layoutList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layoutList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()) {
                    Object val = layoutList.getSelectedValue();
                    if(val != null) {
                        String layout = val.toString();
                        CompFactory.fillListWithWidgetIdsFromLayout(widgetList, layout);
                    }
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
        //widgetList.setSelectedIndex(-1);
        //layoutList.setSelectedIndex(-1);
        widgetList.clearSelection();
        layoutList.clearSelection();
    }

    @Override
    public void populate(Object object) {

    }

    @Override
    public void done() {

    }
}
