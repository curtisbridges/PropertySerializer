package com.curtisbridges;

import javax.swing.JButton;

public class Main {

    public void run() {
        // Example of using a static class
        // Inspector inspector = new Inspector(Defaults.class);
        // inspector.addListener(new XMLSerializer(System.out));
        // inspector.run();

        JButton button = new JButton("Test");
        Inspector inspector = new Inspector(button);
        inspector.addListener(new XMLSerializer(System.out));
        inspector.run();

        // Example of using a standard class instance
// PropertyListener listener = new PropertyListener();

// Inspector inspector = new Inspector(new TestObject());
// inspector.addListener(listener);
// inspector.run();
    }

    public static void main(String[] argv) {
        Main test = new Main();
        test.run();
    }

    class TestObject {
        public int    testInt    = 24;
        public double testDouble = 2.4;
        public String testString = "testString";
        public int[]  numbers    = { 1, 2, 3, 4 };
        public long   testLong   = Long.MAX_VALUE;
    }
}
