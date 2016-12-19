package org.androware.flow.base;

/**
 * Created by jkirkley on 12/16/16.
 */
public class Field2WidgetIdMapper {

    public Field2WidgetIdMapper() {}
    public Field2WidgetIdMapper(String fieldName, String widgetId) {
        this.fieldName = fieldName;
        this.widgetId = widgetId;
    }

    public String fieldName;
    public String widgetId;

    public String toString() {
        return fieldName + " -> " + widgetId;
    }
}
