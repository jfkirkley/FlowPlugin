package org.androware.flow.plugin;


import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.androbeans.utils.ResourceUtils;
import org.androware.flow.base.FlowBase;
import org.androware.flow.builder.*;
import org.jetbrains.annotations.NotNull;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static org.androware.flow.builder.MainForm.mainForm;
import static org.androware.flow.builder.Utils.HACK_ROOT_DIR;

/**
 * Created by jkirkley on 8/19/16.
 */

public class FlowToolWindowFactory implements ToolWindowFactory {

    public FlowBase loadFlow(String fname) throws ObjectReadException {

        List<ObjectReadListener> listeners = new ArrayList<>();
        listeners.add(new LinkObjectReadListener());

        JsonObjectReader jsonObjectReader = new JsonObjectReader(fname, FlowBase.class, null, listeners);

        FlowBase flow = (FlowBase) jsonObjectReader.read();

        return flow;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {


        String baseDir = project.getBaseDir().getCanonicalPath();

        String SRC_DIR = "/app/src/main/";
        System.out.println("root: " + baseDir);
        System.out.println("root: " + baseDir + SRC_DIR);

        StartFlowForm startFlowForm = new StartFlowForm();
        startFlowForm.init(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(startFlowForm.getRootPanel(), "", false);
        toolWindow.getContentManager().addContent(content);

        startFlowForm.getOpenButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String resClassName = startFlowForm.getResourceClassName();
                ResourceUtils.R = ReflectionUtils.getClass(resClassName);

                String path = baseDir + SRC_DIR + "res/raw/" + startFlowForm.getFileName();

                System.out.println(path);

                try {
                    FlowBase flowBase = loadFlow(path);

                    MainForm mainForm = new MainForm(project, toolWindow, flowBase);

                    mainForm.buildFlowTree(flowBase);

                    toolWindow.getContentManager().removeAllContents(false);

                    Content content = contentFactory.createContent(mainForm.getRootPanel(), "", false);
                    toolWindow.getContentManager().addContent(content);

                } catch (Exception e) {

                    startFlowForm.setErrorMsg("Could not load Flow: " + path + "\n exception: " + e.getStackTrace());
                }

            }
        });

    }
}
