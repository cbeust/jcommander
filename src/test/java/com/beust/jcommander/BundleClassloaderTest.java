package com.beust.jcommander;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/*
 * This test is designed to simulate the scenario where @Parameter(s) annotations reference a ResourceBundle which is
 * available from the ClassLoader that contains the declaring class (eg. our Command.class) but not the ClassLoader used
 * by JCommander.  See <a href="https://github.com/cbeust/jcommander/issues/599">...</a>.
 *
 * Implementing the correct behavior within JCommander is as simple as passing the correct ClassLoader to
 * ResourceBundle.getBundle() calls but verifying the behavior through unit tests in more challenging. These tests work
 * by declaring an argument object that references a ResourceBundle that does not exist anywhere on the classpath.  The
 * missing properties resource bundle is then created and added to the ClassPath of a custom ClassLoader.  A custom
 * ClassLoader is required to force the argument object to be re-loaded alongside the resource bundle.
 */
public class BundleClassloaderTest {

    @Parameters(resourceBundle = "MyBundle", commandNames = "test", commandDescriptionKey = "description")
    public static class Command {

        @Parameter(names = "-option", descriptionKey="option")
        public String host;
    }

    @Test
    public void testBundleAvailableFromDifferentClassLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

        /*
        This test is designed to simulate the scenario where @Parameter(s) annotations reference a ResourceBundle which
        available from the ClassLoader that contains the declaring class (eg. our Command.class) but not the ClassLoader
        used by JCommander.  See https://github.com/cbeust/jcommander/issues/599.

        Implementing the correct behavior within JCommander is as simple as passing the correct ClassLoader to
        ResourceBundle.getBundle() calls but verifying the behavior through unit tests in more challenging.
         */

        Locale.setDefault(new Locale("en", "US"));

        Object command = loadClassAndBundleWithIsolatedClassLoader(Command.class, "MyBundle_en_US.properties", String.join("\n",
                "option = Option",
                "description = A command description"));

        final JCommander jc = JCommander.newBuilder()
                .addCommand(command)
                .build();

        JCommander test = jc.findCommandByAlias("test");

        final ParameterDescription pd = test.getParameters().get(0);
        Assert.assertEquals(pd.getDescription(), "Option");

        final StringBuilder sb = new StringBuilder();
        jc.usage(sb);

        final String usage = sb.toString();
        Assert.assertTrue(usage.contains("A command description"));
    }

    public Object loadClassAndBundleWithIsolatedClassLoader(Class<?> clazz, String bundleName, String bundle) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        // build a classpath based on Java's system classpath
        final List<URL> classPath = new ArrayList<>();

        final String[] systemClassPath = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String path : systemClassPath) {
            classPath.add(Paths.get(path).toUri().toURL());
        }

        // create bundle in a temporary file and add it to our classpath
        final Path temporaryDirectory = Files.createTempDirectory("ut");
        temporaryDirectory.toFile().deleteOnExit();

        Files.write(temporaryDirectory.resolve(bundleName), bundle.getBytes());

        classPath.add(temporaryDirectory.toUri().toURL());

        // force the requested class to be constructed within its own classloader
        final ClassLoader isolatedClassLoader = new IsolatedClassLoader(classPath.toArray(new URL[0]), clazz);
        final Class<?> isolatedClass = isolatedClassLoader.loadClass(clazz.getName());
        return isolatedClass.newInstance();
    }

    public static class IsolatedClassLoader extends URLClassLoader {

        private final String blockedClassName;

        public IsolatedClassLoader(URL[] urls, Class<?> blockedClazz) {
            super(urls, IsolatedClassLoader.class.getClassLoader());

            blockedClassName = blockedClazz.getName();
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

            Class<?> clazz = findLoadedClass(name);

            if (clazz == null) {

                if (!name.equals(blockedClassName)) {
                    try {
                        // not our blocked class, delegate to parent
                        clazz = getParent().loadClass(name);

                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // not a parent class, continue
                    }
                }

                if (clazz == null) {
                    // can't load from parent, load in URL classloader
                    clazz = super.findClass(name);
                }
            }

            if (resolve) {
                resolveClass(clazz);
            }

            return clazz;
        }
    }
}
