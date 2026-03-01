package guru.qa.niffler.service;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import io.qameta.allure.Step;
import jaxb.userdata.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public final class UserSoapApiClient extends RestClient {

    private final UserdataSoapApi userdataSoapApi;

    public UserSoapApiClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @Step("Send SOAP POST('/ws') request current user")
    @Nullable
    public UserResponse currentUser(CurrentUserRequest currentUserRequest) throws IOException {
        return userdataSoapApi.currentUser(currentUserRequest).execute().body();

    }

    @Step("Send SOAP POST('/ws') request all users")
    @Nullable
    public UsersResponse allUsers(AllUsersRequest allUsersRequest) throws IOException {
        return userdataSoapApi.allUsers(allUsersRequest).execute().body();

    }

    @Step("Send SOAP POST('/ws') request send invitation")
    public UserResponse sendInvitation(String from, String to) throws IOException {
        SendInvitationRequest req = new SendInvitationRequest();
        req.setUsername(from);
        req.setFriendToBeRequested(to);
        return userdataSoapApi.sendInvitation(req).execute().body();
    }

    @Step("Send SOAP POST('/ws') request accept invitation")
    public UserResponse acceptInvitation(String username, String friend) throws IOException {
        AcceptInvitationRequest req = new AcceptInvitationRequest();
        req.setUsername(username);
        req.setFriendToBeAdded(friend);
        return userdataSoapApi.acceptInvitation(req).execute().body();
    }

    @Step("Send SOAP POST('/ws') decline invitation")
    public UserResponse declineInvitation(String username, String friend) throws IOException {
        DeclineInvitationRequest req = new DeclineInvitationRequest();
        req.setUsername(username);
        req.setInvitationToBeDeclined(friend);
        return userdataSoapApi.declineInvitation(req).execute().body();
    }

    @Step("Send SOAP removeFriend")
    public void removeFriend(String username, String friend) throws IOException {
        RemoveFriendRequest req = new RemoveFriendRequest();
        req.setUsername(username);
        req.setFriendToBeRemoved(friend);
        userdataSoapApi.removeFriend(req).execute();
    }

    @Step("Send SOAP friends")
    public UsersResponse friends(String username, @Nullable String searchQuery) throws IOException {
        FriendsRequest req = new FriendsRequest();
        req.setUsername(username);
        req.setSearchQuery(searchQuery);
        return userdataSoapApi.friends(req).execute().body();
    }

    @Step("Send SOAP friends page")
    public UsersResponse friendsPage(String username, int page, int size, @Nullable String searchQuery) throws IOException {
        FriendsPageRequest req = new FriendsPageRequest();
        req.setUsername(username);

        PageInfo pi = new PageInfo();
        pi.setPage(page);
        pi.setSize(size);
        req.setPageInfo(pi);
        req.setSearchQuery(searchQuery);

        return userdataSoapApi.friendsPage(req).execute().body();
    }
}
