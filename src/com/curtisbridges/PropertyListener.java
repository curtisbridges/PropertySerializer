package com.curtisbridges;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

class PropertyListener implements Inspector.InspectorListener {
    private String             name;
    private final Properties   props;

    private boolean            isArray = false;
    private final List<String> array;
    private String             arrayName;

    public PropertyListener() {
        props = new Properties();
        array = new ArrayList<String>();
    }

    @Override
    public void classFound(String name) {
        this.name = name;
    }

    @Override
    public void classCompleted() {
        try {
            OutputStream out = new FileOutputStream(name + ".props");
            props.store(out, name);
            out.close();
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void fieldFound(Class<?> type, String name) {
        String typeName = type.getName();
        if (type == String.class)
            typeName = "String";

        System.out.println("Field found: " + name + "(" + type + ")");

        props.put(name, "");
    }

    @Override
    public void fieldFound(Class<?> type, String name, Object value) {
        String typeName = type.getName();

        // type = convertType(type);
        // value = convertValue(value);

        if (type == String.class)
            typeName = "String";

        if (value == null)
            value = "";

        String stringVal = value.toString();
        System.out.println("Field found: " + name + "(" + type + ") = " + stringVal);
        if (!isArray)
            props.put(name, stringVal);
        else
            array.add(stringVal);
    }

    @Override
    public void arrayBegin(Class<?> type, String name) {
        isArray = true;
        arrayName = name;
    }

    @Override
    public void arrayEnd() {
        String arrayValue = convertArray(array);
        array.clear();

        props.put(arrayName, arrayValue);
        isArray = false;
    }

    private String convertArray(List<String> array) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = array.iterator();
        while (iter.hasNext()) {
            Object object = iter.next();

            buffer.append(object);
            if (iter.hasNext())
                buffer.append(",");
        }

        return buffer.toString();
    }
}
