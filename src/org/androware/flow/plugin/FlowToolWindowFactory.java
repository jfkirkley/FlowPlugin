package org.androware.flow.plugin;


import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.androbeans.utils.ResourceUtils;
import org.androware.flow.base.FlowBase;
import org.androware.flow.base.ListMapper;
import org.androware.flow.base.StepBase;
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
/*
TODO:
- remove name properties
- validate to ensure essential fields are filled in: processor, parentContainer, layout etc.

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

        PSIclassUtils.project = project;

        if (instance == null) {
            instance = this;
        } else {
            toolWindow.getContentManager().removeAllContents(true);
        }
        /*
        String [] names = PsiShortNamesCache.getInstance(project).getAllClassNames();
        for(String n: names) {
            System.out.println(n);
        }
*/
        baseDir = project.getBaseDir().getCanonicalPath();

        String SRC_DIR = "/app/src/main/";

        resDir = baseDir + SRC_DIR + "res";

        System.out.println("root: " + baseDir);
        System.out.println("root: " + baseDir + SRC_DIR);


        if (false) {

            BeanTree beanTree = new BeanTree();

            //beanTree.init(PSIclassUtils.getClass(TestBean.class.getName()));

            beanTree.init(PSIclassUtils.getClass(StepBase.class.getName()));

            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(beanTree.getRootPanel(), "", false);
            toolWindow.getContentManager().addContent(content);

        } else {

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

                    if(false) {
                        toolWindow.getContentManager().removeAllContents(false);
                        // test code:
                        StandardJSONlistGeneratorForm form = new StandardJSONlistGeneratorForm();

                        //form.init(project, toolWindow, new ListMapper(), "pref_list_item", StepBase.class, null);
                        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        Content content = contentFactory.createContent(form.getRootPanel(), "", false);
                        toolWindow.getContentManager().addContent(content);


                    } else {
                        String path = "";
                        try {

                            FlowBase flowBase = null;
                            String fileName = startFlowForm.getFileName();
                            if (fileName == null || fileName.length() == 0) {
                                flowBase = new FlowBase();
                            } else {
                                path = resDir + "/raw/" + fileName;
                                flowBase = loadFlow(path);
                                flowBase.name = fileName.substring(0, fileName.indexOf('.'));
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
                }
            });
        }
    }
}
