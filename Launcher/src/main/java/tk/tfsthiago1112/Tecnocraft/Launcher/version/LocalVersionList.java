package tk.tfsthiago1112.Tecnocraft.Launcher.version;

import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.OperatingSystem;

public class LocalVersionList extends VersionList {

    static Logger log = LogManager.getLogger();
    private final File baseDirectory;

    private final File baseVersionsDir;

    public LocalVersionList(File baseDirectory) {
        if ((baseDirectory == null) || (!baseDirectory.isDirectory())) {
            throw new IllegalArgumentException("Base directory is not a folder!");
        }

        this.baseDirectory = baseDirectory;
        this.baseVersionsDir = new File(this.baseDirectory, "versions");
        if (!this.baseVersionsDir.isDirectory()) {
            this.baseVersionsDir.mkdirs();
        }
    }

    protected InputStream getFileInputStream(String uri) throws FileNotFoundException {
        return new FileInputStream(new File(this.baseDirectory, uri));
    }

    @Override
    public void refreshVersions() throws IOException {
        this.clearCache();

        File[] files = this.baseVersionsDir.listFiles();
        if (files == null) {
            return;
        }

        int remoteVersion = getRemoteIndexVersion();

        for (File directory : files) {
            String id = directory.getName();
            File jsonFile = new File(directory, id + ".json");

            if ((directory.isDirectory()) && (jsonFile.exists())) {
                try {
                    if (Launcher.getInstance().getSettings().versionIndex < remoteVersion) {
                        log.info("Deleting local version " + jsonFile.getAbsolutePath() + " because the remote server reported it's outdated.");
                        jsonFile.delete();
                    } else {
                        CompleteVersion version = this.gson.fromJson(this.getUrl("versions/" + id + "/" + id + ".json"), CompleteVersion.class);
                        this.addVersion(version);
                    }
                } catch (JsonSyntaxException ex) {
                    if (Launcher.getInstance() != null) {
                        log.error("Couldn't load local version " + jsonFile.getAbsolutePath(), ex);
                    } else {
                        throw new JsonSyntaxException("Loading file: " + jsonFile.toString(), ex);
                    }
                }
            }
        }

        Launcher.getInstance().getSettings().versionIndex = remoteVersion;
        Launcher.getInstance().getSettings().save();
    }

    private int getRemoteIndexVersion() {
        int remoteVersion = -1;

        try {
            URL website = new URL("http://download.tfsthiago1112.net/tecnocraft/versions/versionsIndex.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream(), "UTF-8"));
            remoteVersion = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return remoteVersion;
    }

    public void saveVersionList() throws IOException {
        String text = this.serializeVersionList();
        PrintWriter writer = new PrintWriter(new File(this.baseVersionsDir, "versions.json"));
        writer.print(text);
        writer.close();
    }

    public void saveVersion(CompleteVersion version) throws IOException {
        String text = this.serializeVersion(version);
        File target = new File(this.baseVersionsDir, version.getId() + "/" + version.getId() + ".json");
        if (target.getParentFile() != null) {
            target.getParentFile().mkdirs();
        }
        PrintWriter writer = new PrintWriter(target);
        writer.print(text);
        writer.close();
    }

    public File getBaseDirectory() {
        return this.baseDirectory;
    }

    @Override
    public boolean hasAllFiles(CompleteVersion version, OperatingSystem os) {
        Set<String> files = version.getRequiredFiles(os);

        for (String file : files) {
            if (!new File(this.baseDirectory, file).isFile()) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected String getUrl(String uri) throws IOException {
        InputStream inputStream = this.getFileInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }

        reader.close();

        return result.toString();
    }
}
