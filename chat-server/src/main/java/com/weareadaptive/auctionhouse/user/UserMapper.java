package com.weareadaptive.auctionhouse.user;

import com.weareadaptive.auctionhouse.user.gRPCUserService.AccessStatusValue;
import com.weareadaptive.auctionhouse.user.gRPCUserService.CreateUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserAccessRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserResponse;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UserResponse;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UsersResponse;

import java.util.List;

import static com.weareadaptive.auctionhouse.user.gRPCUserService.UserRoleValue;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponse mapToUserResponse(final User user) {
        return UserResponse.newBuilder()
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setOrganisationName(user.getOrganisationName())
                .setUserRole(
                        UserRoleValue.valueOf(user.getUserRole().name()))
                .setAccessStatus(
                        AccessStatusValue.valueOf(user.getAccessStatus().name()))
                .setOrganisationName(user.getOrganisationName())
                .setUserId(user.getUserId())
                .build();
    }

    public static CreateUserDTO mapToCreateUserDTO(final CreateUserRequest request) {
        return new CreateUserDTO(request.getUsername(), request.getPassword(), request.getFirstName(),
                request.getLastName(), request.getOrganisationName(),
                UserRole.valueOf(request.getUserRole().name()));
    }

    public static UserUpdateDTO mapToUpdateUserDTO(final UpdateUserRequest request) {
        return new UserUpdateDTO(request.getUserId(), request.getPassword(), request.getFirstName(),
                request.getLastName(), request.getOrganisationName());
    }

    public static UserAccessDTO mapToUserAccessDTO(final UpdateUserAccessRequest request) {
        return new UserAccessDTO(request.getUserId(), AccessStatus.valueOf(request.getAccessStatus().name()));
    }

    public static List<UserResponse> mapToUserResponseList(final List<User> users) {
        return users.stream().map(UserMapper::mapToUserResponse).toList();
    }

    public static UpdateUserResponse mapToUpdateUserResponse(final User updateUser, final boolean passwordChange) {
        return UpdateUserResponse.newBuilder().setUser(mapToUserResponse(updateUser)).setPasswordChanged(passwordChange)
                .build();
    }

    public static UsersResponse mapToUsersResponse(final List<User> users, final boolean isInitialData) {
        return UsersResponse.newBuilder()
                .addAllUsers(users.stream()
                        .map(UserMapper::mapToUserResponse)
                        .toList())
                .setInitialData(isInitialData)
                .build();
    }

    public static UsersResponse mapToUsersResponse(final User user) {
        return mapToUsersResponse(List.of(user), false);
    }
}
