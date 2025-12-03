package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

public class ListOfPathTest {

    @Test
    public void testParse() {
        // given
        final class Args {
            @Parameter(names = { "--paths"}, description = "List of paths separated by comma")
            private List<Path> paths = List.of();
        }
        final Args args = new Args();

        // when
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse("--paths", "/home/foo,/var/lib/bar");

        // then
        Assert.assertEquals(args.paths, List.of(Path.of("/home/foo"), Path.of("/var/lib/bar")));
    }

    public static void main(String[] args) {
        new ListOfPathTest().testParse();
    }
}
