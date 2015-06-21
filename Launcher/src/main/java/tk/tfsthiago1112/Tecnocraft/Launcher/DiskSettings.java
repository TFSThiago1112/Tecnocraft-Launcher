package tk.tfsthiago1112.Tecnocraft.Launcher;

import java.io.File;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.FileUtils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiskSettings {
    static Logger log = LogManager.getLogger();
	private static final Gson gson = new Gson();
	
	public boolean isMusicMuted = false;
	
	public int versionIndex = -1;
	
	public void save()
	{
		try {
			FileUtils.writeStringToFile(new File(Launcher.getInstance().baseDirectory, "settings.json"), gson.toJson(this));
		} catch (Exception e) {
			e.printStackTrace();
                        log.error("Failed to save settings.json.");
		}
	}
	
	public static DiskSettings load()
	{
		File file = new File(Launcher.getInstance().baseDirectory, "settings.json");
		DiskSettings settings = null;

		try {
			if (file.exists())
			{
				settings = gson.fromJson(FileUtils.readFileToString(file), DiskSettings.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (settings == null) {
			settings = new DiskSettings();
		}
		
		return settings;
	}
}
