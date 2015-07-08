package tk.tfsthiago1112.Tecnocraft.Launcher;

import com.google.gson.Gson;
import com.google.gson.internal.bind.DateTypeAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil.YggdrasilAuthenticationService;
import tk.tfsthiago1112.Tecnocraft.Launcher.download.DownloadJob;
import tk.tfsthiago1112.Tecnocraft.Launcher.download.DownloadListener;
import tk.tfsthiago1112.Tecnocraft.Launcher.download.Downloadable;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.LauncherDisplay;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms.LoadingForm;
import tk.tfsthiago1112.Tecnocraft.Launcher.process.JavaProcess;
import tk.tfsthiago1112.Tecnocraft.Launcher.process.JavaProcessLauncher;
import tk.tfsthiago1112.Tecnocraft.Launcher.process.JavaProcessRunnable;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.FileUtils;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.StrSubstitutor;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.CompleteVersion;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.ExtractRules;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.Library;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.LocalVersionList;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.Mod;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.VersionList;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.VersionSyncInfo;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.assets.AssetIndex;

public class GameLauncher implements DownloadListener, JavaProcessRunnable, Runnable {

    static Logger log = LogManager.getLogger();
    private CompleteVersion version;

    private File nativeDir;

    private final Gson gson = new Gson();

    private final DateTypeAdapter dateAdapter = new DateTypeAdapter();

    public void playGame() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        log.info("Getting syncinfo for selected version");

        String selectedVersion = Launcher.getInstance().getProfileManager().getAuthenticationService().getSelectedVersion();

        if (selectedVersion == null) {
            selectedVersion = Launcher.getInstance().getVersionManager().getVersions()[0];
        }

        VersionSyncInfo syncInfo = Launcher.getInstance().getVersionManager().getVersionSyncInfo(selectedVersion);

        log.info("Queueing library & version downloads");
        try {
            this.version = Launcher.getInstance().getVersionManager().getLatestCompleteVersion(syncInfo);
        } catch (IOException e) {
            log.error("Couldn't get complete version info for " + syncInfo.getLatestVersion(), e);
            return;
        }

        if (!this.version.appliesToCurrentEnvironment()) {
            String reason = this.version.getIncompatibilityReason();
            if (reason == null) {
                reason = "This version is incompatible with your computer. Please try another one by going into Edit Profile and selecting one through the dropdown. Sorry!";
            }
            log.error("Version " + this.version.getId() + " is incompatible with current environment: " + reason);

            return;
        }

        if (this.version.getMinimumLauncherVersion() > 5) {
            log.error("An update to your launcher is available and is required to play " + this.version.getId() + ". Please restart your launcher.");

            return;
        }

        if (!syncInfo.isInstalled()) {
            try {
                VersionList localVersionList = Launcher.getInstance().getVersionManager().getLocalVersionList();
                ((LocalVersionList) localVersionList).saveVersion(this.version);
                log.info("Installed " + syncInfo.getLatestVersion());
            } catch (IOException e) {
                log.error("Couldn't save version info to install " + syncInfo.getLatestVersion(), e);
                return;
            }
        }

        try {
            DownloadJob job = new DownloadJob("Version & Libraries", false, this);

            boolean forceUpdate = true;
            File versionFolder = new File(Launcher.getInstance().getBaseDirectory(), "versions/" + this.version.getId() + "/");
            File versionFile = new File(versionFolder, this.version.getId() + ".json");
            File persistentFile = new File(versionFolder, "persistent.donotdelete");

            try {
                if (persistentFile.exists()) {
                    byte[] encoded = Files.readAllBytes(persistentFile.toPath());
                    int persistentVersion = Integer.parseInt(new String(encoded, Charset.defaultCharset()));

                    log.trace(persistentVersion);

                    if (persistentVersion >= this.version.getUpdateId()) {
                        forceUpdate = false;
                    }
                }
            } catch (Exception e) {
                forceUpdate = true;
                e.printStackTrace();
                log.warn("Error loading persistant.donotdelete, assuming forceUpdate.");
            }

            if (forceUpdate) {
                for (File file : versionFolder.listFiles()) {
                    if (!file.getAbsolutePath().equals(versionFile.getAbsolutePath())) {
                        file.delete();
                    }
                }

                Writer writer = new FileWriter(persistentFile);
                writer.write(String.valueOf(this.version.getUpdateId()));
                writer.close();
            }

            Launcher.getInstance().getVersionManager().downloadVersion(syncInfo, job);
            job.addDownloadables(Launcher.getInstance().getVersionManager().getResourceFiles(Launcher.getInstance().getProxy(), Launcher.getInstance().getBaseDirectory(), this.version));
            job.startDownloading(Launcher.getInstance().getVersionManager().getExecutorService());
        } catch (IOException e) {
            log.error("Couldn't get version info for " + syncInfo.getLatestVersion(), e);
            return;
        }
    }

    protected void launchGame() {
        File gameDirectory = new File(Launcher.getInstance().getBaseDirectory(), "profiles/" + this.version.getId() + "/");
        gameDirectory.mkdirs();

        log.info("Copying mods");

        for (Mod mod : this.version.getMods()) {
            File source = new File(Launcher.instance.getBaseDirectory(), mod.getVersionPath(this.version));

            File target;
            if (mod.isRelative) {
                target = new File(gameDirectory, mod.getPath());
            } else {
                target = new File(Launcher.instance.getBaseDirectory(), mod.getPath());
            }

            try {
                target.getParentFile().mkdirs();
                target.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            log.info("Copying " + source.toString() + " to " + target.toString());

            try {
                Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.info("Launching game");

        if (this.version == null) {
            log.error("Aborting launch; version is null?");
            return;
        }

        this.nativeDir = new File(Launcher.getInstance().getBaseDirectory(), "versions/" + this.version.getMinecraftVersion() + "/" + this.version.getMinecraftVersion() + "-natives");
        if (!this.nativeDir.isDirectory()) {
            this.nativeDir.mkdirs();
        }
        log.info("Unpacking natives to " + this.nativeDir);
        try {
            this.unpackNatives(this.version, this.nativeDir);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Couldn't unpack natives!", e);
            return;
        }

        AssetIndex assetsDir;
        try {
            assetsDir = this.reconstructAssets();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Couldn't reconstruct the assets index!", e);
            return;
        }

        log.info("Launching in " + gameDirectory);

        if (!gameDirectory.exists()) {
            if (!gameDirectory.mkdirs()) {
                log.error("Aborting launch; couldn't create game directory");
            }
        } else if (!gameDirectory.isDirectory()) {
            log.error("Aborting launch; game directory is not actually a directory");
            return;
        }
        boolean is32Bit = "32".equals(System.getProperty("sun.arch.data.model"));

        if (this.version.getOnly64bits() && is32Bit) {
            log.error("Aborting launch; your computer isn't compatible with this version or don't have enought memory to launcher");
            return;
        }

        JavaProcessLauncher processLauncher = new JavaProcessLauncher(null, new String[0]);
        processLauncher.directory(gameDirectory);

        if (OperatingSystem.getCurrentPlatform().equals(OperatingSystem.OSX)) {
            processLauncher.addCommands(new String[]{"-Xdock:icon=" + new File(assetsDir.getAssetDir(), "icons/minecraft.icns").getAbsolutePath(), "-Xdock:name=Tecnocraft"});
        }

        String defaultArgument = is32Bit ? "-Xmx1G" : "-Xmx2G";
        processLauncher.addSplitCommands(defaultArgument);

        processLauncher.addCommands(new String[]{"-Djava.library.path=" + this.nativeDir.getAbsolutePath()});
        processLauncher.addCommands(new String[]{"-cp", this.constructClassPath(this.version)});
        processLauncher.addCommands(new String[]{this.version.getMainClass()});

        YggdrasilAuthenticationService auth = Launcher.getInstance().getProfileManager().getAuthenticationService();

        String[] args = this.getMinecraftArguments(this.version, auth.getSelectedProfile().getName(), gameDirectory, assetsDir, auth);

        processLauncher.addCommands(args);

        if ((auth == null) || (auth.getSelectedProfile() == null)) {
            processLauncher.addCommands(new String[]{"--demo"});
        }
        try {
            List<String> parts = processLauncher.getFullCommands();
            StringBuilder full = new StringBuilder();
            boolean first = true;

            for (String part : parts) {
                if (!first) {
                    full.append(" ");
                }
                full.append(part);
                first = false;
            }

            log.info("Running " + full.toString());
            JavaProcess process = processLauncher.start();
            process.safeSetExitRunnable(this);
        } catch (IOException e) {
            log.error("Couldn't launch game", e);
            return;
        }

        Launcher.instance.getProfileManager().saveProfile();
        LauncherDisplay.instance.terminate();
    }

    private String[] getMinecraftArguments(CompleteVersion version, String player, File gameDirectory, AssetIndex assetsIndex, YggdrasilAuthenticationService authentication) {
        if (version.getMinecraftArguments() == null) {
            log.error("Can't run version, missing minecraftArguments");
            return null;
        }

        Map<String, String> map = new HashMap<String, String>();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        String[] split = version.getMinecraftArguments().split(" ");

        map.put("auth_username", authentication.getUsername());
        map.put("auth_session", (authentication.getSessionToken() == null) && (authentication.canPlayOnline()) ? "-" : authentication.getSessionToken());

        if (authentication.getSelectedProfile() != null) {
            map.put("auth_player_name", authentication.getSelectedProfile().getName());
        } else {
            map.put("auth_player_name", "Player");
        }

        map.put("profile_name", player);
        map.put("version_name", version.getMinecraftVersion());

        map.put("game_directory", gameDirectory.getAbsolutePath());
        map.put("game_assets", assetsIndex.getAssetDir());

        // 1.7+
        map.put("user_properties", "{}");
        map.put("user_type", "{}");

        map.put("assets_root", assetsIndex.getAssetDir());
        map.put("assets_index_name", assetsIndex.getVersion());

        map.put("auth_access_token", authentication.getAccessToken());
        map.put("auth_uuid", authentication.getSelectedProfile().getId());

        for (int i = 0; i < split.length; i++) {
            split[i] = substitutor.replace(split[i]);
            log.trace(substitutor.replace(split[i]));
        }

        return split;
    }

    private AssetIndex reconstructAssets() throws IOException {
        File assetsDir = new File(Launcher.instance.getBaseDirectory(), "assets");
        File indexDir = new File(assetsDir, "indexes");
        File objectDir = new File(assetsDir, "objects");
        String assetVersion = this.version.assets;

        File indexFile = new File(indexDir, assetVersion + ".json");
        File virtualRoot = new File(new File(assetsDir, "virtual"), assetVersion);

        if (!virtualRoot.exists()) {
            assetsDir.mkdirs();
        }

        if (!indexFile.isFile()) {
            log.error("No assets index file " + virtualRoot + "; can't reconstruct assets");
            return null;
        }

        AssetIndex index = this.gson.fromJson(FileUtils.readFileToString(indexFile), AssetIndex.class);
        index.setVersion(assetVersion);

        if (index.isVirtual()) {
            log.info("Reconstructing virtual assets folder at " + virtualRoot);
            for (Map.Entry entry : index.getFileMap().entrySet()) {
                File target = new File(virtualRoot, (String) entry.getKey());
                File original = new File(new File(objectDir, ((AssetIndex.AssetObject) entry.getValue()).getHash().substring(0, 2)), ((AssetIndex.AssetObject) entry.getValue()).getHash());

                if (!target.isFile()) {
                    target.getParentFile().mkdirs();
                    target.createNewFile();
                    Files.copy(original.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // FileUtils.writeStringToFile(new File(virtualRoot, ".lastused"),
            // this.dateAdapter.serializeToString(new Date()));
        }

        return index;
    }

    private String constructClassPath(CompleteVersion version) {
        StringBuilder result = new StringBuilder();
        Collection<File> classPath = version.getClassPath(OperatingSystem.getCurrentPlatform(), Launcher.getInstance().getBaseDirectory());
        String separator = System.getProperty("path.separator");

        for (File file : classPath) {
            if (!file.isFile()) {
                log.error("Classpath file not found: " + file);
                throw new RuntimeException("Classpath file not found: " + file);
            }
            if (result.length() > 0) {
                result.append(separator);
            }
            result.append(file.getAbsolutePath());
        }

        return result.toString();
    }

    private void unpackNatives(CompleteVersion version, File targetDir) throws IOException {
        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        Collection<Library> libraries = version.getRelevantLibraries();

        for (Library library : libraries) {
            Map<OperatingSystem, String> nativesPerOs = library.getNatives();

            if ((nativesPerOs != null) && (nativesPerOs.get(os) != null)) {
                File file = new File(Launcher.getInstance().getBaseDirectory(), "libraries/" + library.getArtifactPath(nativesPerOs.get(os)));

                ZipFile zip = new ZipFile(file);
                ExtractRules extractRules = library.getExtractRules();
                try {
                    Enumeration<? extends ZipEntry> entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();

                        if ((extractRules == null) || (extractRules.shouldExtract(entry.getName()))) {
                            File targetFile = new File(targetDir, entry.getName());
                            if (targetFile.getParentFile() != null) {
                                targetFile.getParentFile().mkdirs();
                            }

                            if (!entry.isDirectory()) {
                                BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));

                                byte[] buffer = new byte[2048];
                                FileOutputStream outputStream = new FileOutputStream(targetFile);
                                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                                try {
                                    int length;
                                    while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                                        bufferedOutputStream.write(buffer, 0, length);
                                    }
                                } finally {
                                    Downloadable.closeSilently(bufferedOutputStream);
                                    Downloadable.closeSilently(outputStream);
                                    Downloadable.closeSilently(inputStream);
                                }
                            }
                        }
                    }
                } finally {
                    zip.close();
                }
            }
        }
    }

    @Override
    public void onDownloadJobProgressChanged(DownloadJob paramDownloadJob) {
        LoadingForm.instance.getProgressbar().setProgress(paramDownloadJob.getProgress());
    }

    @Override
    public void onJavaProcessEnded(JavaProcess paramJavaProcess) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownloadJobFinished(DownloadJob job) {
        LoadingForm.instance.getProgressbar().setProgress(1.0f);

        if (job.getFailures() > 0) {
            log.error("Job '" + job.getName() + "' finished with " + job.getFailures() + " failure(s)!");
        } else {
            log.info("Job '" + job.getName() + "' finished successfully");

            try {
                this.launchGame();
            } catch (Throwable ex) {
                ex.printStackTrace();
                log.fatal("Fatal error launching game. Report this to http://mojang.atlassian.net please!", ex);
            }
        }
    }

}
