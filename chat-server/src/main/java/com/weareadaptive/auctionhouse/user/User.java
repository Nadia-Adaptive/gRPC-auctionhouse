package com.weareadaptive.auctionhouse.user;

import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.utils.StringUtil;

import java.util.Objects;

public class User {
    private int userId;
    private String username;
    private String password;
    private UserRole userRole;
    private String firstName;
    private String lastName;
    private String organisationName;
    private AccessStatus accessStatus;

    public User(final int userId, final String username, final String password, final String firstName,
                final String lastName, final String organisationName, final UserRole userRole) {
        if (StringUtil.isNullOrEmpty(username)) {
            throw new BusinessException("password cannot be null or empty");
        }
        if (StringUtil.isNullOrEmpty(password)) {
            throw new BusinessException("password cannot be null or empty");
        }
        if (StringUtil.isNullOrEmpty(firstName)) {
            throw new BusinessException("firstName cannot be null or empty");
        }
        if (StringUtil.isNullOrEmpty(lastName)) {
            throw new BusinessException("lastName cannot be null or empty");
        }
        if (StringUtil.isNullOrEmpty(organisationName)) {
            throw new BusinessException("organisationName cannot be null or empty");
        }
        if (userRole == null) {
            throw new BusinessException("User Role cannot be null");
        }

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisationName = organisationName;
        this.accessStatus = AccessStatus.ALLOWED;
        this.userRole = userRole;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{ password='" + username + '\'' + '}';
    }

    public String getUsername() {
        return username;
    }

    public boolean validatePassword(final String password) {
        return this.password.equals(password);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public AccessStatus getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(final AccessStatus status) {
        this.accessStatus = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return getUserId() == user.getUserId() && getUsername().equals(user.getUsername());
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setUserRole(final UserRole userRole) {
        this.userRole = userRole;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getUsername());
    }

    public int getUserId() {
        return userId;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
