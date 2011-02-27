package com.curtisbridges;

import java.io.PrintStream;

class XMLSerializer implements Inspector.InspectorListener {
    PrintStream out;

    public XMLSerializer(PrintStream out) {
        this.out = out;
    }

    @Override
    public void classFound(String name) {
        out.println("<class name=\"" + name + "\">");
    }

    @Override
    public void classCompleted() {
        out.println("</class>");
    }

    @Override
    public void fieldFound(Class<?> type, String name) {
        String typeName = type.getName();
        if (type == String.class)
            typeName = "String";

        out.println("<field type=\"" + typeName + "\"" + " name=\"" + name + "\"/>");
    }

    @Override
    public void fieldFound(Class<?> type, String name, Object value) {
        String typeName = type.getName();
        if (type == String.class)
            typeName = "String";

        if (value == null)
            value = "";

        out.println("<field type=\"" + typeName + "\"" + " name=\"" + name + "\">" + value + "</field>");
    }

    @Override
    public void arrayBegin(Class<?> type, String name) {
        out.println("<field type=\"array\" name=\"" + name + "\">");
    }

    @Override
    public void arrayEnd() {
        out.println("</field>");
    }
}
