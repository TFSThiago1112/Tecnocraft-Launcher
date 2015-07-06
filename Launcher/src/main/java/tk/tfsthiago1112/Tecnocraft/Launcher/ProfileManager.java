package tk.tfsthiago1112.Tecnocraft.Launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.GameProfile;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil.YggdrasilAuthenticationService;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.FileUtils;

public class ProfileManager {

    private final Gson gson;

    private YggdrasilAuthenticationService authenticationService;

    public ProfileManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        this.gson = gsonBuilder.create();

        this.authenticationService = new YggdrasilAuthenticationService();
    }

    public void loadProfile() {
        String rawCredentials;

        try {
            rawCredentials = FileUtils.readFileToString(new File(Launcher.getInstance().getBaseDirectory(), "profile.json"));

            Type typeOfHashMap = new TypeToken<Map<String, String>>() {
            }.getType();
            LinkedTreeMap<String, String> credentials = this.gson.fromJson(rawCredentials, typeOfHashMap);

            this.authenticationService.loadFromStorage(credentials);
            this.authenticationService.logIn();
            this.saveProfile();
        } catch (Exception e) {
            Launcher.getInstance().println("Couldn't load profile.");
            this.authenticationService.logOut();
        }
    }

    public void saveProfile() {
        String rawCredentials = this.gson.toJson(this.authenticationService.saveForStorage());

        try {
            FileUtils.writeStringToFile(new File(Launcher.getInstance().getBaseDirectory(), "profile.json"), rawCredentials);
        } catch (IOException e) {
            Launcher.getInstance().println("Couldn't write profile.");
        }
    }

    public void removeProfile() {
        String rawCredentials = this.gson.toJson(this.authenticationService.removeProfile());

        try {
            FileUtils.writeStringToFile(new File(Launcher.getInstance().getBaseDirectory(), "profile.json"), rawCredentials);
        } catch (IOException e) {
            Launcher.getInstance().println("Couldn't write profile.");
        }
    }

    public YggdrasilAuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }

    public GameProfile getSelectedProfile() {
        return authenticationService.getSelectedProfile();
    }

}
