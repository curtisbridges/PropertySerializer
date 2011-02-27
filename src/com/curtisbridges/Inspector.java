package com.curtisbridges;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class Inspector implements Runnable {
    private final Object                  inspected;
    private final List<InspectorListener> listeners;

    public Inspector(Object theObject) {
        inspected = theObject;
        listeners = new LinkedList<InspectorListener>();
    }

    public void addListener(InspectorListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(InspectorListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /* BEGIN The fire methods */
    protected void fireClassFound(String name) {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.classFound(name);
            }
        }
    }

    protected void fireClassCompleted() {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.classCompleted();
            }
        }
    }

    protected void fireFieldFound(Class<?> type, String name) {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.fieldFound(type, name);
            }
        }
    }

    protected void fireFieldFound(Class<?> type, String name, Object value) {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.fieldFound(type, name, value);
            }
        }
    }

    protected void fireArrayBegin(Class<?> type, String name) {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.arrayBegin(type, name);
            }
        }
    }

    protected void fireArrayEnd() {
        synchronized (listeners) {
            for (int index = listeners.size() - 1; index >= 0; index--) {
                InspectorListener listener = listeners.get(index);
                listener.arrayEnd();
            }
        }
    }

    /* END The fire methods */

    /** The Runnable interface */
    @Override
    public void run() {
        fireClassFound(inspected.getClass().getName());
        process(inspected);
        fireClassCompleted();
    }

    private void process(Object theObject) {
        if (theObject instanceof Class) {
            Class<?> theClass = (Class<?>) theObject;
            fireClassFound(theClass.getName());

            processClass(theClass);

            fireClassCompleted();
        }
        else {
            processObject(theObject);
        }
    }

    private void processObject(Object someObject) {
        Class<?> itsClass = someObject.getClass();
        Field fields[] = itsClass.getFields();

        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];

            // String mods = field.getModifiers();
            Class<?> fieldType = field.getType();
            String name = field.getName();

            try {
                if (fieldType.isArray()) {
                    fireArrayBegin(fieldType, name);
                    processArray(field.get(someObject));
                    fireArrayEnd();
                }
                else {
                    Object fieldValue = field.get(someObject);
                    fireFieldFound(fieldType, name, fieldValue);
                }
            }
            catch (IllegalAccessException exc) {
                // as a debug for now
                exc.printStackTrace();
            }
        }
    }

    private void processClass(Class<?> someclass) {
        Field fields[] = someclass.getFields();

        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];

            // String mods = field.getModifiers();
            Class<?> fieldType = field.getType();
            String name = field.getName();

            Object value = null;
            try {
                if (Modifier.isStatic(field.getModifiers()))
                    value = field.get(null);
            }
            catch (IllegalAccessException exc) {
                exc.printStackTrace();
            }

            if (fieldType.isArray()) {
                fireArrayBegin(fieldType, name);
                fireArrayEnd();
            }
            else {
                fireFieldFound(fieldType, name, value);
            }
        }
    }

    private void processArray(Object array) {
        for (int index = 0; index < Array.getLength(array); index++) {
            Object element = Array.get(array, index);
            processSemiPrimitive(element);
        }
    }

    private void processSemiPrimitive(Object object) {
        Class<?> itsclass = object.getClass();

        if (itsclass.isPrimitive() || Number.class.isAssignableFrom(itsclass)) {
            fireFieldFound(itsclass, "", object);
        }
        else if (itsclass == String.class) {
            fireFieldFound(itsclass, "", (object != null) ? object : "");
        }
        else {
            Field fields[] = itsclass.getFields();

            try {
                for (int index = 0; index < fields.length; index++) {
                    Field field = fields[index];
                    final Object fieldValue = field.get(object);
                    processObject(fieldValue);
                }
            }
            catch (Exception exc) {
                System.out.println("Problem accessing field: " + exc.getMessage());
            }
        }
    }

    public interface InspectorListener {
        void classFound(String name);

        void classCompleted();

        void fieldFound(Class<?> type, String name);

        void fieldFound(Class<?> type, String name, Object value);

        void arrayBegin(Class<?> type, String name);

        void arrayEnd();
    };
}
