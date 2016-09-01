package org.androware.flow.builder;


import javax.json.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Created by jkirkley on 9/1/16.
 */
public class JsonObjectWriter {

    public static class JsonWriterWrapper {

        JsonWriter writer;
        JsonObjectBuilder jsonObjectBuilder = null;
        JsonArrayBuilder jsonArrayBuilder = null;
        Stack<BuilderAndName> builderStack;
        String currName = null;

        public class BuilderAndName {
            Object builder;
            String name;
            public BuilderAndName(Object builder, String name) {
                this.name = name;
                this.builder = builder;
            }

            public void add(JsonObjectBuilder jsonObjectBuilder){
                if(builder instanceof JsonObjectBuilder) {
                    jsonObjectBuilder.add(name, (JsonObjectBuilder) builder);
                } else {
                    jsonObjectBuilder.add(name, (JsonArrayBuilder) builder);
                }
            }
            public void add(JsonArrayBuilder jsonArrayBuilder){
                if(builder instanceof JsonObjectBuilder) {
                    jsonArrayBuilder.add((JsonObjectBuilder) builder);
                } else {
                    jsonArrayBuilder.add((JsonArrayBuilder) builder);
                }
            }

            public void write(JsonWriter writer) {
                if(builder instanceof JsonObjectBuilder) {
                    writer.writeObject(((JsonObjectBuilder) builder).build());
                } else {
                    writer.writeArray(((JsonArrayBuilder) builder).build());
                }
            }

        }

        public JsonWriterWrapper(OutputStreamWriter outputStreamWriter) {
            builderStack = new Stack();
            writer = Json.createWriter(outputStreamWriter);
        }

        public void close() {
            /*
            if (jsonObjectBuilder != null) {
                writer.writeObject(jsonObjectBuilder.build());
            } else if (jsonArrayBuilder != null) {
                writer.writeArray(jsonArrayBuilder.build());
            }
            */
            writer.close();
        }

        public void beginObject() {

            jsonObjectBuilder = Json.createObjectBuilder();
            builderStack.push(new BuilderAndName(jsonObjectBuilder, currName));
        }

        public void endObject() {

            BuilderAndName builderAndName = builderStack.pop();

            if (!builderStack.empty()) {
                Object prev = builderStack.peek().builder;
                if (prev instanceof JsonObjectBuilder) {
                    jsonObjectBuilder = (JsonObjectBuilder) prev;
                    builderAndName.add(jsonObjectBuilder);
                } else {
                    jsonArrayBuilder = (JsonArrayBuilder) prev;
                    jsonObjectBuilder = null;
                    builderAndName.add(jsonArrayBuilder);

                }
            } else {
                builderAndName.write(writer);
                close();
            }
        }

        public void beginArray() {
            jsonArrayBuilder = Json.createArrayBuilder();
            builderStack.push(new BuilderAndName(jsonArrayBuilder, currName));

        }

        public void endArray() {
            BuilderAndName builderAndName = builderStack.pop();

            if (!builderStack.empty()) {
                Object prev = builderStack.peek().builder;
                if (prev instanceof JsonObjectBuilder) {
                    jsonObjectBuilder = (JsonObjectBuilder) prev;
                    builderAndName.add(jsonObjectBuilder);
                    jsonArrayBuilder = null;
                } else {
                    jsonArrayBuilder = (JsonArrayBuilder) prev;
                    builderAndName.add(jsonArrayBuilder);
                }
            } else {
                close();
            }
        }

        public void name(String n) {
            currName = n;
        }

        public void value(Object value) {

            Class fieldType = value.getClass();

            if (currName == null && jsonArrayBuilder != null) {

                if (int.class == fieldType || Integer.class == fieldType) {
                    jsonArrayBuilder.add((int) value);
                } else if (long.class == fieldType || Long.class == fieldType) {
                    jsonArrayBuilder.add((long) value);
                } else if (double.class == fieldType || Double.class == fieldType) {
                    jsonArrayBuilder.add((double) value);
                } else if (float.class == fieldType || Float.class == fieldType) {
                    jsonArrayBuilder.add((float) value);
                } else if (boolean.class == fieldType || Boolean.class == fieldType) {
                    jsonArrayBuilder.add((boolean) value);
                } else if (String.class == fieldType) {
                    jsonArrayBuilder.add((String) value);
                }

            } else {

                if (int.class == fieldType || Integer.class == fieldType) {
                    jsonObjectBuilder.add(currName, (int) value);
                } else if (long.class == fieldType || Long.class == fieldType) {
                    jsonObjectBuilder.add(currName, (long) value);
                } else if (double.class == fieldType || Double.class == fieldType) {
                    jsonObjectBuilder.add(currName, (double) value);
                } else if (float.class == fieldType || Float.class == fieldType) {
                    jsonObjectBuilder.add(currName, (float) value);
                } else if (boolean.class == fieldType || Boolean.class == fieldType) {
                    jsonObjectBuilder.add(currName, (boolean) value);
                } else if (String.class == fieldType) {
                    jsonObjectBuilder.add(currName, (String) value);
                }

            }
        }
    }

    JsonWriterWrapper jsonWriterWrapper;
    public final static String TAG = "jsonwrite";

    public void l(String s) {
        System.out.println(TAG + ": " + s);
    }

    public JsonObjectWriter(OutputStream out) throws IOException {
        jsonWriterWrapper = new JsonWriterWrapper(new OutputStreamWriter(out, "UTF-8"));
    }

    public void close() throws IOException {
        jsonWriterWrapper.close();
    }


    public void write(Object object) throws IOException {
        Class type = object.getClass();

        Field fields[] = type.getFields();
        try {

            jsonWriterWrapper.beginObject();
            for (Field f : fields) {

                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                String fieldName = f.getName();
                Class fieldType = f.getType();

                Object value = f.get(object);

                l("write: " + fieldName + " : " + value);
                if (value != null) {
                    jsonWriterWrapper.name(fieldName);
                    writeValue(fieldType, value);
                } else {
                    //l("field: " + fieldName + " is null.");
                }
            }
            jsonWriterWrapper.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void writeArray(Object array) throws IOException {
        jsonWriterWrapper.beginArray();

        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            Object arrayElement = Array.get(array, i);
            if(arrayElement == null) {
                arrayElement = "null";
            }

            Class componentType = arrayElement.getClass();

            writeValue(componentType, arrayElement);
        }
        jsonWriterWrapper.endArray();
    }

    public void writeValue(Class fieldType, Object value) throws IOException {
        if(value == null) {
            value = "null";
        }


        if (int.class == fieldType || Integer.class == fieldType) {
            jsonWriterWrapper.value((int) value);
        } else if (long.class == fieldType || Long.class == fieldType) {
            jsonWriterWrapper.value((long) value);
        } else if (double.class == fieldType || Double.class == fieldType) {
            jsonWriterWrapper.value((double) value);
        } else if (float.class == fieldType || Float.class == fieldType) {
            jsonWriterWrapper.value((float) value);
        } else if (boolean.class == fieldType || Boolean.class == fieldType) {
            jsonWriterWrapper.value((boolean) value);
        } else if (String.class == fieldType) {
            jsonWriterWrapper.value((String) value);
        } else if (fieldType.isArray()) {
            writeArray(value);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            writeMap((Map) value);
        } else if (List.class.isAssignableFrom(fieldType)) {
            writeList((List) value);
        } else {
            write(value);
        }
    }

    public void writeMap(Map map) throws IOException {

        try {

            if (map == null) {
                jsonWriterWrapper.beginObject();
                jsonWriterWrapper.endObject();
                return;
            }


            jsonWriterWrapper.beginObject();
            for (Object k : map.keySet()) {
                String key = (String) k;
                jsonWriterWrapper.name(key);

                Object value = map.get(key);
                if(value == null) {
                    value = "null";
                }
                Class fieldType = value.getClass();

                writeValue(fieldType, value);
            }
            jsonWriterWrapper.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeList(List list) {

        try {

            if (list == null) {
                // writer empty array
                jsonWriterWrapper.beginArray();
                jsonWriterWrapper.endArray();
                return;
            }


            jsonWriterWrapper.beginArray();
            for (Object value : list) {
                Class fieldType = value.getClass();

                writeValue(fieldType, value);

            }
            jsonWriterWrapper.endArray();

        } catch (IOException e) {
        }

    }

}

