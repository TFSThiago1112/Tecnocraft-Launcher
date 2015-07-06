package tk.tfsthiago1112.Tecnocraft.Launcher.download;

public abstract interface DownloadListener {

    public abstract void onDownloadJobFinished(DownloadJob paramDownloadJob);

    public abstract void onDownloadJobProgressChanged(DownloadJob paramDownloadJob);
}
