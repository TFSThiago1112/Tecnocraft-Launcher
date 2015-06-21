package tk.tfsthiago1112.Tecnocraft.Launcher.version;

public class Mod {

	private String path;

	private String url;
	
	public boolean isRelative = true;

	public Mod() {
	}

	public String getPath() {
		return this.path;
	}

	public String getUrl() {
		return this.url;
	}

	public String getName() {
		return this.path.substring(this.path.lastIndexOf("/"), this.path.length());
	}

	public String getVersionPath(Version version) {
		return "versions/" + version.getId() + "/" + this.getName();
	}
}
