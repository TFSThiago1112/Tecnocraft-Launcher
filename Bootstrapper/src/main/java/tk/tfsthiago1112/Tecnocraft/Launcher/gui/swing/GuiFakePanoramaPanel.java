// 
// Decompiled by Procyon v0.5.29
// 

package tk.tfsthiago1112.Tecnocraft.Launcher.gui.swing;

import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Image;
import javax.swing.JPanel;

public class GuiFakePanoramaPanel extends JPanel
{
    Image img;
    
    public GuiFakePanoramaPanel() {
        try {
            this.img = ImageIO.read(GuiFakePanoramaPanel.class.getResource("/assets/init/fakePanorama.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, 0, -22, null);
    }
}
