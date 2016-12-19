package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * Created by jkirkley on 8/21/16.
 */
public interface CRUDForm<T> {

    public void init(Project project, ToolWindow toolWindow, T target, FormAssembler formAssembler, CRUDForm parentForm);

    public void init(Project project, ToolWindow toolWindow, T target, FormAssembler formAssembler);

    public void init(Project project, ToolWindow toolWindow, T target);


    public JPanel getRootPanel();

    public T getTarget();

    public void clear();

    public void populate(T object);

    public void done();

    public void handleChildValue(Object childValue);
}
