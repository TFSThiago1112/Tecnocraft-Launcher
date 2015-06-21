// 
// Decompiled by Procyon v0.5.29
// 

package tk.tfsthiago1112.Tecnocraft.Launcher.gui.swing;

import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.JLabel;

public class GuiFancyLabel extends JLabel
{
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
