//
// Decompiled by Procyon v0.5.29
//
package tk.tfsthiago1112.Tecnocraft.Launcher.bootstrapper;

import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.File;

public class UnZip {

    public void unZipIt(final File zipFile, final File outputFolder) {
        final byte[] buffer = new byte[1024];
        try {
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                final String fileName = ze.getName();
                if (!fileName.contains("__")) {
                    final File newFile = new File(outputFolder + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();
                    final FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
