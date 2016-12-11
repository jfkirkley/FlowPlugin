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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jkirkley on 8/19/16.
 */

public class FlowToolWindowFactory implements ToolWindowFactory {

    public String baseDir;
    public String resDir;

    public static FlowToolWindowFactory instance = null;

    public FlowBase loadFlow(String fname) throws ObjectReadException {

        List<ObjectReadListener> listeners = new ArrayList<>();
        listeners.add(new LinkObjectReadListener());

        JsonObjectReader jsonObjectReader = new JsonObjectReader(fname, FlowBase.class, null, listeners);

        FlowBase flow = (FlowBase) jsonObjectReader.read();

        return flow;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        if(instance == null) {
            instance = this;
        } else {
            toolWindow.getContentManager().removeAllContents(true);
        }

        baseDir = project.getBaseDir().getCanonicalPath();

        String SRC_DIR = "/app/src/main/";

        resDir = baseDir + SRC_DIR + "res";

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
                String path = "";
                try {

                    FlowBase flowBase = null;
                    String fileName = startFlowForm.getFileName();
                    if (fileName == null || fileName.length() == 0) {
                        flowBase = new FlowBase();
                    } else {
                        path = resDir + "/raw/" + fileName;
                        flowBase = loadFlow(path);
                        System.out.println(path);
                    }

                    MainForm mainForm = new MainForm(project, toolWindow, flowBase);

                    mainForm.buildFlowTree(flowBase);

                    toolWindow.getContentManager().removeAllContents(false);

                    Content content = contentFactory.createContent(mainForm.getRootPanel(), "", false);
                    toolWindow.getContentManager().addContent(content);

                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    startFlowForm.setErrorMsg("Could not load Flow: " + path + "\n exception:\n " + sw.toString());
                }

            }
        });

    }
}
