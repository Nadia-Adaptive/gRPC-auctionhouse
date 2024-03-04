package com.weareadaptive.auctionhouse.businesslogic.model;

import com.weareadaptive.auctionhouse.businesslogic.utils.StringUtil;

import java.util.Objects;

public class User implements Model {
    private final int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String organisation;
    private final boolean isAdmin;
    private AccessStatus accessStatus;

    public User(final int id, final String username, final String password, final String firstName,
                final String lastName,
                final String organisation) {
        this(id, username, password, firstName, lastName, organisation, false);
    }

    public User(final int id, final String username, final String password, final String firstName,
                final String lastName, final String organisation, final boolean isAdmin) {
        if (StringUtil.isNullOrEmpty(username)) {
            throw new BusinessException("username cannot be null or empty");
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
        if (StringUtil.isNullOrEmpty(organisation)) {
            throw new BusinessException("organisation cannot be null or empty");
        }

        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisation = organisation;
        this.isAdmin = isAdmin;
        this.accessStatus = AccessStatus.ALLOWED;
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

    public int getId() {
        return id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public AccessStatus getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(final AccessStatus accessStatus) {
        this.accessStatus = accessStatus;
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
        return getId() == user.getId() && getUsername().equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername());
    }

    public void update(final String username, final String password, final String firstName, final String lastName,
                       final String organisation) {
        this.username = StringUtil.isNullOrEmpty(username) ? this.username : username;
        this.password = StringUtil.isNullOrEmpty(password) ? this.password : password;
        this.firstName = StringUtil.isNullOrEmpty(firstName) ? this.firstName : firstName;
        this.lastName = StringUtil.isNullOrEmpty(lastName) ? this.lastName : lastName;
        this.organisation = StringUtil.isNullOrEmpty(organisation) ? this.organisation : organisation;
    }
}
