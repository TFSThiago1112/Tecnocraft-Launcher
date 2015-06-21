package tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms;

import java.awt.Font;

import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.GuiPanel;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiProgressbar;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiRectangle;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiText;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class LoadingForm extends GuiForm {

	private GuiText text;

	private GuiProgressbar progressbar;

	public static LoadingForm instance;

	public LoadingForm(GuiPanel panel, GuiForm parentForm) {
		super(panel, parentForm);

		Font font = Launcher.font.deriveFont(Font.PLAIN, 24);

		GuiRectangle rect = new GuiRectangle(this, (Display.getWidth() - 400) / 2, (Display.getHeight() - 150) / 2 + 20, 400, 150);
		rect.setColor(new Color(0, 0, 0, 0.2f), new Color(0, 0, 0, 0.2f));
		this.add(rect);

		this.progressbar = new GuiProgressbar(this, (Display.getWidth() - 300) / 2, ((Display.getHeight() - 30) / 2) + 40, 300, 30);
		this.progressbar.setColor(new Color(0, 0, 0, 0.5f), new Color(0, 0, 0, 0.7f));
		this.add(this.progressbar);

		this.text = new GuiText(this, font, "Downloading resources");
		LoadingForm.instance = this;
	}

	@Override
	public void render() {
		super.render();

		this.text.render((Display.getWidth() - this.text.getWidth()) / 2, ((Display.getHeight() - this.text.getHeight()) / 2) - 10);
	}

	public GuiProgressbar getProgressbar() {
		return this.progressbar;
	}

}
