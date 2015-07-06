package tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms;

import java.awt.Font;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.GuiPanel;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiRectangle;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiText;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.Version;
import tk.tfsthiago1112.Tecnocraft.Launcher.version.VersionSyncInfo;

public class PatchNotesForm extends GuiForm {

    private GuiRectangle background;

    private String currentVersionId = "";

    private ArrayList<GuiText> changelog = new ArrayList<GuiText>();

    private Font font;

    private GuiText changelogLabel;

    public PatchNotesForm(GuiPanel panel, GuiForm parentForm) {
        super(panel, parentForm);

        this.font = Launcher.font.deriveFont(Font.PLAIN, 16);
        Font sfont = Launcher.font.deriveFont(Font.PLAIN, 20);
        Font ssfont = Launcher.font.deriveFont(Font.PLAIN, 18);

        this.background = new GuiRectangle(this, ((Display.getWidth() - 224) / 2) + 264, 157, 224, 254);
        this.background.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().backgroundColor);
        this.add(this.background);

        this.changelogLabel = new GuiText(this, ssfont, "Changelog:");
    }

    public void setVersion(String versionId) {
        if (!this.currentVersionId.equals(versionId)) {
            VersionSyncInfo versionSyncInfo = Launcher.getInstance().getVersionManager().getVersionSyncInfo(versionId);
            Version version = versionSyncInfo.getLatestVersion();

            this.changelog.clear();

            if (version.getChangelog() != null) {
                for (String changelog : version.getChangelog()) {
                    this.changelog.add(new GuiText(this, this.font, changelog));
                }
            }
        }

        this.currentVersionId = versionId;
    }

    @Override
    public void render() {
        super.render();

        this.changelogLabel.render(this.background.getFadingX() + ((this.background.getWidth() - this.changelogLabel.getWidth()) / 2), this.background.getY() + 15);

        int offset = 50;

        for (GuiText text : this.changelog) {
            text.render(this.background.getFadingX() + 20, this.background.getY() + offset);
            offset += 25;
        }
    }

}
