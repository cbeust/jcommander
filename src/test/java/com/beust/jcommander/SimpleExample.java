package com.beust.jcommander;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.Assert.assertEquals;

class Main {
    static PrintWriter out;
    @Parameter(names={"--length", "-l"})
    int length;
    @Parameter(names={"--pattern", "-p"})
    int pattern;

    public static void main(String ... args) {
        Main main = new Main();
        new JCommander(main, args);
        main.run();
    }

    public void run() {
        out.printf("%d %d", length, pattern);
    }
}

public class SimpleExample {
    StringWriter out;

    @BeforeMethod
    public void setupMain(){
        out=new StringWriter();
        Main.out=new PrintWriter(out);
    }

    @Test
    public void testLongArgs() {
        Main.main("--length", "512", "--pattern", "2");
        assertEquals("512 2", out.toString());
    }

    @Test
    public void testShortArgs() {
        Main.main("-l", "256", "-p", "171");
        assertEquals("256 171", out.toString());
    }

}
