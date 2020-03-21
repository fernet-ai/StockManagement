package others;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class JavaProcessId {

    public static void kILL() throws InterruptedException, IOException {

        String PID = jPID();
        System.out.println("PID " + PID);
        String os = "";
        os = System.getProperty("os.name");
        System.out.println("os " + os);
        if (os.startsWith("Windows")) {
            Process pr = Runtime.getRuntime().exec("taskkill /pid " + jPID() + " /t /f");
        } else {
            System.out.println("sto chiudendo " + os);
            Process pr = Runtime.getRuntime().exec("kill " + jPID());
            System.out.println("Chiuso");
        }

    }

    public static String jPID() throws InterruptedException {
        String vmName = ManagementFactory.getRuntimeMXBean().getName();
        int p = vmName.indexOf("@");
        String pid = vmName.substring(0, p);
        return pid;
    }

    public static String path() {
        String jarLocationUrl = JavaProcessId.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = new File(jarLocationUrl).getParent();
        System.out.println("path " + path);
        return path;
    }
}