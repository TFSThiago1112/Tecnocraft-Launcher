package tk.tfsthiago1112.Tecnocraft.Launcher.version;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import tk.tfsthiago1112.Tecnocraft.Launcher.OperatingSystem;

public class RemoteVersionList extends VersionList {

    private final Proxy proxy;

    public RemoteVersionList(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean hasAllFiles(CompleteVersion version, OperatingSystem os) {
        return true;
    }

    @Override
    protected String getUrl(String uri) throws IOException {
        return Http.performGet(new URL("http://download.tfsthiago1112.net/tecnocraft/" + uri), this.proxy);
    }

    public Proxy getProxy() {
        return this.proxy;
    }

}
