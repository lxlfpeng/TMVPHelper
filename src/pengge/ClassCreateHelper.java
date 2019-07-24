package pengge;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassCreateHelper {
    static final int CONTRACT = 0;
    static final int MODEL = 0;
    static final int PRESENTER = 1;
    static final int VIEW = 2;

    public static void createInterface(String path, String className, String classFullName, int mode) throws IOException {
        String type = null;
        if (mode == 0) {
            type = "Model";
        } else if (mode == PRESENTER) {
            type = "Presenter";
        } else if (mode == VIEW) {
            type = "View";
        }
        String dir = path + type.toLowerCase() + "/";
        path = dir + className + type + ".java";
        File dirs = new File(dir);
        System.out.println("dirs = " + dir);
        File file = new File(path);
        if (!dirs.exists()) {
            dirs.mkdir();
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
        writer.write("package " + getPackageName(path) + type.toLowerCase() + ";");
        writer.newLine();
        writer.newLine();
        Date date = new Date();
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + new SimpleDateFormat("yyyy/MM/dd").format(date) + "\n*/");
        writer.newLine();
        writer.newLine();
        writer.write("public interface " + className + type + "{");
        writer.newLine();
        writer.newLine();
        writer.write("}");
        writer.flush();
        writer.close();
    }

    public static void createImplClass(String path, String className, String classFullName, int mode, int tag) throws IOException {
        String type = null;
        if (mode == 0) {
            type = "Model";
        } else if (mode == PRESENTER) {
            type = "Presenter";
        }
        String dir = path + type.toLowerCase() + "/";
        path = dir + className + type + ".java";
        File dirs = new File(dir);
        File file = new File(path);
        String packageName = getPackageName(path);
        System.out.println(packageName);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
        writer.write("package " + packageName + type.toLowerCase() + ";");
        writer.newLine();
        if (tag == 0) {
            writer.write("import " + packageName + "contract." + classFullName + ";");
        }
        writer.newLine();
        writer.newLine();
        Date date = new Date();
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + new SimpleDateFormat("yyyy/MM/dd").format(date) + "\n*/");
        writer.newLine();
        writer.newLine();
        if (tag == 0) {
            writer.write("public class " + className + type + " extends " + classFullName + "." + type + "{");
        } else if (tag == PRESENTER) {
            writer.write("public class " + className + type + " extends " + className + type + "{");
        }
        writer.newLine();
        writer.newLine();
        writer.write("}");
        writer.flush();
        writer.close();
    }

    private static String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        int i = 0;
        while (i < strings.length) {
            if (strings[i].equals("com") || strings[i].equals("org") || strings[i].equals("cn") || strings[i].equals("net") || strings[i].equals("me") || strings[i].equals("io") || strings[i].equals("tech")) {
                index = i;
                break;
            }
            i += PRESENTER;
        }
        for (int j = index; j < length - 2; j += PRESENTER) {
            packageName.append(strings[j] + ".");
        }
        return packageName.toString();
    }

    public static String getCurrentPath(AnActionEvent e, String classFullName) {
        return ((VirtualFile) DataKeys.VIRTUAL_FILE.getData(e.getDataContext())).getPath().replace(classFullName + ".java", "");
    }
}
