package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import guru.qa.niffler.model.IUserJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private final UserService userService;

    @Override
    public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
        respond(responseObserver, fromJson(userService.getCurrentUser(request.getUsername())));
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        var updatedUser = userService.update(UserJson.fromMessage(request.getUser()));
        respond(responseObserver, fromJson(updatedUser));
    }

    @Override
    public void sendInvitation(SendInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        respond(responseObserver, fromJson(
                userService.createFriendshipRequest(request.getRequester(), request.getAddressee())));
    }

    @Override
    public void acceptInvitation(AcceptInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        respond(responseObserver, fromJson(
                userService.acceptFriendshipRequest(request.getRequester(), request.getAddressee())));
    }

    @Override
    public void declineInvitation(DeclineInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        respond(responseObserver, fromJson(
                userService.declineFriendshipRequest(request.getRequester(), request.getAddressee())));
    }

    @Override
    public void removeFriend(RemoveFriendRequest request, StreamObserver<Empty> responseObserver) {
        userService.removeFriend(request.getRequester(), request.getAddressee());
        respond(responseObserver, Empty.newBuilder().build());
    }

    @Override
    public void allUsers(AllUsersRequest request, StreamObserver<UserPageResponse> responseObserver) {
        respond(responseObserver, buildPageResponse(
                userService.allUsers(
                        request.getUsername(),
                        PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
                        request.hasSearchQuery() ? request.getSearchQuery() : null
                )
        ));
    }

    @Override
    public void friends(FriendsRequest request, StreamObserver<UserPageResponse> responseObserver) {
        respond(responseObserver, buildPageResponse(
                userService.friends(
                        request.getUsername(),
                        PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
                        request.hasSearchQuery() ? request.getSearchQuery() : null
                )
        ));
    }

    private <T> void respond(StreamObserver<T> observer, T response) {
        observer.onNext(response);
        observer.onCompleted();
    }

    private UserResponse fromJson(@Nonnull IUserJson userJson) {
        var builder = UserResponse.newBuilder()
                .setId(userJson.id() != null ? userJson.id().toString() : "")
                .setUsername(userJson.username());

        if (userJson.firstname() != null && !userJson.firstname().isBlank()) builder.setFirstname(userJson.firstname());
        if (userJson.surname() != null && !userJson.surname().isBlank()) builder.setSurname(userJson.surname());
        if (userJson.currency() != null) builder.setCurrency(
                CurrencyValues.valueOf(userJson.currency().name())
        );
        if (userJson.photo() != null && !userJson.photo().isBlank()) builder.setPhoto(userJson.photo());
        if (userJson.photoSmall() != null && !userJson.photoSmall().isBlank()) builder.setPhotoSmall(userJson.photoSmall());
        if (userJson.friendshipStatus() != null) builder.setFriendshipStatus(
                FriendshipStatus.valueOf(userJson.friendshipStatus().name())
        );

        // fullname только для UserJsonBulk
        if (userJson instanceof UserJsonBulk bulk) {
            if (bulk.fullname() != null && !bulk.fullname().isBlank()) builder.setFullname(bulk.fullname());
        } else if (userJson instanceof UserJson single && single.fullname() != null && !single.fullname().isBlank()) {
            builder.setFullname(single.fullname());
        }

        return builder.build();
    }

    private UserPageResponse buildPageResponse(@Nonnull org.springframework.data.domain.Page<? extends IUserJson> page) {
        var builder = UserPageResponse.newBuilder()
                .setTotalElements((int) page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setFirst(page.isFirst())
                .setLast(page.isLast())
                .setSize(page.getSize());

        page.getContent().forEach(u -> builder.addUsers(fromJson(u)));
        return builder.build();
    }
}