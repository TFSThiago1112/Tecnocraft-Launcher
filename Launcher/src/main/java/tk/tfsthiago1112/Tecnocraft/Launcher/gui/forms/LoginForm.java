package tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms;

import java.awt.Font;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.GuiPanel;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiButton;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiCheckbox;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiElement;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiRectangle;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiText;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.elements.GuiTextfield;

public class LoginForm extends GuiForm {

    private GuiRectangle background;

    private GuiTextfield usernameField, passwordField;

    private GuiText usernameLabel, emailLabel, usernameInput, passwordLabel, passwordInput, rememberLabel;

    private GuiCheckbox rememberCheckbox;

    private GuiButton loginButton;

    private ErrorForm errorForm;

    public LoginForm(GuiPanel panel, GuiForm parentForm) {
        super(panel, parentForm);

        Font lfont = Launcher.font.deriveFont(Font.PLAIN, 24);
        Font font = Launcher.font.deriveFont(Font.PLAIN, 18);
        Font sfont = Launcher.font.deriveFont(Font.PLAIN, 16);
        Font sssfont = Launcher.font.deriveFont(Font.PLAIN, 14);

        this.background = new GuiRectangle(this, (Display.getWidth() - 264) / 2, 187, 264, 254);
        this.background.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().backgroundColor);
        this.add(this.background);

        this.usernameLabel = new GuiText(this, font, "Minecraft Username");
        this.emailLabel = new GuiText(this, sssfont, "or Mojang Email");
        this.usernameInput = new GuiText(this, sfont, "");
        this.usernameField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, this.background.getY() + 60, 210, 30, this.usernameInput, false);
        this.usernameField.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor);
        this.add(this.usernameField);

        this.passwordLabel = new GuiText(this, font, "Password");
        this.passwordInput = new GuiText(this, lfont, "");
        this.passwordField = new GuiTextfield(this, (Display.getWidth() - 210) / 2, this.usernameField.getY() + 64, 210, 30, this.passwordInput, true);
        this.passwordField.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor);
        this.add(this.passwordField);

        this.rememberLabel = new GuiText(this, font, "Remember Login");
        this.rememberCheckbox = new GuiCheckbox(this, this.passwordField.getX() + 20, this.passwordField.getY() + 40, 20, 20, true);
        this.rememberCheckbox.setColor(this.panel.getSettings().textFieldColor, this.panel.getSettings().textFieldHoveredColor, new Color(255, 255, 255, 0.8f));
        this.add(this.rememberCheckbox);

        GuiText loginText = new GuiText(this, font, "Login");
        this.loginButton = new GuiButton(this, (Display.getWidth() - 100) / 2, this.rememberCheckbox.getY() + 40, 100, 35, loginText);
        this.loginButton.setColor(this.panel.getSettings().backgroundColor, this.panel.getSettings().textFieldColor);
        this.add(this.loginButton);

        GuiTextfield.activeTextfield = this.usernameField;
    }

    @Override
    public void render() {
        super.render();

        this.usernameLabel.render((Display.getWidth() - this.usernameLabel.getWidth()) / 2, this.usernameField.getY() - 50);
        this.emailLabel.render((Display.getWidth() - this.emailLabel.getWidth()) / 2, this.usernameField.getY() - 25);
        this.passwordLabel.render((Display.getWidth() - this.passwordLabel.getWidth()) / 2, this.passwordField.getY() - 25);
        this.rememberLabel.render(this.rememberCheckbox.getX() + 30, this.rememberCheckbox.getY());
    }

    @Override
    public void onElementClick(GuiElement element) {
        super.onElementClick(element);

        if (element == this.loginButton) {
            if (this.usernameField.getText().isEmpty() || this.passwordField.getText().isEmpty()) {
                if (this.usernameField.getText().isEmpty()) {
                    this.usernameField.flashError();
                }
                if (this.passwordField.getText().isEmpty()) {
                    this.passwordField.flashError();
                }

                return;
            }
            Launcher.getInstance().getProfileManager().getAuthenticationService().setRememberMe(this.rememberCheckbox.isChecked());
            Launcher.getInstance().login(this.usernameField.getText(), this.passwordField.getText());

            if (Launcher.getInstance().getProfileManager().getAuthenticationService().isLoggedIn()) {
                Launcher.getInstance().getVersionManager().setSelectedProfile(Launcher.getInstance().getProfileManager().getAuthenticationService().getAvailableProfileNames()[0]);
                PlayForm playForm = new PlayForm(this.panel, this);
                playForm.setFadeX(Display.getWidth());
                playForm.fadeLeft();
                this.fadeLeft();
                this.kill = true;
            } else {
                errorForm = new ErrorForm(this.panel, this, "Login failed.");
                errorForm.setFadeX(Display.getWidth());
                errorForm.fadeLeft();
                errorForm.setOnScreen(true);
                this.fadeLeft();
            }
        }
    }

    @Override
    public void onKey(int key, char character) {
        if (key == Keyboard.KEY_RETURN) {
            if (this.onScreen) {
                this.onElementClick(this.loginButton);
                this.onScreen = false;
            }
        } else if (key == Keyboard.KEY_TAB) {
            if (GuiTextfield.activeTextfield == this.usernameField) {
                GuiTextfield.activeTextfield = this.passwordField;
            } else {
                GuiTextfield.activeTextfield = this.usernameField;
            }
        }
    }

}
