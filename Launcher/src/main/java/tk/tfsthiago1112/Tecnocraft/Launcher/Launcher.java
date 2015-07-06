package tk.tfsthiago1112.Tecnocraft.Launcher;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.util.UUID;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.GameProfile;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.exceptions.AuthenticationException;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil.YggdrasilAuthenticationService;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.LocalVersionList;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.RemoteTestingVersionList;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.RemoteVersionList;

public class Launcher {

    public static Launcher instance;

    private DiskSettings settings;

    private VersionManager versionManager;

    private GameLauncher gameLauncher = new GameLauncher();

    private ProfileManager profileManager;

    private Proxy proxy = Proxy.NO_PROXY;

    protected File baseDirectory;

    private UUID clientToken = UUID.randomUUID();

    public static Font font;

    public Launcher() {
        this.baseDirectory = OperatingSystem.getCurrentPlatform().getWorkingDirectory();
        this.versionManager = new VersionManager(new LocalVersionList(this.baseDirectory), new RemoteVersionList(this.proxy), new RemoteTestingVersionList(this.proxy));

        Launcher.instance = this;

        this.settings = DiskSettings.load();

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/assets/Roboto-Regular.ttf"));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load the required font 'Roboto-Regular.ttf'.", e);
        }

        File config = new File(Launcher.instance.getBaseDirectory().getAbsolutePath() + "\\config\\Tecnocraft.cfg");

        System.gc();

        try {
            Files.deleteIfExists(config.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.profileManager = new ProfileManager();
        this.profileManager.loadProfile();

        this.refreshVersions();
    }

    public void refreshVersions() {
        this.versionManager.getExecutorService().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    GameProfile profile = profileManager.getSelectedProfile();
                    Launcher.this.versionManager.refreshVersions(profile != null ? profileManager.getSelectedProfile().getName() : null);
                } catch (Throwable e) {
                    Launcher.getInstance().println("Unexpected exception refreshing version list", e);
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(String username, String password) {
        YggdrasilAuthenticationService auth = this.profileManager.getAuthenticationService();

        auth.setUsername(username);
        auth.setPassword(password);

        try {
            auth.logIn();
        } catch (AuthenticationException e) {
            this.println("Invalid creditentials");
        }
    }

    public void println(String string) {
        System.out.println(string);
    }

    public void println(String string, Throwable e) {
        System.out.println("exception, " + string);
    }

    public static Launcher getInstance() {
        return instance;
    }

    public File getBaseDirectory() {
        return this.baseDirectory;
    }

    public VersionManager getVersionManager() {
        return this.versionManager;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public UUID getClientToken() {
        return this.clientToken;
    }

    public void setClientToken(UUID clientToken) {
        this.clientToken = clientToken;
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public GameLauncher getGameLauncher() {
        return this.gameLauncher;
    }

    public DiskSettings getSettings() {
        return this.settings;
    }
}
