package com.weareadaptive.auction.user;

import com.weareadaptive.auction.auction.Auction;
import com.weareadaptive.auction.auction.AuctionRepository;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.organisation.Organisation;
import com.weareadaptive.auction.organisation.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.weareadaptive.auction.utils.StringUtil.isNullOrEmpty;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final AuctionRepository auctionRepository;

    public UserService(final UserRepository userRepository, final OrganisationRepository organisationRepository,
                       final AuctionRepository auctionRepository) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.auctionRepository = auctionRepository;
    }

    public User createUser(final UserCreateRequest request) {
        var organisation = organisationRepository.findByName(request.organisationName()).orElse(null);

        if (organisation == null) {
            organisation = new Organisation(request.organisationName());
            organisationRepository.save(organisation);
        }

        if (!request.username().matches("^[a-zA-Z0-9]*$")) {
            throw new BusinessException("Invalid password");
        }

        if (request.userRole() == null) {
            throw new BusinessException("Invalid role");
        }

        if (request.userRole() == UserRole.ADMIN && !organisation.getName().equals("ADMIN")) {
            throw new BusinessException("Admins must be added to correct organisationName");
        }
        if (request.userRole() == UserRole.USER && organisation.getName().equals("ADMIN")) {
            throw new BusinessException("Users cannot be added to an admin organisationName");
        }

        final var user = userRepository.save(
                new User(request.username(), request.password(), request.firstName(), request.lastName(),
                        organisation.getName(),
                        request.userRole(), Instant.now())); // TODO: Time provider
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(final int id) {
        final var user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User does not exist.");
        }
        return user.get();
    }

    public User updateUser(final int id, final UserUpdateRequest request) {
        final var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new NotFoundException("User does not exist.");
        }

        if (!isNullOrEmpty(request.password())) {
            user.setPassword(request.password());
        }

        if (!isNullOrEmpty(request.firstName())) {
            user.setFirstName(request.firstName());
        }

        if (!isNullOrEmpty(request.lastName())) {
            user.setLastName(request.lastName());
        }

        if (!isNullOrEmpty(request.organisationName()) &&
                organisationRepository.existsByName(request.organisationName())) {
            user.setOrganisationName(request.organisationName());
        }

        if (request.userRole() != null) {
            user.setUserRole(request.userRole());
        }

        userRepository.save(user);
        return user;
    }

    public User updateUserStatus(final int id, final UserAccessRequest request) {
        final var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new NotFoundException("User does not exist.");
        }

        user.setAccessStatus(request.status());
        return user;
    }

    public List<Auction> getUserAuctions(final int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist.");
        }
        return auctionRepository.findUserAuctions(id);
    }
}
