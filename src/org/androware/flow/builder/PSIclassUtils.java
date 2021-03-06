package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtil;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

import static org.androware.flow.builder.CompFactory.fillCombo;
import static org.androware.flow.builder.CompFactory.fillJList;


/**
 * Created by jkirkley on 9/2/16.
 */
public class PSIclassUtils {

    public static PsiClass resourceClass;
    public static Project project;

    public static class PsiClassWrap extends CompFactory.ObjectWrap {
        PsiClass psiClass;
        public PsiClassWrap() {}

        public PsiClassWrap(PsiClass psiClass) {
            this.psiClass = psiClass;
        }

        public String toString() {
            return psiClass.getName();
        }

        public Object get() {
            return psiClass;
        }

        @Override
        public Object set(Object o) {
            psiClass = (PsiClass) o;
            return psiClass;
        }
    }

    public static class PsiFieldWrap extends CompFactory.ObjectWrap {
        PsiField field;
        public PsiFieldWrap() {}


        public PsiFieldWrap(String resourceGroupName, String fieldName) {
            PsiClass resourceGroupClass = getInnerClass(resourceClass, resourceGroupName);
            field = getField(resourceGroupClass, fieldName);
        }

        public PsiFieldWrap(PsiField field) {
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
            field = (PsiField) o;
            return field;
        }

    }


    public static class PsiMethodWrap extends CompFactory.ObjectWrap {
        PsiMethod method;
        public PsiMethodWrap() {}


        public PsiMethodWrap(String resourceGroupName, String methodName) {
            PsiClass resourceGroupClass = getInnerClass(resourceClass, resourceGroupName);
            method = getMethod(resourceGroupClass, methodName);
        }

        public PsiMethodWrap(PsiMethod method) {
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
            method = (PsiMethod) o;
            return method;
        }

    }

    public static PsiClass getInnerClass(PsiClass psiClass, String innerClassName) {
        PsiClass psiClasses[] = psiClass.getInnerClasses();

        for(PsiClass clazz: psiClasses) {
            if(clazz.getName().equals(innerClassName)) {
                return clazz;
            }
        }
        return null;
    }


    public static PsiMethod getMethod(PsiClass psiClass, String methodName) {
        PsiMethod methods[] = psiClass.getMethods();

        for(PsiMethod clazz: methods) {
            if(clazz.getName().equals(methodName)) {
                return clazz;
            }
        }
        return null;
    }

    public static PsiField getField(PsiClass psiClass, String fieldName) {
        PsiField fields[] = psiClass.getFields();

        for(PsiField clazz: fields) {
            if(clazz.getName().equals(fieldName)) {
                return clazz;
            }
        }
        return null;
    }

    public static List<PsiClassWrap> wrapInnerClasses(PsiClass psiClass, Predicate<PsiClass> predicate) {
        List<PsiClassWrap> classWraps = new ArrayList<>();
        PsiClass psiClasses[] = psiClass.getInnerClasses();

        for(PsiClass clazz: psiClasses) {
            if(predicate == null || predicate.test(clazz) ) {
                classWraps.add(new PsiClassWrap(clazz));
            }
        }
        return classWraps;
    }


    public static void fillListWithClassPsiFields(JList<PsiFieldWrap> jList, PsiClass aClass, Predicate<PsiMember> predicate) {

        List<PsiFieldWrap> items = new ArrayList<>();
        PsiField fields[] = aClass.getFields();
        for (PsiField field : fields) {
            if(predicate == null || predicate.test(field)) {
                items.add(new PsiFieldWrap(field));
            }
        }
        fillJList(jList, items);
    }

    public static class IgnoreBaseObjectPredicate implements Predicate<PsiMember> {

        @Override
        public boolean test(PsiMember member) {
            //System.out.println("mem qtn: " + member.getContainingClass().getQualifiedName());
            return !member.getContainingClass().getQualifiedName().equals(Object.class.getName());
        }
    }

    public static List<PsiFieldWrap> wrapAllFields(PsiClass psiClass, Predicate<PsiMember> predicate) {
        List<PsiFieldWrap> items = new ArrayList<>();

        PsiField fields[] = psiClass.getAllFields();
        for (PsiField field : fields) {
            if (predicate == null || predicate.test(field)) {
                items.add(new PsiFieldWrap(field));
            }
        }
        return items;
    }

    public static List<CompFactory.ObjectWrap> wrapAllClassMembers(PsiClass psiClass, Predicate<PsiMember> predicate) {
        List<CompFactory.ObjectWrap> items = new ArrayList<>();

        PsiField fields [] = psiClass.getAllFields();
        for(PsiField field: fields) {
            //System.out.println(field.getName());
            if(predicate == null || predicate.test(field)) {
                items.add(new PsiFieldWrap(field));
            }
        }

        PsiMethod methods [] = psiClass.getAllMethods();
        for(PsiMethod method: methods) {
            //System.out.println(method.getName());
            if(predicate == null || predicate.test(method)) {
                items.add(new PsiMethodWrap(method));
            }
        }
        return items;

    }

    public static List<CompFactory.ObjectWrap> wrapAllClassMembers(PsiClass psiClass) {
        return wrapAllClassMembers(psiClass, null);
    }

    public static void fillListWithAllClassMembers(JList<CompFactory.ObjectWrap> jList, PsiClass aClass) {
        fillListWithAllClassMembers(jList, aClass, new IgnoreBaseObjectPredicate());
    }

    public static void fillListWithAllClassMembers(JList<CompFactory.ObjectWrap> jList, PsiClass psiClass, Predicate<PsiMember> predicate) {
        fillJList(jList, wrapAllClassMembers(psiClass, predicate));
    }

    public static void fillListWithAllClassFields(JList<PsiFieldWrap> jList, PsiClass psiClass, Predicate<PsiMember> predicate) {
        fillJList(jList, wrapAllFields(psiClass, predicate));
    }

    public static void fillListWithAllInnerClasses(JList<PsiClassWrap> jList, PsiClass psiClass, Predicate<PsiClass> predicate) {
        fillJList(jList, wrapInnerClasses(psiClass, predicate));
    }

    public static void fillListWithAllResGroups(JList<PsiClassWrap> jList, Predicate<PsiClass> predicate) {
        fillListWithAllInnerClasses(jList, resourceClass, predicate);
    }



    public static void fillComboWithClassFields(JComboBox<PsiFieldWrap> jComboBox, PsiClass aClass, Predicate<PsiMember> predicate) {
        List<PsiFieldWrap> items = wrapAllFields(aClass, predicate);
        fillCombo(jComboBox, items);
    }

    public static void fillComboWithResourceGroup(JComboBox<PsiFieldWrap> jComboBox, String resourceGroupName) {
        fillComboWithClassFields(jComboBox, getInnerClass(resourceClass, resourceGroupName), new IgnoreMemberWithPrefix("abc_"));
    }

    public static void setComboItemWithResourceGroupField(JComboBox<PsiFieldWrap> jComboBox, String resourceGroupName, String fieldName) {
        if (fieldName != null) {
            jComboBox.setSelectedItem(new PsiFieldWrap(resourceGroupName, fieldName));
        } else {
            jComboBox.setSelectedIndex(-1);
        }
    }

    public static class IgnoreMemberWithPrefix implements Predicate<PsiMember> {
        String prefix;
        public IgnoreMemberWithPrefix(String prefix) {
            this.prefix = prefix;
        }
        @Override
        public boolean test(PsiMember member) {
            return !member.getName().startsWith(prefix);
        }
    }

    public static void fillListWithResourceGroup(JList<PsiFieldWrap> jListBox, String resourceGroupName) {
        fillListWithAllClassFields(jListBox, getInnerClass(resourceClass, resourceGroupName), new IgnoreMemberWithPrefix("abc_"));
    }

    public static PsiClass getClass(String className) {

        System.out.println(className);
        return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project));
        //return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.(project));
    }

    public static PsiClass getClassOfField(PsiField psiField) {

        return PsiUtil.resolveClassInType(psiField.getType());
        //PsiType.getTypeByName(psiField.getType().getNproject, GlobalSearchScope.allScope(project)).resolve();
    }

    public static TreeSet<String> simpleTypeSet = new TreeSet();

    static {
        simpleTypeSet.add("String");
        simpleTypeSet.add("int");
        simpleTypeSet.add("Integer");
        simpleTypeSet.add("long");
        simpleTypeSet.add("Long");
        simpleTypeSet.add("float");
        simpleTypeSet.add("Float");
        simpleTypeSet.add("double");
        simpleTypeSet.add("Double");
        simpleTypeSet.add("byte");
        simpleTypeSet.add("Byte");
        simpleTypeSet.add("short");
        simpleTypeSet.add("Short");
        simpleTypeSet.add("char");
        simpleTypeSet.add("Character");
        simpleTypeSet.add("boolean");
        simpleTypeSet.add("Boolean");
    }
    public static String getTypeName(PsiType type) {
        return type.toString().substring("PsiType:".length());
    }

    public static boolean isPrimitiveOrString(PsiType type) {
        return simpleTypeSet.contains(getTypeName(type));
    }

    public static String getFieldIdentifier(PsiField field) {
        return field.getNameIdentifier().toString().substring("PsiIdentifier:".length());
    }


    public static PsiType getParameterType(PsiClassType psiClassType, int index) {
        PsiType[] params = psiClassType.getParameters();
        return index < params.length? params[index]: null;
    }
}
