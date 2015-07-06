package tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil;

import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.GameProfile;

public class AuthenticationResponse extends Response {

    AuthenticationResponse(String accToken, String clToken, GameProfile sltProfile, GameProfile[] profiles) {
        this.accessToken = accToken;
        this.clientToken = clToken;
        this.selectedProfile = sltProfile;
        this.availableProfiles = profiles;
    }

    private String accessToken;

    private String clientToken;

    private GameProfile selectedProfile;

    private GameProfile[] availableProfiles;

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getClientToken() {
        return this.clientToken;
    }

    public GameProfile[] getAvailableProfiles() {
        return this.availableProfiles;
    }

    public GameProfile getSelectedProfile() {
        return this.selectedProfile;
    }
}
