//
// Decompiled by Procyon v0.5.29
//
package tk.tfsthiago1112.Tecnocraft.Launcher.bootstrapper;

import java.io.File;

public class Util {

    public static final String APPLICATION_NAME = "Tecnocraft";
    private static /* synthetic */ int[] $SWITCH_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS;

    public static OS getPlatform() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OS.WINDOWS;
        }
        if (osName.contains("mac")) {
            return OS.MACOS;
        }
        if (osName.contains("linux")) {
            return OS.LINUX;
        }
        if (osName.contains("unix")) {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }

    public static File getWorkingDirectory(final String foldername) {
        final String userHome = System.getProperty("user.home", ".");
        File workingDirectory = null;
        switch ($SWITCH_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS()[getPlatform().ordinal()]) {
            case 3:
            case 4: {
                workingDirectory = new File(userHome, "." + foldername + "/");
                break;
            }
            case 1: {
                final String applicationData = System.getenv("APPDATA");
                final String folder = (applicationData != null) ? applicationData : userHome;
                workingDirectory = new File(folder, "." + foldername + "/");
                break;
            }
            case 2: {
                workingDirectory = new File(userHome, "Library/Application Support/" + foldername + "/");
                break;
            }
            default: {
                workingDirectory = new File(userHome, "Tecnocraft/");
                break;
            }
        }
        return workingDirectory;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS() {
        final int[] $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS = Util.$SWITCH_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS;
        if ($switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS != null) {
            return $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS;
        }
        final int[] $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2 = new int[OS.values().length];
        try {
            $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2[OS.LINUX.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
        try {
            $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2[OS.MACOS.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }
        try {
            $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2[OS.SOLARIS.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }
        try {
            $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2[OS.UNKNOWN.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }
        try {
            $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2[OS.WINDOWS.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }
        return Util.$SWITCH_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS = $switch_TABLE$net$aetherteam$aether$launcher$bootstrapper$Util$OS2;
    }

    public enum OS {

        WINDOWS("WINDOWS", 0),
        MACOS("MACOS", 1),
        SOLARIS("SOLARIS", 2),
        LINUX("LINUX", 3),
        UNKNOWN("UNKNOWN", 4);

        private OS(final String s, final int n) {
        }
    }
}
