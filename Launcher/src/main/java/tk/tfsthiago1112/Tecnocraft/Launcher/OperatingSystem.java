package tk.tfsthiago1112.Tecnocraft.Launcher;

import java.io.File;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum OperatingSystem {

    LINUX("linux", new String[]{"linux", "unix"}), WINDOWS("windows", new String[]{"win"}), OSX("osx", new String[]{"mac"}), UNKNOWN("unknown", new String[0]);

    private final String name;

    private final String[] aliases;

    private OperatingSystem(String name, String[] aliases) {
        this.name = name;
        this.aliases = (aliases == null ? new String[0] : aliases);
    }

    public String getName() {
        return this.name;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public boolean isSupported() {
        return this != UNKNOWN;
    }

    public String getJavaDir() {
        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator;

        if ((getCurrentPlatform() == WINDOWS) && (new File(path + "javaw.exe").isFile())) {
            return path + "javaw.exe";
        }

        return path + "java";
    }

    public static OperatingSystem getCurrentPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        for (OperatingSystem os : values()) {
            for (String alias : os.getAliases()) {
                if (osName.contains(alias)) {
                    return os;
                }
            }
        }

        return UNKNOWN;
    }

    public File getWorkingDirectory() {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (this) {
            case LINUX:
                workingDirectory = new File(userHome, ".Tecnocraft/");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;

                workingDirectory = new File(folder, ".Tecnocraft/");
                break;
            case OSX:
                workingDirectory = new File(userHome, "Library/Application Support/Tecnocraft");
                break;
            default:
                workingDirectory = new File(userHome, "Tecnocraft/");
        }

        return workingDirectory;
    }

    public static void openLink(URI link) {
        Logger log = LogManager.getLogger();
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            desktopClass.getMethod("browse", new Class[]{URI.class}).invoke(o, new Object[]{link});
        } catch (Throwable e) {
            log.error("Failed to open link " + link.toString(), e);
        }
    }
}
