package io.github.cj_cate.ideationitems.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.lang.System.out;

/*
    100% credit to @Wonkglorg (id: 295632220339830784) on discord.
    Madlad wrote this up for me on a whim and it just works.
 */
public class JarPackageScanner{

    /**
     * Find all classes in a package.
     *
     * @param packageName The package to search.
     * @param scanSubPackages Whether to scan sub-packages or just the specified one
     * @return A list of classes in the package.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    public static List<Class<?>> findClassesInPackage(String packageName, boolean scanSubPackages) throws IOException, ClassNotFoundException {
        return findClassesInPackage(packageName, Objects.class, scanSubPackages);
    }

    /**
     * Find all classes in a package.
     *
     * @param packageName The package to search.
     * @param parent the parent of this class
     * @param scanSubPackages Whether to scan sub-packages or just the specified one
     * @return A list of classes in the package.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> findClassesInPackage(String packageName, Class<?> parent, boolean scanSubPackages)
            throws IOException, ClassNotFoundException {
        return findClassesInPackage(packageName, parent::isAssignableFrom, scanSubPackages);
    }

    /**
     * Find all classes in a package that match a predicate.
     *
     * @param packageName The package to search.
     * @param predicate The predicate to match classes against.
     * @param scanSubPackages Whether to scan sub-packages or just the specified one
     * @return A list of classes in the package that match the predicate.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    public static List<Class<?>> findClassesInPackage(String packageName, Predicate<Class<?>> predicate, boolean scanSubPackages)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);

        while(resources.hasMoreElements()){
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            out.println(protocol);
            if("file".equals(protocol)){ // resource file
                classes.addAll(findClassesInDirectory(resource.getPath(), packageName, predicate, scanSubPackages));
            }
        }
        return classes;
    }

    public static List<Class<?>> findClassesInJar(String packagePath) throws IOException, ClassNotFoundException {
        return findClassesInJar(packagePath, Object.class::isAssignableFrom, false);
    }

    public static List<Class<?>> findClassesInJar(String packagePath, boolean scanSubPackages) throws IOException, ClassNotFoundException {
        return findClassesInJar(packagePath, Object.class::isAssignableFrom, scanSubPackages);
    }

    /**
     * Find all classes in a JAR file.
     *
     * @param packagePath The package to search.
     * @param predicate The predicate to match classes against.
     * @param scanSubPackages Whether to scan sub-packages or just the specified one
     * @return A list of classes in the package.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    public static List<Class<?>> findClassesInJar(String packagePath, Predicate<Class<?>> predicate, boolean scanSubPackages)
            throws IOException, ClassNotFoundException {
        return findClassesInJar(new File(URLDecoder.decode(JarPackageScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                StandardCharsets.UTF_8)), packagePath, predicate, scanSubPackages);
    }

    /**
     * Find all classes in a JAR file.
     *
     * @param jarFile The JAR file to search.
     * @param packagePath The package to search.
     * @param predicate The predicate to match classes against.
     * @param scanSubPackages Whether to scan sub-packages or just the specified one
     * @return A list of classes in the package.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    public static List<Class<?>> findClassesInJar(File jarFile, String packagePath, Predicate<Class<?>> predicate, boolean scanSubPackages)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        try(JarFile jar = new JarFile(jarFile)){
            Enumeration<JarEntry> entries = jar.entries();

            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if(entryName.endsWith(".class") && entryName.startsWith(packagePath.replace('.', '/'))){
                    if(scanSubPackages || entryName.lastIndexOf('/') == packagePath.length()){
                        String className = entryName.replace('/', '.').substring(0, entryName.length() - ".class".length());

                        Class<?> clazz = Class.forName(className);
                        if(predicate.test(clazz)){
                            classes.add(clazz);
                        }
                    }
                }
            }
        }

        return classes;
    }

    private static List<Class<?>> findClassesInDirectory(String directoryPath, String packageName, Predicate<Class<?>> predicate, boolean scanDeep)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File directory = new File(URLDecoder.decode(directoryPath, StandardCharsets.UTF_8));

        if(directory.exists() && directory.isDirectory()){
            for(File file : Objects.requireNonNull(directory.listFiles())){
                if(file.isDirectory() && scanDeep){
                    classes.addAll(findClassesInDirectory(file.getAbsolutePath(), packageName + "." + file.getName(), predicate, scanDeep));
                } else if(file.getName().endsWith(".class")){
                    String className = packageName + '.' + file.getName().replace(".class", "");
                    Class<?> foundClass = Class.forName(className);
                    if(predicate.test(foundClass)){
                        classes.add(foundClass);
                    }
                }
            }
        }
        return classes;
    }

    /**
     * Create an instance of a class.
     *
     * @param clazz The class to create an instance of
     * @param args The arguments to pass to the constructor
     * @param <T>
     * @return The created instance
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) {
        try{
            return clazz.getDeclaredConstructor().newInstance(args);
        } catch(Exception e){
            throw new RuntimeException("Failed to create instance of class " + clazz.getName(), e);
        }

    }
}