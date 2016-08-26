package org.androware.flow.builder;

import org.androware.androbeans.utils.ReflectionUtils;


import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Field;
import java.util.*;

import static javax.json.stream.JsonParser.Event.KEY_NAME;
import static javax.json.stream.JsonParser.Event.VALUE_NUMBER;
import static javax.json.stream.JsonParser.Event.VALUE_STRING;


/**
 * Created by jkirkley on 8/16/16.
 */
public class JsonObjectReader implements ObjectReader {
    JsonParser parser;
    JsonReader reader;

    public class JsonReader {

        JsonParser.Event lastEvent = null;
        JsonParser parser;

        public JsonReader(JsonParser jsonParser) {
            parser = jsonParser;
        }

        private JsonParser.Event getNext() {
            if (lastEvent != null) {
                l(lastEvent.toString());
                JsonParser.Event temp = lastEvent;
                lastEvent = null;
                return temp;
            }
            return parser.hasNext() ? parser.next(): null;
        }

        public JsonParser.Event peek() {
            if(lastEvent == null && parser.hasNext()) {
                lastEvent = parser.next();
            }
            return lastEvent;
        }

        public void beginObject() {
            JsonParser.Event event = getNext();
            if (event != JsonParser.Event.START_OBJECT) {
                throw new IllegalArgumentException("Not at start of object.  Got: " + event);
            }
        }

        public void endObject() {
            JsonParser.Event event = getNext();
            if (event != JsonParser.Event.END_OBJECT) {
                 throw new IllegalArgumentException("Not at end of object.  Got: " + event);
            }
        }

        public void beginArray() {
            JsonParser.Event event = getNext();
            if (event != JsonParser.Event.START_ARRAY) {
                throw new IllegalArgumentException("Not at start of Array.  Got: " + event);
            }
        }

        public void endArray() {
            JsonParser.Event event = getNext();
            if (event != JsonParser.Event.END_ARRAY) {
                throw new IllegalArgumentException("Not at end of Array.  Got: " + event);
            }
        }

        public String nextName() {
            JsonParser.Event event = getNext();
            if (event == KEY_NAME) {
                return parser.getString();
            }
            throw new IllegalArgumentException("No name found.  Got: " + event);
        }

        public int nextInt() {
            JsonParser.Event event = getNext();
            if (event == VALUE_NUMBER) {
                return Integer.parseInt(parser.getString());
            }
            throw new IllegalArgumentException("No number found.  Got: " + event);
        }

        public Long nextLong() {
            JsonParser.Event event = getNext();
            if (event == VALUE_NUMBER) {
                return Long.parseLong(parser.getString());
            }
            throw new IllegalArgumentException("No number found.  Got: " + event);
        }

        public boolean isBoolean(JsonParser.Event event) {
            return event == JsonParser.Event.VALUE_FALSE || event == JsonParser.Event.VALUE_TRUE;
        }

        public Boolean nextBoolean() {
            JsonParser.Event event = getNext();
            if (isBoolean(event)) {
                return event == JsonParser.Event.VALUE_FALSE? false: true;
            }
            throw new IllegalArgumentException("No number found.  Got: " + event);
        }

        public Double nextDouble() {
            JsonParser.Event event = getNext();
            if (event == VALUE_NUMBER) {
                return Double.parseDouble(parser.getString());
            }
            throw new IllegalArgumentException("No number found.  Got: " + event);
        }

        public String nextString() {
            JsonParser.Event event = getNext();
            if (event == VALUE_STRING) {
                return parser.getString();
            }
            throw new IllegalArgumentException("No string found.  Got: " + event);
        }

        public boolean atEndObjectOrArray(){
            JsonParser.Event event = peek();
            return event != null && (event == JsonParser.Event.END_ARRAY || event == JsonParser.Event.END_OBJECT);
        }

        public boolean hasNext() {
            if(!atEndObjectOrArray()) {
                return parser.hasNext() || lastEvent != null;
            }
            return false;
        }
    }

    JsonObjectReader parent;

    List<ObjectReadListener> objectReadListeners = new ArrayList<>();

    Class type;
    Object target;

    public static final String TAG = "jsonread";

    public void l(String s) {
        System.out.println(TAG + ": " + s);
    }

    public JsonObjectReader(String fileName, JsonObjectReader parent, Class type) throws ObjectReadException {
        this(fileName, type, parent, null);
    }

    public JsonObjectReader(String fileName, Class targetType) throws ObjectReadException {
        this(fileName, null, targetType);
    }

    public JsonObjectReader(JsonReader reader, Class targetType, JsonObjectReader parent) throws ObjectReadException {
        this.reader = reader;
        type = targetType;
        this.parent = parent;
        if(parent != null ) {
            this.setObjectReadListeners(parent.getObjectReadListeners());
        }
        mkTarget();
    }


    public JsonObjectReader(String fileName, Class type, JsonObjectReader parent, List<ObjectReadListener> objectReadListeners) throws ObjectReadException {
        try {
            parser = Json.createParser(new FileInputStream(new File(fileName)));

            reader = new JsonReader(parser);

            this.type = type;
            this.parent = parent;

            if (objectReadListeners != null) {
                this.setObjectReadListeners(objectReadListeners);
            }

            mkTarget();

        } catch (IOException e) {
            throw new ObjectReadException(e);
        }
    }

    private void mkTarget() throws ObjectReadException {
        target = invokeListenersOnCreate(type);
        if (target == null) {
            target = ReflectionUtils.newInstance(type);
        } else {
            this.type = target.getClass();
        }
        invokeListenersOnPostCreate();
    }

    @Override
    public Object read() throws ObjectReadException {
        UUID targetUUID = UUID.randomUUID();

        try {
            reader.beginObject();
            while (reader.hasNext()) {

                String fieldName = reader.nextName();
                l(fieldName);
                try {
                    Field field = type.getField(fieldName);
                    invokeListenersOnFieldName(fieldName, field, targetUUID);
                    field.set(target, readValueAndInvokeListeners(field));
                } catch (NoSuchFieldException e) {
                    invokeListenersOnFieldName(fieldName, null, targetUUID);
                }

            }
            reader.endObject();

        } catch (IllegalAccessException e) {
            throw new ObjectReadException(e);
        }

        invokeListenersOnReadDone(target, targetUUID);

        return target;
    }

    @Override
    public String nextFieldName() throws ObjectReadException {
        return reader.nextName();
    }

    @Override
    public Object nextValue() throws ObjectReadException {
        return readAnyObject(false);
    }

    @Override
    public void addObjectReadListener(ObjectReadListener objectReadListener) {
        objectReadListeners.add(objectReadListener);
    }

    @Override
    public void removeOjectReadListener(ObjectReadListener objectReadListener) {
        objectReadListeners.remove(objectReadListener);
    }

    protected void invokeListenersOnReadDone(Object value, Object id) throws ObjectReadException {
        for (ObjectReadListener objectReadListener : objectReadListeners) {
            objectReadListener.onReadDone(value, id, this);
        }
    }

    protected void invokeListenersOnFieldName(String fieldName, Field field, Object id) throws ObjectReadException {
        invokeListenersOnFieldName(fieldName, field, target, id);
    }

    protected void invokeListenersOnFieldName(String fieldName, Field field, Object theTarget, Object id) throws ObjectReadException {
        for (ObjectReadListener objectReadListener : objectReadListeners) {
            objectReadListener.onFieldName(fieldName, field, theTarget, id, this);
        }
    }

    protected Object invokeListenersOnValue(Object value, Field field) throws ObjectReadException {
        for (ObjectReadListener objectReadListener : objectReadListeners) {
            // values is chained through the listeners - hence order is important
            value = objectReadListener.onValue(value, field, this);
        }
        return value;
    }

    protected Object invokeListenersOnCreate(Class type) throws ObjectReadException {
        Object object = null;
        for (ObjectReadListener objectReadListener : objectReadListeners) {
            // values is chained through the listeners - hence order is important
            object = objectReadListener.onCreate(type, this);
        }
        return object;
    }

    protected void invokeListenersOnPostCreate() throws ObjectReadException {
        for (ObjectReadListener objectReadListener : objectReadListeners) {
            objectReadListener.onPostCreate(target, this);
        }
    }

    public Object readValue(Class fieldType) throws ObjectReadException {
        return readValue(fieldType, null);
    }

    public Object readValue(Field field) throws ObjectReadException {
        return readValue(field.getType(), field);
    }

    @Override
    public Object getTarget() {
        return target;
    }


    public Object readValue(Class fieldType, Field field) throws ObjectReadException {

        Object value = null;
        if (int.class == fieldType || Integer.class == fieldType) {
            value = reader.nextInt();
        } else if (long.class == fieldType || Long.class == fieldType) {
            value = reader.nextLong();
        } else if (double.class == fieldType || Double.class == fieldType) {
            value = reader.nextDouble();
        } else if (float.class == fieldType || Float.class == fieldType) {
            value = new Float(reader.nextDouble());
        } else if (boolean.class == fieldType || Boolean.class == fieldType) {
            value = reader.nextBoolean();
        } else if (String.class == fieldType) {
            value = reader.nextString();
        } else if (fieldType.isArray()) {
            value = readArray(fieldType);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            value = readMap(field);
        } else if (List.class.isAssignableFrom(fieldType)) {
            value = readList(field);
        } else {
            value = readObject(fieldType);
        }
        return value;
    }

    public Object readValueAndInvokeListeners(Class fieldType, Field field) throws ObjectReadException {
        return invokeListenersOnValue(readValue(fieldType, field), field);
    }

    public Object readValueAndInvokeListeners(Field field) throws ObjectReadException {
        return readValueAndInvokeListeners(field.getType(), field);
    }

    public Object readArray(Class fieldType) throws ObjectReadException {


        List list = new ArrayList();
        Class componentType = fieldType == null ? Object.class : fieldType.getComponentType();
        reader.beginArray();
        while (reader.hasNext()) {
            if (componentType == Object.class) {
                list.add(readAnyObject());
            } else {
                list.add(readValueAndInvokeListeners(componentType, null));
            }
        }
        reader.endArray();
        return (componentType == Object.class) ? list.toArray() : ReflectionUtils.toTypedArray(list, fieldType);

    }

    public Map readMap(Field field) throws ObjectReadException {

        HashMap map = new HashMap();

        Class valueType = field != null ? ReflectionUtils.getGenericType(field, 1) : Object.class;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();

            if (valueType == Object.class) {
                map.put(key, readAnyObject());
            } else {
                map.put(key, readValueAndInvokeListeners(valueType, null));
            }
        }
        reader.endObject();


        return map;
    }

    public List readList(Field field) throws ObjectReadException {
        List list = new ArrayList();

        Class valueType = field != null ? ReflectionUtils.getGenericType(field, 0) : Object.class;
        reader.beginArray();

        while (reader.hasNext()) {

            if (valueType == Object.class) {
                list.add(readAnyObject());
            } else {
                list.add(readValueAndInvokeListeners(valueType, null));
            }

        }
        reader.endArray();

        return list;
    }

    public Object readAnyObject() throws ObjectReadException {
        return readAnyObject(true);
    }

    public Object readObject(Class fieldType) throws ObjectReadException {
        if (fieldType == Object.class) {
            return readAnyObject();
        } else {
            return (new JsonObjectReader(reader, fieldType, this)).read();
        }
    }

    public Object readAnyObject(boolean invokeListeners) throws ObjectReadException {


        Object value = null;
        JsonParser.Event jsonToken = reader.peek();
        if (jsonToken == JsonParser.Event.START_ARRAY) {

            List list = new ArrayList<>();

            reader.beginArray();
            while (reader.hasNext()) {
                list.add(readAnyObject(invokeListeners));
            }
            reader.endArray();

            value = list;

        } else if (jsonToken == JsonParser.Event.START_OBJECT) {

            Map map = new HashMap();
            l("make map: " + map.toString());
            UUID mapUUID = UUID.randomUUID();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                l("read name: " + name);

                if (invokeListeners) invokeListenersOnFieldName(name, null, map, mapUUID);

                map.put(name, readAnyObject(invokeListeners));
            }
            reader.endObject();
            value = map;

            l("done map: " + map.toString());

            if (invokeListeners) invokeListenersOnReadDone(value, mapUUID);

        } else if (reader.isBoolean(jsonToken)) {
            value = reader.nextBoolean();
        } else if (jsonToken == JsonParser.Event.VALUE_NUMBER) {
            value = reader.nextInt();
        } else if (jsonToken == JsonParser.Event.VALUE_STRING) {
            value = reader.nextString();
        }

        if (invokeListeners) {
            return invokeListenersOnValue(value, null);
        } else {
            return value;
        }

    }

    public Map readRefMap() throws ObjectReadException {

        HashMap map = new HashMap();

        reader.beginObject();

        String name = reader.nextName();
        String typeClassName = reader.nextString();
        Class beanClass = ReflectionUtils.getClass(typeClassName);


        if (beanClass != null) {
            name = reader.nextName();

            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                map.put(key, readValue(beanClass));
            }
            reader.endObject();

        }
        reader.endObject();

        return map;

    }

    public List<ObjectReadListener> getObjectReadListeners() {
        return objectReadListeners;
    }

    public void setObjectReadListeners(List<ObjectReadListener> objectReadListeners) {
        this.objectReadListeners = objectReadListeners;
    }


}