package tk.tfsthiago1112.Tecnocraft.Launcher.version.assets;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;

public class AssetIndex {

	public static final String DEFAULT_ASSET_NAME = "legacy";

	private Map<String, AssetObject> objects;

	private boolean virtual;
	
	private String version;

	public AssetIndex() {
		this.objects = new LinkedHashMap();
	}

	public Map<String, AssetObject> getFileMap() {
		return this.objects;
	}

	public Set<AssetObject> getUniqueObjects() {
		return new HashSet(this.objects.values());
	}

	public boolean isVirtual() {
		return this.virtual;
	}
	
	public String getAssetDir() {
		File assetsDir = new File(Launcher.getInstance().getBaseDirectory(), "assets");
		
		if (this.virtual)
		{
			return new File(new File(assetsDir, "virtual"), this.version).getAbsolutePath();
		}
		else
		{
			return assetsDir.getAbsolutePath();
		}
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public class AssetObject {

		private String hash;

		private long size;

		public AssetObject() {
		}

		public String getHash() {
			return this.hash;
		}

		public long getSize() {
			return this.size;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if ((o == null) || (this.getClass() != o.getClass())) {
				return false;
			}

			AssetObject that = (AssetObject) o;

			if (this.size != that.size) {
				return false;
			}
			if (!this.hash.equals(that.hash)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = this.hash.hashCode();
			result = (31 * result) + (int) (this.size ^ (this.size >>> 32));
			return result;
		}
	}
}