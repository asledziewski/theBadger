package explorer;

import fx.Main;
import javassist.ClassPool;
import javassist.Modifier;
import javassist.NotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;


public class Explorer {
    public static ArrayList<JarEntry> files = new ArrayList<>();
    public static ClassPool classPool;
    public static Manifest manifest;
    public static JarFile jarrFile;

    private static ArrayList<String> getClassNames(String jarName) {
        ArrayList<String> classes = new ArrayList<>();
        try {
            jarrFile = new JarFile(jarName);
            files = new ArrayList<>();

            JarInputStream jarFile = new JarInputStream(new FileInputStream(
                    jarName));
            manifest = jarFile.getManifest();
            JarEntry jarEntry;
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    classes.add(jarEntry.getName().replaceAll("/", "\\."));
                } else if (!jarEntry.getName().endsWith("/")) {
                    files.add(jarEntry);
                }
            }
        } catch (IOException e) {
            Main.showAlert("Something's wrong with this jar file.");
        }
        return classes;
    }

    public static ArrayList<MyClass> getClasses(String path) {
        ArrayList<String> list = getClassNames(path);
        classPool = ClassPool.getDefault();
        ArrayList<MyClass> classes = new ArrayList<>();
        try {
            classPool.insertClassPath(path);
            ClassPool.doPruning = false;

            for (String aList : list) {
                classes.add(new MyClass(classPool.get(aList.replaceAll(".class", ""))));
            }
        } catch (NotFoundException e) {
            Main.showAlert("Error while reading class files.");
        }
        return classes;
    }

    public static String decodeMod(int mod) {
        StringBuilder mods = new StringBuilder();
        if (Modifier.isPublic(mod)) mods.append("public ");
        if (Modifier.isPrivate(mod)) mods.append("private ");
        if (Modifier.isProtected(mod)) mods.append("protected ");
        if (Modifier.isFinal(mod)) mods.append("final ");
        if (Modifier.isInterface(mod)) mods.append("interface ");
        if (Modifier.isStatic(mod)) mods.append("static ");
        return mods.toString();
    }

    public static int getMod(String[] mods) {
        int mod = 0;
        for (String mod1 : mods) {
            switch (mod1) {
                case "public": {
                    mod += Modifier.PUBLIC;
                    break;
                }
                case "protected": {
                    mod += Modifier.PROTECTED;
                    break;
                }
                case "private": {
                    mod += Modifier.PRIVATE;
                    break;
                }
                case "static": {
                    mod += Modifier.STATIC;
                    break;
                }
                case "final": {
                    mod += Modifier.FINAL;
                    break;
                }
                case "static final": {
                    mod += Modifier.STATIC;
                    mod += Modifier.FINAL;
                    break;
                }
                case "interface": {
                    mod += Modifier.INTERFACE;
                    break;
                }
                case "abstract": {
                    mod += Modifier.ABSTRACT;
                    break;
                }
                default: {
                    mod += 0;
                    break;
                }
            }
        }
        return mod;
    }


}

