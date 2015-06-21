// 
// Decompiled by Procyon v0.5.29
// 

package tk.tfsthiago1112.Tecnocraft.Launcher.gui.swing;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class GuiFirstTimeInit
{
    private JFrame frame;
    private GuiFancyLabel statusLabel;
    
    public void start() {
        this.initialize();
    }
    
    public void setVisible(final boolean bool) {
        this.frame.setVisible(bool);
    }
    
    public void quit() {
        this.setVisible(false);
        this.frame.dispose();
    }
    
    public GuiFirstTimeInit() {
        this.initialize();
    }
    
    private void initialize() {
        (this.frame = new JFrame()).setIconImage(Toolkit.getDefaultToolkit().getImage(GuiFirstTimeInit.class.getResource("/assets/icon_64.png")));
        this.frame.getContentPane().setLayout(new BorderLayout(0, 0));
        final JPanel mainPanel = new GuiFakePanoramaPanel();
        this.frame.getContentPane().add(mainPanel, "Center");
        mainPanel.setLayout(null);
        (this.statusLabel = new GuiFancyLabel()).setText("Preparing...");
        this.statusLabel.setForeground(Color.WHITE);
        this.statusLabel.setFont(new Font("Athelas", 0, 14));
        this.statusLabel.setHorizontalAlignment(0);
        this.statusLabel.setBounds(0, 130, 404, 27);
        mainPanel.add(this.statusLabel);
        this.frame.setBounds(100, 100, 420, 260);
        this.frame.setDefaultCloseOperation(3);
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setLocation(dim.width / 2 - this.frame.getSize().width / 2, dim.height / 2 - this.frame.getSize().height / 2);
        this.frame.setTitle("TFSThiago1112 Tecnocraft Launcher Updater");
    }
    
    public void setStatus(final String text) {
        this.statusLabel.setText(text);
    }
}
