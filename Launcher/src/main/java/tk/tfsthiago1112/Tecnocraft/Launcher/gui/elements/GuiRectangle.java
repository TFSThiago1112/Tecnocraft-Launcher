package tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements;

import tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms.GuiForm;

public class GuiRectangle extends GuiElement {

    public GuiRectangle(GuiForm form, int x, int y, int width, int height) {
        super(form, x, y, width, height);
    }

    @Override
    public void render() {
        if (!this.isMouseHovering()) {
            GuiElement.renderColoredRect(this.getFadingX(), this.y, this.width, this.height, this.getColor());
        } else {
            GuiElement.renderColoredRect(this.getFadingX(), this.y, this.width, this.height, this.getHoveringColor());
        }
    }

}
