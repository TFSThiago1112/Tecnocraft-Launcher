package tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil;

@SuppressWarnings("unused")
public class InvalidateRequest {

    private String accessToken;

    private String clientToken;

    public InvalidateRequest(YggdrasilAuthenticationService authenticationService) {
        this.accessToken = authenticationService.getAccessToken();
        this.clientToken = authenticationService.getClientToken();
    }
}
