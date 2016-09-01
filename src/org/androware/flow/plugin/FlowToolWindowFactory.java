package org.androware.flow.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.androware.androbeans.utils.ResourceUtils;
import org.androware.flow.base.FlowBase;
import org.androware.flow.builder.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkirkley on 8/19/16.
 */

public class FlowToolWindowFactory implements ToolWindowFactory {
    public FlowBase loadFlow(String fname) {
        try {

            List<ObjectReadListener> listeners = new ArrayList<>();
            listeners.add(new LinkObjectReadListener());

            JsonObjectReader jsonObjectReader = new JsonObjectReader(fname, FlowBase.class, null, listeners);

            FlowBase flow = (FlowBase) jsonObjectReader.read();

            return flow;

        } catch (ObjectReadException e) {
        }
        return null;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        FlowBase flowBase = loadFlow("/home/jkirkley/tmp/tflow.js");
        //FlowBase flowBase = loadFlow("/home/jkirkley/tmp/tf.js");
        //FlowBase flowBase = new FlowBase();

        ResourceUtils.R = ResEx.class;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        MainForm mainForm = new MainForm(project, toolWindow, flowBase);

        mainForm.buildFlowTree(flowBase);


        //StepForm stepForm = new StepForm(project, toolWindow, flowBase.steps.get("try_words"), flowBase);
        //Content content = contentFactory.createContent(stepForm.getRootPanel(), "", false);

        Content content = contentFactory.createContent(mainForm.getRootPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
