package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

/**
 * Created by jkirkley on 8/25/16.
 */
public interface FormAssembler<F> {

    public void assemble(Project project, ToolWindow toolWindow, F form);

}
