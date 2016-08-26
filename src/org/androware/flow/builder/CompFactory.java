package org.androware.flow.builder;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.androware.androbeans.utils.ReflectionUtils;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import static groovy.ui.text.FindReplaceUtility.showDialog;

/**
 * Created by jkirkley on 8/20/16.
 */
public class CompFactory {

    public interface Sink<T>{
        public void sink(T t);
    }

    public static class TextFieldStringSink<T> implements Sink<T> {

        JTextField jTextField;
        public TextFieldStringSink(JTextField jTextField) {
            this.jTextField = jTextField;
        }

        @Override
        public void sink(T s) {
            jTextField.setText(s.toString());
        }
    }

    public static class ComboBoxSink<T> implements Sink<T> {

        List<T> items;
        JComboBox jComboBox;

        public ComboBoxSink(JComboBox jComboBox, List<T> strings) {
            this.items = strings;
            this.jComboBox = jComboBox;
        }

        @Override
        public void sink(T t) {
            items.add(t);
            jComboBox.addItem(t);
        }
    }


    public static void addTreeClassChooserAction(final Project project, final AbstractButton abstractButton, final Sink<String> stringSink, String title) {
        abstractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TreeClassChooser treeClassChooser = TreeClassChooserFactory.getInstance(project).createProjectScopeChooser(title);

                treeClassChooser.showDialog();

                PsiClass psiClass = treeClassChooser.getSelected();

                if(psiClass != null) {
                    stringSink.sink(psiClass.getQualifiedName());
                }

            }
        });
    }

    public static void addTreeClassChooserAction(final Project project, final AbstractButton abstractButton, JTextField jTextField, String title) {
        addTreeClassChooserAction(project, abstractButton, new TextFieldStringSink(jTextField), title);
    }

    public static void addTreeClassChooserAction(final Project project, final AbstractButton abstractButton, JComboBox jComboBox, List items, String title) {
        addTreeClassChooserAction(project, abstractButton, new ComboBoxSink<String>(jComboBox, items), title);
    }

    public static void addFileChooserAction(final Project project, final AbstractButton abstractButton, final Sink<String> stringSink, String title, FileType fileType) {
        abstractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TreeFileChooser treeFileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser(title, null, fileType, null);

                treeFileChooser.showDialog();

                PsiFile psiFile = treeFileChooser.getSelectedFile();

                if(psiFile != null) {
                    stringSink.sink(psiFile.getName());
                }

            }
        });
    }

    public static void addFileChooserAction(final Project project, final AbstractButton abstractButton,
                                            JTextField jTextField, String title, FileType fileType) {

        addFileChooserAction(project, abstractButton, new TextFieldStringSink(jTextField), title, fileType);
    }

    public static void addFileChooserAction(final Project project, final AbstractButton abstractButton,
                                            JComboBox jComboBox, List items, String title, FileType fileType) {

        addFileChooserAction(project, abstractButton, new ComboBoxSink(jComboBox, items), title, fileType);
    }


    public interface CRUDCompWrapper<T> {
        public void add(T newObject);
        public void delete(T object);
        public void update(T oldObject, T newObject);
        public T read(Object objId);
    }

    public static class JComboBoxCRUDWrapper<T> implements  CRUDCompWrapper<T> {

        JComboBox<T> jComboBox;
        List<T> items = null;
        Map<String, T> itemMap = null;

        private void addItem(T item){
            if(items != null) {
                items.add(item);
            } else if(itemMap != null){
                itemMap.put(item.toString(), item);
            }
        }

        private void removeItem(T item) {
            if(items != null) {
                items.remove(item);
            } else if(itemMap != null){
                itemMap.remove(item.toString());
            }

        }
        public JComboBoxCRUDWrapper(JComboBox<T> jComboBox, List<T> items) {
            this.items = items;
            this.jComboBox = jComboBox;
            for(T t: items) {
                this.jComboBox.addItem(t);
            }

        }
        public JComboBoxCRUDWrapper(JComboBox<T> jComboBox, Map<String, T> itemMap) {
            this.itemMap = itemMap;
            this.jComboBox = jComboBox;
            for(String s: itemMap.keySet()) {
                this.jComboBox.addItem(itemMap.get(s));
            }
        }

        @Override
        public void add(T newObject) {
            addItem(newObject);
            jComboBox.addItem(newObject);
        }

        @Override
        public void delete(T object) {
            if(object == null) {
                object = (T)jComboBox.getSelectedItem();
                jComboBox.removeItem(object);
            } else {
                jComboBox.removeItem(object);
            }
            removeItem(object);

        }

        @Override
        public void update(T oldObject, T newObject) {
            delete(oldObject);
            add(newObject);
        }

        @Override
        public T read(Object objId) {
            if(objId instanceof  Integer) {
                return jComboBox.getItemAt((int)objId);
            }
            return jComboBox.getItemAt(jComboBox.getSelectedIndex());
        }
    }

    public interface CRUDObjectEditor<T> {
        public void edit(T object);
        public T create();
        public void done();
        public void cancel();
        public void setCompWrapper(CRUDCompWrapper<T> crudCompWrapper);
    }


    public static <T> void addCRUDWrapper(final Project project, final CRUDObjectEditor<T> crudObjectEditor, final AbstractButton addButton, final AbstractButton deleteButton, final CRUDCompWrapper<T> crudCompWrapper) {
        addCRUDWrapper(project,crudObjectEditor, addButton, null, deleteButton, crudCompWrapper);
    }


    public static <T> void addCRUDWrapper(final Project project, final CRUDObjectEditor<T> crudObjectEditor, final AbstractButton editButton, final AbstractButton addButton, final AbstractButton deleteButton, final CRUDCompWrapper<T> crudCompWrapper) {

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                crudCompWrapper.delete(null);

            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                crudObjectEditor.edit(crudCompWrapper.read(null));

            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                T newObject = crudObjectEditor.create();
                crudCompWrapper.add(newObject);
                crudObjectEditor.edit(newObject);


            }
        });

        crudObjectEditor.setCompWrapper(crudCompWrapper);
    }



    public static class DefaultCRUDEditorImpl<T> implements CompFactory.CRUDObjectEditor<T> {
        ToolWindow toolWindow;
        Project project;
        Class formClass;
        Class targetClass;
        CreateObjectListener createObjectListener;
        T defaultTemplateObject;
        CRUDCompWrapper<T> crudCompWrapper;
        CRUDForm<T> form;

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass) {
            this(project, toolWindow, formClass, targetClass, null);
        }

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, CreateObjectListener createObjectListener) {
            this(project, toolWindow, formClass, targetClass, createObjectListener, null);
        }

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, CreateObjectListener createObjectListener, T defaultTemplateObject) {
            this.project = project;
            this.toolWindow = toolWindow;
            this.formClass = formClass;
            this.targetClass = targetClass;
            this.createObjectListener = createObjectListener;
            this.defaultTemplateObject = defaultTemplateObject;
        }

        @Override
        public void edit(T object) {
            if(object == null) {
                object = create();
            }
            form = (CRUDForm)ReflectionUtils.newInstance(formClass);
            form.init(project, toolWindow, object);
            EditFormWrapperForm editFormWrapperForm = new EditFormWrapperForm();
            editFormWrapperForm.init(project, toolWindow, form.getRootPanel(), this);
        }

        @Override
        public T create() {

            T obj = defaultTemplateObject != null? (T)ReflectionUtils.tryCopy(defaultTemplateObject): (T)ReflectionUtils.newInstance(targetClass);
            if(createObjectListener!=null) {
                createObjectListener.onCreate(obj);
            }
            return obj;
        }

        @Override
        public void done() {
        }

        @Override
        public void cancel() {

        }

        @Override
        public void setCompWrapper(CRUDCompWrapper<T> crudCompWrapper) {
            this.crudCompWrapper = crudCompWrapper;
        }
    }

    public static class PrimitiveTypeDefaultCRUDEditorImpl<T> extends DefaultCRUDEditorImpl<T> {


        public PrimitiveTypeDefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass) {
            super(project, toolWindow, formClass, targetClass);
        }

        public PrimitiveTypeDefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, CreateObjectListener createObjectListener) {
            super(project, toolWindow, formClass, targetClass, createObjectListener);
        }

        public PrimitiveTypeDefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, CreateObjectListener createObjectListener, T defaultTemplateObject) {
            super(project, toolWindow, formClass, targetClass, createObjectListener, defaultTemplateObject);
        }

        @Override
        public void done() {
            this.crudCompWrapper.add(form.getTarget());
            super.done();
        }
    }

    public static <T> void mkAddEditToggleWidget(Project project, ToolWindow toolWindow, AbstractButton toggleButton, Class formClass, Class targetClass, T target, CreateObjectListener createObjectListener) {

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultCRUDEditorImpl<T>  editor = new DefaultCRUDEditorImpl<T>(project, toolWindow, formClass, targetClass);

                editor.edit(target);
            }
        });

        if(target != null) {
            toggleButton.setText("Edit");
        } else {
            toggleButton.setText("Add");
        }
    }


}
