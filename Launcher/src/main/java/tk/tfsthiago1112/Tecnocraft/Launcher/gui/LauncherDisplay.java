package tk.tfsthiago1112.Tecnocraft.Launcher.gui;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms.AdForm;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms.LoginForm;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.forms.PlayForm;
import tk.tfsthiago1112.Tecnocraft.Launcher.gui.utils.Sprite;

public class LauncherDisplay {

    static Logger log = LogManager.getRootLogger();

    public static LauncherDisplay instance;

    public static void main(String[] argv) {
        log.debug("Starting LauncherDisplay");
        new LauncherDisplay();
    }

    private Panorama panorama;

    private Sprite logo;

    /*public Sprite facebook;

     public Sprite twitter;*/
    private Audio music;

    private GuiPanel panel;

    public Sprite audioMute;

    public Sprite audioPlay;

    public Sprite checkboxCheck;

    private float fadeInAlpha = 1f;

    private boolean shouldTerminate;

    public LauncherDisplay() {
        new Launcher();

        LauncherDisplay.instance = this;
        log.debug("Initialzing LauncherDisplay");
        this.init();
        log.debug("Start LauncherDisplay");
        this.start();
    }

    public ByteBuffer loadIcon(String filename, int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream(filename))); // load
        // image

        // convert image to byte array
        byte[] imageBytes = new byte[width * height * 4];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                for (int k = 0; k < 3; k++) // red, green, blue
                {
                    imageBytes[(i * 16 + j) * 4 + k] = (byte) (((pixel >> (2 - k) * 8)) & 255);
                }
                imageBytes[(i * 16 + j) * 4 + 3] = (byte) (((pixel >> (3) * 8)) & 255); // alpha
            }
        }
        return ByteBuffer.wrap(imageBytes);
    }

    public ByteBuffer loadIcon(String url) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream(url)));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadIcons() {
        try {
            Display.setIcon(new ByteBuffer[]{
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream("assets/icon_16.png"))), false, false, null),
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream("assets/icon_32.png"))), false, false, null),
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream("assets/icon_64.png"))), false, false, null),
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new BufferedInputStream(ResourceLoader.getResourceAsStream("assets/icon_128.png"))), false, false, null)});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            log.debug("Setup Window Title");
            Display.setTitle("Tecnocraft Launcher 1.1");
            log.debug("Loading Icons");
            this.loadIcons();
            log.debug("Setup Window Size");
            Display.setDisplayMode(new DisplayMode(854, 480));
            log.debug("Create Window and Set VSync Enabled");
            Display.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        log.debug("Setup Panorama System");
        this.panorama = new Panorama(new String[]{"assets/bg/panorama0.png", "assets/bg/panorama1.png", "assets/bg/panorama2.png", "assets/bg/panorama3.png", "assets/bg/panorama4.png", "assets/bg/panorama5.png",});
        Keyboard.enableRepeatEvents(true);

        try {
            log.debug("Setup Logos and Icons");
            this.music = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("assets/music.ogg"));
            this.logo = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/aether_logo.png")));
            /*this.facebook = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/facebook.png")));
             this.twitter = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/twitter.png")));*/
            this.audioPlay = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/sound_on.png")));
            this.audioMute = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/sound_off.png")));
            this.checkboxCheck = new Sprite(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/check.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GuiSettings settings = new GuiSettings();

        log.debug("Setup Colors and others Settings");
        settings.backgroundColor = new Color(0, 0, 0, 0.2F);
        settings.textFieldColor = new Color(0, 0, 0, 0.3F);
        settings.textFieldHoveredColor = new Color(0, 0, 0, 0.5F);
        settings.fadeSpeed = 40;

        this.panel = new GuiPanel(settings);

        log.debug("Checking Login");
        boolean isLoggedIn = Launcher.getInstance().getProfileManager().getAuthenticationService().isLoggedIn();

        if (isLoggedIn) {
            log.debug("Starting PlayForm");
            new PlayForm(this.panel, null);
        } else {
            log.debug("Starting LoginForm");
            new LoginForm(this.panel, null);
        }

        log.debug("Starting Form Icon");
        new AdForm(this.panel, null, Launcher.getInstance().getSettings().isMusicMuted);

        if (!Launcher.getInstance().getSettings().isMusicMuted) {
            log.debug("Starting Music");
            this.startMusic();
        }
    }

    public void start() {
        log.debug("Starting OpenGL");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 854, 480, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        log.debug("OpenGL is Running");
        while (!Display.isCloseRequested() && !this.shouldTerminate) {
            this.render();
            Display.update();
            Display.sync(60);
        }

        Display.destroy();
        AL.destroy();

        Launcher.instance.getProfileManager().saveProfile();

        System.exit(0);
    }

    public void terminate() {
        this.shouldTerminate = true;
    }

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        this.panorama.render();

        GL11.glDisable(GL11.GL_CULL_FACE);
        this.logo.render((Display.getWidth() - this.logo.getWidth()) / 2, 15);

        this.panel.render();

        SoundStore.get().poll(0);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_QUADS);
        new Color(0, 0, 0, this.fadeInAlpha).bind();

        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(Display.getWidth(), 0, 0);
        GL11.glVertex3f(Display.getWidth(), Display.getHeight(), 0);
        GL11.glVertex3f(0, Display.getHeight(), 0);
        GL11.glEnd();

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        this.fadeInAlpha = (float) Math.max(0, this.fadeInAlpha - 0.025);

        GL11.glPopMatrix();
    }

    public void stopMusic() {
        log.debug("Stopping Music");
        SoundStore.get().setCurrentMusicVolume(0.0F);
        this.music.stop();

        Launcher.getInstance().getSettings().isMusicMuted = true;
        Launcher.getInstance().getSettings().save();
        log.debug("Stopped Music");
    }

    public void startMusic() {
        log.debug("Starting Music");
        SoundStore.get().setCurrentMusicVolume(1F);
        this.music.playAsMusic(1.0f, 0.5f, true);

        Launcher.getInstance().getSettings().isMusicMuted = false;
        Launcher.getInstance().getSettings().save();
        log.debug("Started Music");
    }

    public boolean isMusicPlaying() {
        return this.music.isPlaying();
    }
}
