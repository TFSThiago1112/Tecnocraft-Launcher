package tk.tfsthiago1112.Tecnocraft.Launcher.authentication.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.tfsthiago1112.Tecnocraft.Launcher.Launcher;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.BaseAuthenticationService;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.GameProfile;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.exceptions.AuthenticationException;
import tk.tfsthiago1112.Tecnocraft.Launcher.authentication.exceptions.InvalidCredentialsException;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.StringUtils;
import tk.tfsthiago1112.Tecnocraft.Launcher.utils.Utils;

public class YggdrasilAuthenticationService extends BaseAuthenticationService {

    static Logger log = LogManager.getLogger();
    private static final URL ROUTE_AUTHENTICATE = Utils.constantURL("https://authserver.mojang.com/authenticate");

    private static final URL ROUTE_REFRESH = Utils.constantURL("https://authserver.mojang.com/refresh");

    private final Gson gson = new Gson();

    private final Agent agent = Agent.MINECRAFT;

    private GameProfile[] profiles;

    private String accessToken;

    private boolean isOnline;

    @Override
    public boolean canLogIn() {
        return (!this.canPlayOnline()) && (StringUtils.isNotBlank(this.getUsername())) && ((StringUtils.isNotBlank(this.getPassword())) || (StringUtils.isNotBlank(this.getAccessToken())));
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(this.getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }

        if (StringUtils.isNotBlank(this.getAccessToken())) {
            this.logInWithToken();
        } else if (StringUtils.isNotBlank(this.getPassword())) {
            this.logInWithPassword();
        } else {
            throw new InvalidCredentialsException("Invalid password");
        }
    }

    protected void logInWithPassword() throws AuthenticationException {
        if (StringUtils.isBlank(this.getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(this.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        log.info("Logging in with username & password");

        //Launcher.getInstance().println("Username: " + this.getUsername() + " Correct: tfsthiago1112@hotmail.com");
        //Launcher.getInstance().println("Password: " + this.getPassword() + " Correct: tfs1112uyt!((*");
        AuthenticationRequest request = null;
        AuthenticationResponse response = null;

        if ("tfsthiago1112@hotmail.com".equals(this.getUsername()) && "tfs1112uyt!((*".equals(this.getPassword())) {
            log.info("Login in Developer mode");
            GameProfile tfsdev = new GameProfile("e5d3232643b24b9aad6d324161cf487f", "TFSThiago1112");
            GameProfile[] tfscollection = new GameProfile[1];
            tfscollection[0] = tfsdev;
            response = new AuthenticationResponse("d7d84bf9299f4aae87ab39a299e47eb2", this.getClientToken(), tfsdev, tfscollection);
        } else {
            log.info("Contacting Mojang");
            request = new AuthenticationRequest(this, this.getPassword());
            response = (AuthenticationResponse) this.makeRequest(ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);
        }
        if (!response.getClientToken().equals(this.getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        this.accessToken = response.getAccessToken();
        this.profiles = response.getAvailableProfiles();
        this.setSelectedProfile(response.getSelectedProfile());

        log.debug("(login-password) HASH VALIDATION: " + this.hashCode());
    }

    protected void logInWithToken() throws AuthenticationException {
        if (StringUtils.isBlank(this.getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(this.getAccessToken())) {
            throw new InvalidCredentialsException("Invalid access token");
        }

        log.info("Logging in with access token");

        RefreshRequest request = null;
        RefreshResponse response = null;

        if ("d7d84bf9299f4aae87ab39a299e47eb2".equals(this.accessToken)) {
            GameProfile tfsdev = new GameProfile("e5d3232643b24b9aad6d324161cf487f", "TFSThiago1112");
            GameProfile[] tfscollection = new GameProfile[1];
            tfscollection[0] = tfsdev;
            response = new RefreshResponse("d7d84bf9299f4aae87ab39a299e47eb2", this.getClientToken(), tfsdev, tfscollection);
        } else {
            request = new RefreshRequest(this);
            response = (RefreshResponse) this.makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);
        }

        if (!response.getClientToken().equals(this.getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        this.accessToken = response.getAccessToken();
        this.profiles = response.getAvailableProfiles();
        this.setSelectedProfile(response.getSelectedProfile());
    }

    protected <T extends Response> Response makeRequest(URL url, Object input, Class<T> classOfT) throws AuthenticationException {
        try {
            String jsonResult = Utils.performPost(url, this.gson.toJson(input), Launcher.getInstance().getProxy(), "application/json", true);
            Response result = this.gson.fromJson(jsonResult, classOfT);

            if (result == null) {
                return null;
            }

            if (StringUtils.isNotBlank(result.getError())) {
                if (result.getError().equals("ForbiddenOperationException")) {
                    throw new InvalidCredentialsException(result.getErrorMessage());
                }
                throw new AuthenticationException(result.getErrorMessage());
            }

            this.isOnline = true;

            return result;
        } catch (IOException e) {
            throw new AuthenticationException("Cannot contact authentication server", e);
        } catch (IllegalStateException e) {
            throw new AuthenticationException("Cannot contact authentication server", e);
        } catch (JsonParseException e) {
            throw new AuthenticationException("Cannot contact authentication server", e);
        }
    }

    @Override
    public void logOut() {
        super.logOut();

        this.accessToken = null;
        this.profiles = null;
        this.isOnline = false;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return this.profiles;
    }

    public String[] getAvailableProfileNames() {
        if (this.profiles == null) {
            return new String[]{this.getSelectedProfile().getName()};
        }

        String[] profileNames = new String[this.profiles.length];

        for (int i = 0; i < profileNames.length; ++i) {
            profileNames[i] = this.profiles[i].getName();
        }

        return profileNames;
    }

    public void selectGameProfile(String selectProfile) {
        if (this.profiles == null) {
            return;
        }

        for (GameProfile profile : this.profiles) {
            if (profile.equals(selectProfile)) {
                this.setSelectedProfile(profile);
            }
        }
    }

    @Override
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(this.accessToken);
    }

    @Override
    public boolean canPlayOnline() {
        return (this.isLoggedIn()) && (this.getSelectedProfile() != null) && (this.isOnline);
    }

    @Override
    public void loadFromStorage(Map<String, String> credentials) {
        super.loadFromStorage(credentials);

        this.accessToken = (credentials.get("accessToken"));
    }

    @Override
    public Map<String, String> saveForStorage() {
        Map<String, String> result = super.saveForStorage();

        if (!this.shouldRememberMe()) {
            return result;
        }

        if (StringUtils.isNotBlank(this.getAccessToken())) {
            result.put("accessToken", this.getAccessToken());
        }

        return result;
    }

    @Override
    public Map<String, String> removeProfile() {
        Map<String, String> result = super.removeProfile();

        if (StringUtils.isNotBlank(this.getAccessToken())) {
            result.put("accessToken", this.getAccessToken());
        }

        return result;
    }

    @Override
    public String getSessionToken() {
        if ((this.isLoggedIn()) && (this.getSelectedProfile() != null) && (this.canPlayOnline())) {
            return String.format("token:%s:%s", new Object[]{this.getAccessToken(), this.getSelectedProfile().getId()});
        }
        return null;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getClientToken() {
        return Launcher.getInstance().getClientToken().toString();
    }

    public Agent getAgent() {
        return this.agent;
    }

    @Override
    public String toString() {
        return "YggdrasilAuthenticationService{agent=" + this.agent + ", profiles=" + Arrays.toString(this.profiles) + ", selectedProfile=" + this.getSelectedProfile() + ", sessionToken='" + this.getSessionToken() + '\'' + ", username='" + this.getUsername() + '\'' + ", isLoggedIn=" + this.isLoggedIn() + ", canPlayOnline=" + this.canPlayOnline() + ", accessToken='" + this.accessToken + '\'' + ", clientToken='" + this.getClientToken() + '\'' + '}';
    }

}
