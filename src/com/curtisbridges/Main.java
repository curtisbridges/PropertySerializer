package com.curtisbridges;

import javax.swing.JButton;

public class Main {

    public void run() {
        // Example of using a static class
        Inspector inspector1 = new Inspector(System.class);
        inspector1.addListener(new XMLSerializer(System.out));
        // inspector1.run();

        // Example of a more complex object instance
        JButton button = new JButton("Test");
        Inspector inspector2 = new Inspector(button);
        inspector2.addListener(new XMLSerializer(System.out));
        // inspector2.run();

        // Example of using a standard class instance
        // PropertyListener listener = new PropertyListener();

        Inspector inspector3 = new Inspector(new TestObject());
        inspector3.addListener(new XMLSerializer(System.out));
        inspector3.run();
    }

    public static void main(String[] argv) {
        Main test = new Main();
        test.run();
    }

    class TestObject {
        int    testInt    = 24;
        double testDouble = 2.4;
        String testString = "testString";
        int[]  numbers    = { 1, 2, 3, 4 };
        long   testLong   = Long.MAX_VALUE;
    }
}
