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
import org.androware.androbeans.utils.ResourceUtils;
import org.androware.androbeans.utils.Type2TypeDefaultConstructorFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;



/**
 * Created by jkirkley on 8/20/16.
 */
public class CompFactory {

    public static void setFieldSetterOnSelect(JList jList, ReflectionUtils.FieldSetter fieldSetter) {
        setFieldSetterOnSelect(jList, fieldSetter, null);
    }

    public static void setFieldSetterOnSelect(JList jList, ReflectionUtils.FieldSetter fieldSetter, Object value) {

        setSelectedValueOnJList(jList, value);

        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if(!listSelectionEvent.getValueIsAdjusting()) {
                    if(jList.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
                        fieldSetter.set(jList.getSelectedValuesList());
                    } else {
                        Object v = jList.getSelectedValue();
                        if( v instanceof ObjectWrap) {
                            v = v.toString();
                        }
                        fieldSetter.set(v);
                    }
                }
            }
        });



    }
    public static void setFieldSetterOnAction(JComboBox jComboBox, ReflectionUtils.FieldSetter fieldSetter) {
        setFieldSetterOnAction(jComboBox, fieldSetter, null);
    }

    public static void setFieldSetterOnAction(JComboBox jComboBox, ReflectionUtils.FieldSetter fieldSetter, Object value) {
        if(value != null) {
            jComboBox.setSelectedItem(value);
        }
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object o = jComboBox.getSelectedItem();
                if( o instanceof ObjectWrap) {
                    fieldSetter.set(o.toString());
                } else {
                    fieldSetter.set(o);
                }
            }
        });
    }

    public static void setFieldSetterOnAction(JToggleButton button, ReflectionUtils.FieldSetter fieldSetter) {
        setFieldSetterOnAction(button, fieldSetter, false);
    }

    public static void setFieldSetterOnAction(JToggleButton button, ReflectionUtils.FieldSetter fieldSetter, boolean select) {
        button.setSelected(select);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fieldSetter.set(button.isSelected());
            }
        });
    }

    public interface Sink<T> {
        public void sink(T t);
    }

    public static class TextFieldStringSink<T> implements Sink<T> {

        ReflectionUtils.FieldSetter fieldSetter;
        JTextField jTextField;

        public TextFieldStringSink(JTextField jTextField) {
            this(jTextField, null);
        }

        public TextFieldStringSink(JTextField jTextField, ReflectionUtils.FieldSetter fieldSetter) {
            this.jTextField = jTextField;
            this.fieldSetter = fieldSetter;
        }

        @Override
        public void sink(T s) {
            jTextField.setText(s.toString());
            if(fieldSetter != null) {
                fieldSetter.set(s);
            }
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

                if (psiClass != null) {
                    stringSink.sink(psiClass.getQualifiedName());
                }

            }
        });
    }
    //
    public static void addTreeClassChooserAction(final Project project, final AbstractButton abstractButton, JTextField jTextField, String title) {
        addTreeClassChooserAction(project, abstractButton, jTextField, title, null);
    }
    public static void addTreeClassChooserAction(final Project project, final AbstractButton abstractButton, JTextField jTextField, String title, ReflectionUtils.FieldSetter fieldSetter) {
        addTreeClassChooserAction(project, abstractButton, new TextFieldStringSink(jTextField, fieldSetter), title);
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

                if (psiFile != null) {
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

    public static class JComboBoxCRUDWrapper<T> implements CRUDCompWrapper<T> {

        JComboBox<T> jComboBox;

        Object collection;
        ReflectionUtils.FieldSetter collectionFieldSetter;

        private void addItem(T item) {
            if(collection == null) {
                collection = collectionFieldSetter.set();
            }

            if(collection instanceof Map) {
                ((Map)collection).put(item.toString(), item);
            } else {
                ((List)collection).add(item);
            }
        }

        private void removeItem(T item) {
            if(collection instanceof Map) {
                ((Map)collection).remove(item.toString());
            } else {
                ((List)collection).remove(item);
            }
        }

        public JComboBoxCRUDWrapper(JComboBox<T> jComboBox, ReflectionUtils.FieldSetter collectionFieldSetter) {
            this.jComboBox = jComboBox;
            this.collectionFieldSetter = collectionFieldSetter;
            this.collection = collectionFieldSetter.get();
            fillCombo();
        }

        private void fillCombo(){
            if(collection != null) {
                if (collection instanceof Map) {
                    Map<String, T> itemMap = (Map<String, T>) collection;
                    for (String s : itemMap.keySet()) {
                        this.jComboBox.addItem(itemMap.get(s));
                    }
                } else {
                    List<T> items = (List<T>) collection;
                    for (T t : items) {
                        this.jComboBox.addItem(t);
                    }
                }
            }
        }

        public JComboBoxCRUDWrapper(JComboBox<T> jComboBox, List<T> items) {
            this.collection = items;
            this.jComboBox = jComboBox;
            if(items != null)
                for (T t : items) {
                    this.jComboBox.addItem(t);
                }

        }

        public JComboBoxCRUDWrapper(JComboBox<T> jComboBox, Map<String, T> itemMap) {
            this.collection = itemMap;
            this.jComboBox = jComboBox;
            if(itemMap != null) {
                for (String s : itemMap.keySet()) {
                    this.jComboBox.addItem(itemMap.get(s));
                }
            }
        }

        @Override
        public void add(T newObject) {
            addItem(newObject);
            jComboBox.addItem(newObject);
        }

        @Override
        public void delete(T object) {
            if (object == null) {
                object = (T) jComboBox.getSelectedItem();
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
            if (objId instanceof Integer) {
                return jComboBox.getItemAt((int) objId);
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
        addCRUDWrapper(project, crudObjectEditor, addButton, null, deleteButton, crudCompWrapper);
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
        ReflectionUtils.FieldSetter fieldSetter;
        T defaultTemplateObject;
        CRUDCompWrapper<T> crudCompWrapper;
        CRUDForm<T> form;
        FormAssembler formAssembler;

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass) {
            this(project, toolWindow, formClass, targetClass, null);
        }

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter) {
            this(project, toolWindow, formClass, targetClass, fieldSetter, null);
        }

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter, T defaultTemplateObject) {
            this(project, toolWindow, formClass, targetClass, fieldSetter, defaultTemplateObject, null);
        }

        public DefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter, T defaultTemplateObject, FormAssembler formAssembler) {
            this.project = project;
            this.formAssembler = formAssembler;
            this.toolWindow = toolWindow;
            this.formClass = formClass;
            this.targetClass = targetClass;
            this.fieldSetter = fieldSetter;
            this.defaultTemplateObject = defaultTemplateObject;
        }

        @Override
        public void edit(T object) {
            if (object == null) {
                object = create();
            }
            form = (CRUDForm) ReflectionUtils.newInstance(formClass);
            if (formAssembler == null) {
                form.init(project, toolWindow, object);
            } else {
                form.init(project, toolWindow, object, formAssembler);
            }
            EditFormWrapperForm editFormWrapperForm = new EditFormWrapperForm();
            editFormWrapperForm.init(project, toolWindow, form.getRootPanel(), this);
        }

        @Override
        public T create() {

            T obj = defaultTemplateObject != null ? (T) ReflectionUtils.tryCopy(defaultTemplateObject) : (T) ReflectionUtils.newInstance(targetClass);
            if (fieldSetter != null) {
                fieldSetter.set(obj);
            }
            return obj;
        }

        @Override
        public void done() {
            form.done();
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

        public PrimitiveTypeDefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter) {
            super(project, toolWindow, formClass, targetClass, fieldSetter);
        }

        public PrimitiveTypeDefaultCRUDEditorImpl(Project project, ToolWindow toolWindow, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter, T defaultTemplateObject) {
            super(project, toolWindow, formClass, targetClass, fieldSetter, defaultTemplateObject);
        }

        @Override
        public void done() {
            this.crudCompWrapper.add(form.getTarget());
            super.done();
        }
    }

    public static <T> void mkAddEditToggleWidget(Project project, ToolWindow toolWindow, AbstractButton toggleButton, Class formClass, Class targetClass, ReflectionUtils.FieldSetter fieldSetter) {

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultCRUDEditorImpl<T> editor = new DefaultCRUDEditorImpl<T>(project, toolWindow, formClass, targetClass, fieldSetter);

                editor.edit((T)fieldSetter.get());
            }
        });

        if (fieldSetter.get() != null) {
            toggleButton.setText("Edit");
        } else {
            toggleButton.setText("Add");
        }
    }


    public static <T> void fillCombo(JComboBox<T> jComboBox, List<T> items) {
        for (T item : items) {
            jComboBox.addItem(item);
        }
    }

    public static void fillComboWithClassFields(JComboBox<FieldWrap> jComboBox, Class aClass) {
        List<FieldWrap> items = new ArrayList<>();
        Field fields[] = aClass.getFields();
        for (Field field : fields) {
            items.add(new FieldWrap(field));
        }
        fillCombo(jComboBox, items);
    }

    public static void fillComboWithResourceGroup(JComboBox<FieldWrap> jComboBox, String resourceGroupName) {
        fillComboWithClassFields(jComboBox, ResourceUtils.getResourceGroup(resourceGroupName));
    }

    public static void setComboItemWithResourceGroupField(JComboBox<FieldWrap> jComboBox, String resourceGroupName, String fieldName) {
        if (fieldName != null) {
            jComboBox.setSelectedItem(new FieldWrap(resourceGroupName, fieldName));
        } else {
            jComboBox.setSelectedIndex(-1);
        }
    }

    public static abstract class ObjectWrap {
        public abstract String toString();

        public Object get() {
            return null;
        }
        public abstract Object set(Object o);

        public boolean equals(Object o) {
            return o != null && o.toString().equals(toString());
        }
    }


    public static class ClassWrap extends ObjectWrap {
        Class clazz;

        public ClassWrap() {}

        public ClassWrap(Class clazz) {
            this.clazz = clazz;
        }

        public String toString() {
            return clazz.getSimpleName();
        }

        public Object get() {
            return clazz;
        }

        @Override
        public Object set(Object o) {
            clazz = (Class)o;
            return clazz;
        }
    }

    public static class FieldWrap extends ObjectWrap {
        Field field;
        public FieldWrap() {}


        public FieldWrap(String resourceGroupName, String fieldName) {
            Class resourceGroupClass = ResourceUtils.getResourceGroup(resourceGroupName);
            try {
                field = resourceGroupClass.getField(fieldName);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        public FieldWrap(Field field) {
            this.field = field;
        }

        public String toString() {
            return field.getName();
        }

        public Object get() {
            return field;
        }
        @Override
        public Object set(Object o) {
            field = (Field) o;
            return field;
        }

    }

    public static class MethodWrap extends ObjectWrap {
        Method method;
        public MethodWrap() {}

        public MethodWrap(Method method) {
            this.method = method;
        }

        public String toString() {
            return method.getName() + "()";
        }

        public Object get() {
            return method;
        }
        @Override
        public Object set(Object o) {
            method = (Method) o;
            return method;
        }
    }



    public static Type2TypeDefaultConstructorFactory objectWrapType2TypeDefaultConstructorFactory =
            new Type2TypeDefaultConstructorFactory(
                    Class.class, ClassWrap.class,
                    Method.class, MethodWrap.class,
                    Field.class, FieldWrap.class);

    public static final String ID_ATTR = "android:id=\"@+id/";

    public static List<String> getWidgetIdList(String layout) {
        String layoutContents = Utils.HACKgetLayoutFileContents(layout);
        List<String> items = new ArrayList<>();

        if(layoutContents != null) {
            String lines[] = layoutContents.split("\\n");
            for (String line : lines) {
                int index = line.indexOf(ID_ATTR);
                if (index != -1) {
                    index += ID_ATTR.length();
                    String id = line.substring(index, line.indexOf('\"', index));
                    items.add(id);
                }
            }
        }
        return items;
    }

    public static void fillComboWidthWdgetIdsFromLayout(JComboBox comboBox, String layout) {
        fillCombo(comboBox, getWidgetIdList(layout));
    }


    public static <T> void fillJList(JList<T> jList, List<T> items) {

        ListModel listModel = jList.getModel();

        if (listModel == null || !(listModel instanceof DefaultListModel)) {
            listModel = new DefaultListModel();
            jList.setModel(listModel);
        }

        DefaultListModel<T> jListModel = (DefaultListModel) listModel;

        jListModel.removeAllElements();

        for (T item : items) {
            jListModel.addElement(item);
        }

        jList.setModel(jListModel);
    }


    public static void fillListWithWidgetIdsFromLayout(JList jList, String layout) {
        fillJList(jList, getWidgetIdList(layout));
    }

    public static void fillListWithClassFields(JList<FieldWrap> jList, Class aClass) {
        fillListWithClassFields(jList, aClass, null);
    }

    public static void fillListWithClassFields(JList<FieldWrap> jList, Class aClass, Predicate<Member> predicate) {

        List<FieldWrap> items = new ArrayList<>();
        Field fields[] = aClass.getFields();
        for (Field field : fields) {
            if(predicate == null || predicate.test(field)) {
                items.add(new FieldWrap(field));
            }
        }
        fillJList(jList, items);
    }

    public static class IgnoreBaseObjectPredicate implements Predicate<Member> {
        @Override
        public boolean test(Member member) {
            return member.getDeclaringClass() != Object.class;
        }
    }

    public static void fillListWithAllClassMembers(JList<ObjectWrap> jList, Class aClass) {
        fillListWithAllClassMembers(jList, aClass, new IgnoreBaseObjectPredicate());
    }

    public static void fillListWithAllClassMembers(JList<ObjectWrap> jList, Class aClass, Predicate<Member> predicate) {

        List<Member> members = ReflectionUtils.getAllMembers(aClass, predicate);
        List<ObjectWrap> items = new ArrayList<>();

        for(Member member: members) {
            items.add((ObjectWrap) objectWrapType2TypeDefaultConstructorFactory.build(member.getClass(), member));
        }
        fillJList(jList, items);


    }

    public static void setSelectedValueOnJList(JList jList, Object value) {
        if(value != null) {
            if(value instanceof List) {
                List l = (List)value;
                for(Object o: l) {
                    jList.setSelectedValue(o,false);
                }
            } else {
                jList.setSelectedValue(value,true);
            }
        }
    }

    // TODO Regex version
    // TODO Move this and ObjectWrap into ReflectionUtils
    public static class IgnoreMemberWithPrefix implements Predicate<Member> {
        String prefix;
        public IgnoreMemberWithPrefix(String prefix) {

            this.prefix = prefix;
        }
        @Override
        public boolean test(Member member) {
            return !member.getName().startsWith(prefix);
        }
    }
    public static void fillListWithResourceGroup(JList<FieldWrap> jListBox, String resourceGroupName) {
        fillListWithClassFields(jListBox, ResourceUtils.getResourceGroup(resourceGroupName), new IgnoreMemberWithPrefix("abc_"));
    }


    public static void setComboVal(JComboBox comboBox, Object target, String field) {
        Object v = ReflectionUtils.getFieldValue(target, field);
        System.out.println(field + ": " + v);
        if (v != null) {
            comboBox.setSelectedItem(v);
        } else {
            comboBox.setSelectedIndex(-1);
        }
    }

    public static void setFieldFromComboVal(JComboBox comboBox, Object target, String field) {
        Object v = comboBox.getSelectedItem();
        if (v != null) {
            if (v instanceof ObjectWrap) {
                ReflectionUtils.setField(target.getClass(), field, target, v.toString());
            } else {
                ReflectionUtils.setField(target.getClass(), field, target, v);
            }
        }
    }

    public static void setTextfieldVal(JTextField textField, Object target, String field) {
        Object v = ReflectionUtils.getFieldValue(target, field);
        if (v != null) {
            textField.setText(v.toString());
        }
    }

    public static void setValFromTextfield(JTextField textField, Object target, String field) {
        String v = textField.getText();
        if (v != null && v.length() > 0) {
            ReflectionUtils.setField(target.getClass(), field, target, v);
        }
    }



}
