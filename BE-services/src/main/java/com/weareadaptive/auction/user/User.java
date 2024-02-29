package com.weareadaptive.auction.user;

import com.weareadaptive.auction.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

import static com.weareadaptive.auction.utils.StringUtil.isNullOrEmpty;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 50, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Column(length = 50, nullable = false)
    private String firstName;
    @Column(length = 50, nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String organisationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessStatus")
    private AccessStatus accessStatus;

    private Instant createdAt;
    private Instant updatedAt;

    public User(final String username, final String password, final String firstName,
                final String lastName, final String organisationName, final UserRole userRole, final Instant createdAt) {
        if (isNullOrEmpty(username)) {
            throw new BusinessException("password cannot be null or empty");
        }
        if (isNullOrEmpty(password)) {
            throw new BusinessException("password cannot be null or empty");
        }
        if (isNullOrEmpty(firstName)) {
            throw new BusinessException("firstName cannot be null or empty");
        }
        if (isNullOrEmpty(lastName)) {
            throw new BusinessException("lastName cannot be null or empty");
        }
        if (isNullOrEmpty(organisationName)) {
            throw new BusinessException("organisationName cannot be null or empty");
        }
        if (createdAt == null) {
            throw new BusinessException("CreateAt cannot be null");
        }
        if (userRole == null) {
            throw new BusinessException("User Role cannot be null");
        }

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisationName = organisationName;
        this.accessStatus = AccessStatus.ALLOWED;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
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
        return getId() == user.getId() && getUsername().equals(user.getUsername());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername());
    }


    public int getId() {
        return id;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
