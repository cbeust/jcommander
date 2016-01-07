package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <p>Test that parent classes and interfaces are used correctly</p>
 */
public class TypeHierarchyTest {

    public interface Marker {
        @Parameter(names = {"--available"})
        void setAvailable(boolean available);
    }

    public class Base implements Marker {
        boolean available = false;

        public boolean isAvailable() {
            return available;
        }

        @Override
        public void setAvailable(boolean available) {
            this.available = true;
        }
    }

    public interface IMiddle {
        @Parameter(names = {"--count"})
        void setCount(int count);
    }

    public interface IMiddleMiddle extends IMiddle {
        @Parameter(names = {"--again"})
        void setCountAgain(int count);
    }

    public class Middle extends Base implements IMiddleMiddle {

        private int count;
        private int again;

        public int getCount() {
            return count;
        }

        @Override
        public void setCount(int count) {
            this.count = count;
        }

        public int getCountAgain() {
            return again;
        }

        @Override
        public void setCountAgain(int again) {
            this.again = again;
        }
    }

    // trying to trip it up and get it to go from Child -> Composite -> Visitor
    public interface Composite extends Visitor {
        @Parameter(names = {"-n", "--name"})
        void setName(String validate);
    }

    public interface Visitor {
        @Parameter(names = {"--validate"})
        void setValidate(boolean validate);
    }

    public class Child extends Middle implements Composite, Visitor{

        private String name;
        private boolean validate = false;

        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        public boolean isValidate() {
            return validate;
        }

        @Override
        public void setValidate(boolean validate) {
            this.validate = validate;
        }
    }

    @Test
    public void testTypeHierarchy() {
        final Child child = new Child();
        JCommander commander = new JCommander(child);
        commander.parse("--validate","-n","child-one","--count","14","--again","22","--available");

        // test values through entire hierarchy
        Assert.assertTrue(child.isValidate());
        Assert.assertTrue(child.isAvailable());
        Assert.assertEquals(14, child.getCount());
        Assert.assertEquals(22, child.getCountAgain());
        Assert.assertEquals("child-one", child.getName());
    }
}
