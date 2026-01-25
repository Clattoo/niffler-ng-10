package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserdataGrpcTest extends BaseGrpcTest {

    @Test
    @DisplayName("Список друзей возвращается с пагинацией")
    @User(friends = 5)
    public void friendListShouldBePageable(UserJson user) {
        final var friendResponse = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPageInfo(defaultPageInfo())
                .build());

        step("Общее количество элементов должно быть 5", () ->
                assertEquals(5, friendResponse.getTotalElements()));

        step("Общее количество страниц должно быть 1", () ->
                assertEquals(1, friendResponse.getTotalPages()));

        step("Должна быть первая страница", () ->
                assertTrue(friendResponse.getFirst()));

        step("Должна быть последняя страница", () ->
                assertTrue(friendResponse.getLast()));

        step("Размер страницы должен быть 10 по умолчанию", () ->
                assertEquals(10, friendResponse.getSize()));
    }

    @Test
    @DisplayName("Список друзей фильтруется по username")
    @User(friends = 5)
    public void friendListFilteredBySearchQuery(UserJson user) {
        final var friendAsFilter = user.getTestData().friends().get(0);

        final var friendResponse = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPageInfo(defaultPageInfo())
                .setSearchQuery(friendAsFilter.getUsername())
                .build());

        step("В ответе 1 друг", () ->
                assertEquals(1, friendResponse.getUsersCount()));

        step("Фильтрация по username выполнена корректно", () ->
                assertEquals(friendAsFilter.getUsername(),
                        friendResponse.getUsersList().get(0).getUsername()));
    }

    @Test
    @DisplayName("Дружба удаляется корректно")
    @User(friends = 1)
    public void removeFriendship(UserJson user) {
        final var friend = user.getTestData().friends().get(0);

        userdataServiceBlockingStub.removeFriend(RemoveFriendRequest.newBuilder()
                .setRequester(user.getUsername())
                .setAddressee(friend.getUsername())
                .build());

        final var friendsCount = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPageInfo(defaultPageInfo())
                .build()).getUsersCount();

        step("Список друзей пуст", () ->
                assertEquals(0, friendsCount));
    }

    @Test
    @DisplayName("Входящее приглашение принимается")
    @User(incomeInvitations = 1)
    public void acceptIncomeInvitation(UserJson user) {
        final var inviter = user.getTestData().incomeInvitations().get(0);

        userdataServiceBlockingStub.acceptInvitation(AcceptInvitationRequest.newBuilder()
                .setRequester(user.getUsername())
                .setAddressee(inviter.getUsername())
                .build());

        final var friends = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPageInfo(defaultPageInfo())
                .build());

        step("Приглашение принято", () ->
                assertEquals(1, friends.getUsersCount()));

        step("Статус дружбы FRIEND", () ->
                assertEquals(FriendshipStatus.FRIEND.name(),
                        friends.getUsersList().get(0).getFriendshipStatus().name()));
    }

    @Test
    @DisplayName("Входящее приглашение отклоняется")
    @User(incomeInvitations = 1)
    public void rejectIncomeInvitation(UserJson user) {
        final var inviter = user.getTestData().incomeInvitations().get(0);

        userdataServiceBlockingStub.declineInvitation(DeclineInvitationRequest.newBuilder()
                .setRequester(user.getUsername())
                .setAddressee(inviter.getUsername())
                .build());

        final var friendsPage = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPageInfo(defaultPageInfo())
                .build());

        step("Списки друзей и приглашений пусты", () ->
                assertEquals(0, friendsPage.getUsersCount()));
    }

    @Test
    @DisplayName("Исходящее приглашение отправляется корректно")
    @User
    public void sendFriendshipInvitation(UserJson user) {
        final var allUsers = userdataServiceBlockingStub.allUsers(
                AllUsersRequest.newBuilder()
                        .setUsername("")
                        .setPageInfo(defaultPageInfo())
                        .build()
        ).getUsersList();

        final var addressee = allUsers.stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .findFirst()
                .orElse(null);

        if (addressee == null) {
            step("Нет доступных пользователей для отправки приглашения, тест пропускается", () -> {});
            return;
        }

        step("Отправитель: " + user.getUsername() + ", Получатель: " + addressee.getUsername(), () -> {});

        userdataServiceBlockingStub.sendInvitation(SendInvitationRequest.newBuilder()
                .setRequester(user.getUsername())
                .setAddressee(addressee.getUsername())
                .build());

        final var friendsPageResponse = userdataServiceBlockingStub.friends(FriendsRequest.newBuilder()
                .setUsername(addressee.getUsername())
                .setPageInfo(defaultPageInfo())
                .build());

        step("Статус входящего приглашения INVITE_RECEIVED", () ->
                assertEquals(FriendshipStatus.INVITE_RECEIVED.name(),
                        friendsPageResponse.getUsersList().get(0).getFriendshipStatus().name()));
    }

    private PageInfo defaultPageInfo() {
        return PageInfo.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();
    }
}