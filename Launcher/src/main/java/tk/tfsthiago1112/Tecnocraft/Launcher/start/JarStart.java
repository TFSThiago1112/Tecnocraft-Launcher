package tk.tfsthiago1112.Tecnocraft.Launcher.start;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JarStart {

    static Logger log = LogManager.getRootLogger();

    @SuppressWarnings("resource")
    public void start() {
        Util.LogConfigurator();
        File workingDir = Util.getWorkingDirectory();
        System.setProperty("tc.logdir", workingDir.getAbsolutePath());
        log.info("Starting Launcher");
        log.debug("Setup Working Diretory to: " + workingDir);
        File launcherJar = new File(workingDir, "launcher.jar");
        File launcherNativesDir = new File(workingDir, "launcher-natives");
        log.debug("Setup Java Enviroment Files");
        System.setProperty("org.lwjgl.librarypath", launcherNativesDir.getAbsolutePath());
        log.debug("Setup Natives Diretorys to: " + launcherNativesDir.getAbsolutePath());
        try {
            log.debug("Starting Launcher Screen");
            Class<?> aClass = new URLClassLoader(new URL[]{launcherJar.toURI().toURL()}).loadClass("tk.tfsthiago1112.Tecnocraft.Launcher.gui.LauncherDisplay");
            aClass.newInstance();
        } catch (Exception e) {
            log.fatal("Unable to start: " + e + "" + e.getStackTrace().toString());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        JarStart start = new JarStart();
        start.start();
    }
}
