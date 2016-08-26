package org.androware.flow.builder;

import java.util.Arrays;

import org.androware.androbeans.utils.ReflectionUtils;
import sun.rmi.runtime.Log;

import javax.swing.text.View;
import javax.swing.text.html.ListView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jkirkley on 1/19/16.
 */
public class Utils {

    public static Object R;

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }


    public static boolean fileExists(String absolutePath) {
        return new File(absolutePath).exists();
    }

    public static void deleteFiles(File dir, final String ext) {

        FilenameFilter ff;
        ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(ext);
            }
        };
        for (File f : dir.listFiles(ff)) {

            f.delete();
        }
    }


    public static List<File> getFiles(File dir, final String suffix) {

        FilenameFilter ff;
        ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(suffix);
            }
        };
        List<File> files = new ArrayList<>();
        for (File f : dir.listFiles(ff)) {
            files.add(f);
        }
        return files;
    }

    public static String normalizeStringToFilePath(String s) {
        // test code:
        //         String s = Utils.normalizeStringToFilePath("s{}!@#$abc%^&*()xyz+");

        return s.replaceAll("[\\s{}\\?\\.\\,\\:\\;!@#\\$%\\^&*\\-\\(\\)\\+\\]\\[]+", "_");
    }

    public static String removePunctuation(String s, String replacement) {
        return s.replaceAll("[<>{}\\?\\.\\,\\:\\;!@#\\$%\\^&*\\(\\)\\+\\]\\[]+", replacement);
    }

    public static String removePunctuation(String s) {
        // test code:
        //         String s = Utils.normalizeStringToFilePath("s{}!@#$abc%^&*()xyz+");
        return removePunctuation(s, " ");
    }



    public static Set makeSet(Object... members) {
        HashSet hashSet = new HashSet();
        for (Object obect : members) {
            hashSet.add(obect);
        }
        return hashSet;
    }

    public static boolean isPrimitiveOrString(Object object) {
        if (object != null) {
            Class cls = object.getClass();
            return cls.isPrimitive() || cls == String.class || cls == Integer.class ||
                    cls == Boolean.class || cls == Long.class || cls == Character.class ||
                    cls == Float.class || cls == Double.class;
        }
        return false;
    }



    public static String upCaseFirstLetter(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    public static <K, C> void addToContainerValue(Map<K, C> map,  K key, Object value) {
        if(!map.containsKey(key)) {
            //  TODO can this be done??? C container = ReflectionUtils.newInstance(C);
        }
    }

    public static void addValueToMappedContainer(Field mapField, Map map,  Object key, Object value) {
        if(!map.containsKey(key)) {
            Class containerType = ReflectionUtils.getGenericType(mapField, 1);
            if(Collection.class.isAssignableFrom(containerType)) {
                Collection collection = (Collection)ReflectionUtils.newInstance(containerType);
                collection.add(value);
                map.put(key, collection);
            }
        }
    }
}