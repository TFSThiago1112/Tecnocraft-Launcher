//
// Decompiled by Procyon v0.5.29
//
package tk.tfsthiago1112.Tecnocraft.Launcher.bootstrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.swing.GuiFirstTimeInit;

public class Bootstrap {

    public int getLocalVersion() {
        final File workingDir = Util.getWorkingDirectory("Tecnocraft");
        final File launcherVersionFile = new File(workingDir, "launcher-version.txt");
        int localLauncherVersion = 0;
        if (launcherVersionFile.exists()) {
            try {
                final BufferedReader br = new BufferedReader(new FileReader(launcherVersionFile));
                localLauncherVersion = Integer.parseInt(br.readLine());
                br.close();
            } catch (FileNotFoundException ex) {
            } catch (NumberFormatException ex2) {
            } catch (IOException ex3) {
            }
        }
        return localLauncherVersion;
    }

    public int getRemoteVersion() {
        int remoteLauncherVersion = 0;
        try {
            final URL website = new URL("http://download.tfsthiago1112.net/tecnocraft/launcher-version.txt");
            final BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream(), "UTF-8"));
            remoteLauncherVersion = Integer.parseInt(br.readLine());
            br.close();
        } catch (MalformedURLException ex) {
        } catch (FileNotFoundException ex2) {
        } catch (IOException ex3) {
        }
        return remoteLauncherVersion;
    }

    private void downloadFile(final String url, final File target) {
        try {
            final URL website = new URL(url);
            final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            final FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void updateLocalVersion(final int version) {
        final File workingDir = Util.getWorkingDirectory("Tecnocraft");
        final File launcherVersionFile = new File(workingDir, "launcher-version.txt");
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(launcherVersionFile);
            final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bw.write(Integer.valueOf(version).toString());
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void startLauncher() {
        final File workingDir = Util.getWorkingDirectory("Tecnocraft");
        System.setProperty("tecnocraft.home", workingDir.getAbsolutePath());
        final GuiFirstTimeInit guiFirstTimeInit = new GuiFirstTimeInit();
        guiFirstTimeInit.start();
        workingDir.mkdirs();
        final File launcherNatives = new File(workingDir, "launcher-natives.zip");
        final File launcherJar = new File(workingDir, "launcher.jar");
        final File launcherNativesDir = new File(workingDir, "launcher-natives");
        try {
            launcherJar.createNewFile();
        } catch (IOException ex) {
        }
        final int remoteLauncherVersion = this.getRemoteVersion();
        final int localLauncherVersion = this.getLocalVersion();
        if (localLauncherVersion < 11) {
            final File oldAether = Util.getWorkingDirectory("Tecnocraft_Old");
            oldAether.mkdirs();
            if (workingDir.isDirectory()) {
                final File[] content = workingDir.listFiles();
                for (int i = 0; i < content.length; ++i) {
                    System.out.println(content[i].getName());
                    content[i].renameTo(new File(oldAether, content[i].getName()));
                }
            }
        }
        if (localLauncherVersion < remoteLauncherVersion) {
            guiFirstTimeInit.setVisible(true);
            guiFirstTimeInit.setStatus("Downloading natives...");
            this.downloadFile("http://download.tfsthiago1112.net/tecnocraft/launcher-natives.zip", launcherNatives);
            guiFirstTimeInit.setStatus("Extracting natives...");
            final UnZip unzip = new UnZip();
            unzip.unZipIt(launcherNatives, launcherNativesDir);
            guiFirstTimeInit.setStatus("Downloading launcher...");
            this.downloadFile("http://download.tfsthiago1112.net/tecnocraft/launcher.jar", launcherJar);
            this.updateLocalVersion(remoteLauncherVersion);
        }
        System.out.println("Starting launcher.");
        guiFirstTimeInit.setStatus("Starting launcher...");
        System.setProperty("org.lwjgl.librarypath", launcherNativesDir.getAbsolutePath());
        try {
            guiFirstTimeInit.quit();
            final Class<?> aClass = new URLClassLoader(new URL[]{launcherJar.toURI().toURL()}).loadClass("tk.tfsthiago1112.Tecnocraft.Launcher.gui.LauncherDisplay");
            aClass.newInstance();
        } catch (Exception e) {
            System.out.println("Unable to start: " + e);
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(final String[] args) {
        try {
            final Bootstrap boostrap = new Bootstrap();
            boostrap.startLauncher();
        } catch (Exception e2) {
            try {
                updateSystemUI();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            e2.printStackTrace(pw);
            final String stacktrace = sw.toString();
            final String message = "An error occured trying to open the Tecnocraft launcher.\n\n" + stacktrace;
            JOptionPane.showMessageDialog(null, message, "Tecnocraft Launcher Bootstrapper", 0);
        }
    }

    private static void updateSystemUI() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
}
