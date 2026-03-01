package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserSoapApiClient;
import jaxb.userdata.AllUsersRequest;
import jaxb.userdata.FriendshipStatus;
import jaxb.userdata.UsersResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SoapTest
public class SoapUserTest {

    private final UserSoapApiClient userSoapApiClient = new UserSoapApiClient();

    @Test
    @User(friends = 2)
    @DisplayName("SOAP: Друзья должны возвращаться в виде страницы")
    void friendsShouldBeReturnedAsPage(UserJson user) throws Exception {
        UsersResponse response = userSoapApiClient.friendsPage(user.getUsername(), 0, 2, null);

        assertNotNull(response);
        assertEquals(2, response.getSize());
        assertEquals(0, response.getNumber());
        assertTrue(response.getUser().size() <= 2);
    }

    @Test
    @User(friends = 3)
    @DisplayName("SOAP: Друзья должны фильтроваться по строке поиска")
    void friendsShouldBeFilteredBySearchQuery(UserJson user) throws Exception {
        String searchQuery = user.getTestData().friends().get(0).getUsername().substring(0, 3);

        UsersResponse response = userSoapApiClient.friends(user.getUsername(), searchQuery);
        assertNotNull(response);
        response.getUser().forEach(u -> assertTrue(u.getUsername().contains(searchQuery)));
    }

    @Test
    @User(friends = 1)
    @DisplayName("SOAP: Дружба должна быть удалена")
    void friendshipShouldBeRemoved(UserJson user) throws Exception {
        UserJson friend = user.getTestData().friends().get(0);
        userSoapApiClient.removeFriend(user.getUsername(), friend.getUsername());
        UsersResponse friends = userSoapApiClient.friends(user.getUsername(), friend.getUsername());

        assertTrue(friends.getUser().isEmpty());
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("SOAP: Приглашение должно быть принято")
    void invitationShouldBeAccepted(UserJson user) throws Exception {
        UserJson inviter = user.getTestData().incomeInvitations().get(0);
        userSoapApiClient.acceptInvitation(user.getUsername(), inviter.getUsername());
        UsersResponse friends = userSoapApiClient.friends(user.getUsername(), inviter.getUsername());

        assertEquals(1, friends.getUser().size());
        assertEquals(FriendshipStatus.FRIEND, friends.getUser().get(0).getFriendshipStatus());
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("SOAP: Приглашение должно быть отклонено")
    void invitationShouldBeDeclined(UserJson user) throws Exception {
        UserJson inviter = user.getTestData().incomeInvitations().get(0);
        userSoapApiClient.declineInvitation(user.getUsername(), inviter.getUsername());
        UsersResponse friends = userSoapApiClient.friends(user.getUsername(), inviter.getUsername());

        assertTrue(friends.getUser().isEmpty());
    }

    @Test
    @User
    @DisplayName("SOAP: Приглашение должно быть отправлено")
    public void invitationShouldBeSent(UserJson user) throws IOException {
        var recipient = userSoapApiClient.allUsers(new AllUsersRequest() {{
                    setUsername(user.getUsername());
                }}).getUser().stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .findFirst().orElseThrow();

        System.out.println("Отправитель: " + user.getUsername() + ", Получатель: " + recipient.getUsername());

        assertEquals(FriendshipStatus.INVITE_SENT,
                userSoapApiClient.sendInvitation(user.getUsername(), recipient.getUsername())
                        .getUser().getFriendshipStatus());
    }
}