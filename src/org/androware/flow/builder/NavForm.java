package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.NavBase;
import org.androware.flow.base.StepBase;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;


import static org.androware.flow.builder.CompFactory.setComboVal;
import static org.androware.flow.builder.CompFactory.setFieldFromComboVal;
import static org.androware.flow.builder.CompFactory.setFieldSetterOnAction;
import static org.androware.flow.builder.PSIclassUtils.setComboItemWithResourceGroupField;


/**
 * Created by jkirkley on 8/18/16.
 */
public class NavForm  implements CRUDForm<NavBase> {
    private JCheckBox useseStepGeneratorCheckBox;

    public static String eventNames[] = {"onClick", "onItemClick", "swipeLeft", "swipeRight"};

    private JComboBox targetComboBox;
    private JComboBox animInComboBox;
    private JComboBox animOutComboBox;

    public JPanel getNavPanel() {
        return navPanel;
    }

    private JPanel navPanel;
    private JComboBox eventComboBox;

    private JComboBox widgetIdComboBox;

    private JComboBox adapterNameComboBox;
    private JCheckBox useListAdapterCheckBox;
    private CRUDForm parentForm;
    NavBase target;

    public static class NavFormAssembler implements FormAssembler<NavForm> {
        StepBase stepBase;
        FlowBase flowBase;


        public NavFormAssembler(FlowBase flowBase, StepBase stepBase) {
            this.stepBase = stepBase;
            this.flowBase = flowBase;
        }

        @Override
        public void assemble(Project project, ToolWindow toolWindow, NavForm form) {
            if(stepBase != null) {
                String layout = stepBase.layout;
                if (layout != null) {
                    CompFactory.fillComboWidthWdgetIdsFromLayout(form.getWidgetIdComboBox(), layout);
                }
            } else {
                form.getWidgetIdComboBox().setEnabled(false);
            }
            CompFactory.fillCombo(form.getTargetComboBox(), new ArrayList<String>(flowBase.steps.keySet()));
            if(stepBase.ui != null && stepBase.ui.adapterViews != null) {
                CompFactory.fillCombo(form.getAdapterNameComboBox(), new ArrayList<String>(stepBase.ui.adapterViews.keySet()));
            }
        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, NavBase target, FormAssembler formAssembler, CRUDForm parentForm) {
        this.parentForm = parentForm;
        init(project, toolWindow, target, formAssembler);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, NavBase target, FormAssembler formAssembler) {
        formAssembler.assemble(project, toolWindow, this);
        init(project, toolWindow, target);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, NavBase target) {
        this.target = target;
        PSIclassUtils.fillComboWithResourceGroup(animInComboBox, "anim");
        PSIclassUtils.fillComboWithResourceGroup(animOutComboBox, "anim");
        CompFactory.fillCombo(eventComboBox, Arrays.asList(eventNames));
        populate(target);
    }

    @Override
    public JPanel getRootPanel() {
        return navPanel;
    }

    @Override
    public NavBase getTarget() {


        return target;
    }

    @Override
    public void clear() {
        eventComboBox.setSelectedIndex(-1);
        animInComboBox.setSelectedIndex(-1);
        animOutComboBox.setSelectedIndex(-1);
        widgetIdComboBox.setSelectedIndex(-1);
        targetComboBox.setSelectedIndex(-1);
        useseStepGeneratorCheckBox.setSelected(false);
    }

    @Override
    public void populate(NavBase object) {
        setComboVal(eventComboBox, object, "event");
        setComboItemWithResourceGroupField(animInComboBox, "anim", object.anim_in);
        setComboItemWithResourceGroupField(animOutComboBox, "anim", object.anim_out);
        setComboVal(widgetIdComboBox, object, "compName");
        setComboVal(targetComboBox, object, "target");
        useseStepGeneratorCheckBox.setSelected(object.useStepGenerator);

    }

    public JComboBox getWidgetIdComboBox() {
        return widgetIdComboBox;
    }

    public JComboBox getTargetComboBox() {
        return targetComboBox;
    }

    @Override
    public void done() {

        setFieldFromComboVal(eventComboBox, target, "event");
        setFieldFromComboVal(animInComboBox, target, "anim_in");
        setFieldFromComboVal(animOutComboBox, target, "anim_out");
        setFieldFromComboVal(widgetIdComboBox, target, "compName");
        setFieldFromComboVal(adapterNameComboBox, target, "listAdapterName");
        setFieldFromComboVal(targetComboBox, target, "target");

        target.useStepGenerator = useseStepGeneratorCheckBox.isSelected();

        if(parentForm != null) {
            parentForm.handleChildValue(target);
        }


    }

    @Override
    public void handleChildValue(Object childValue) {

    }


    public JComboBox getAdapterNameComboBox() {
        return adapterNameComboBox;
    }


}
