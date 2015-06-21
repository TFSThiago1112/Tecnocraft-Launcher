package tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil;

import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.GameProfile;

@SuppressWarnings("unused")
public class RefreshRequest {

	private String clientToken;

	private String accessToken;

	private GameProfile selectedProfile;

	public RefreshRequest(YggdrasilAuthenticationService authenticationService) {
		this(authenticationService, null);
	}

	public RefreshRequest(YggdrasilAuthenticationService authenticationService, GameProfile profile) {
		this.clientToken = authenticationService.getClientToken();
		this.accessToken = authenticationService.getAccessToken();
		this.selectedProfile = profile;
	}
}