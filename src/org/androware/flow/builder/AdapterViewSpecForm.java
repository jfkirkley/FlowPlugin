package org.androware.flow.builder;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.flow.base.AdapterViewSpec;
import org.androware.flow.base.UI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 8/22/16.
 */
public class AdapterViewSpecForm implements CRUDForm<AdapterViewSpec> {
    private JComboBox viewIDComboBox;
    private FileChooserWidget itemLayoutFileChooserWidget;
    private JCheckBox useDefaultCheckBox;
    private ComboBoxCRUDForm beanIDsComboBoxCRUDForm;
    private ComboBoxCRUDForm itemsComboBoxCRUDForm;
    private JButton itemGeneratorButton;
    private JPanel rootPanel;
    private JButton addConstructorSpecButton;

    AdapterViewSpec adapterViewSpec;

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target, FormAssembler<CRUDForm> formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, AdapterViewSpec target) {
        adapterViewSpec =  target;

        itemLayoutFileChooserWidget.init(project, "Choose Item Layout", StdFileTypes.XML);

        if(adapterViewSpec.beanIds == null) {
            adapterViewSpec.beanIds = new ArrayList<String>();
        }

        beanIDsComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl<String>(project, toolWindow, StringForm.class, String.class), adapterViewSpec.beanIds);

        Map defItemMap = new HashMap<String, Object>();

        defItemMap.put("target", null);
        defItemMap.put("label", null);
        defItemMap.put("labelFieldId", null);

        if(adapterViewSpec.items == null){
            adapterViewSpec.items = new ArrayList();
        }
        itemsComboBoxCRUDForm.init(project, new CompFactory.DefaultCRUDEditorImpl(project, toolWindow, MapForm.class, HashMap.class, null, defItemMap), adapterViewSpec.items);//new ArrayList<Map>());

        CompFactory.mkAddEditToggleWidget(project, toolWindow, addConstructorSpecButton,
                ConstructorSpecForm.class, ConstructorSpec.class, target.adapterConstructorSpec,
                new CreateObjectListener<ConstructorSpec>() {
                    @Override
                    public void onCreate(ConstructorSpec object) {
                        target.adapterConstructorSpec = object;
                    }
                });

        CompFactory.mkAddEditToggleWidget(project, toolWindow, itemGeneratorButton,
                ConstructorSpecForm.class, ConstructorSpec.class, target.itemGeneratorSpec,
                new CreateObjectListener<ConstructorSpec>() {
                    @Override
                    public void onCreate(ConstructorSpec object) {
                        target.itemGeneratorSpec = object;
                    }
                });

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public AdapterViewSpec getTarget() {
        return adapterViewSpec;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(AdapterViewSpec object) {

    }
}
