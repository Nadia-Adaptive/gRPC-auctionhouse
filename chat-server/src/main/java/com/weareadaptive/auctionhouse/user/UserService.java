package com.weareadaptive.auctionhouse.user;

import com.weareadaptive.auctionhouse.auction.Auction;
import com.weareadaptive.auctionhouse.auction.AuctionRepository;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.organisation.OrganisationRepository;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UsersResponse;
import com.weareadaptive.auctionhouse.utils.StringUtil;
import reactor.core.publisher.Sinks;

import java.util.List;

import static com.weareadaptive.auctionhouse.user.UserMapper.mapToUsersResponse;
import static reactor.util.concurrent.Queues.SMALL_BUFFER_SIZE;

public class UserService {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final AuctionRepository auctionRepository;

    private final Sinks.Many<UsersResponse> usersSink =
            Sinks.many().multicast().onBackpressureBuffer(SMALL_BUFFER_SIZE, false);

    public UserService(final UserRepository userRepository, final OrganisationRepository organisationRepository,
                       final AuctionRepository auctionRepository) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
        this.auctionRepository = auctionRepository;
    }

    public User createUser(final CreateUserDTO request) {
        var organisation = organisationRepository.findByName(request.organisationName());

        if (!request.username().matches("^[a-zA-Z0-9]*$")) {
            throw new BusinessException("Invalid password");
        }

        if (request.userRole() == null) {
            throw new BusinessException("Invalid role");
        }

        if (request.userRole() == UserRole.ADMIN && !organisation.getOrganisationName().equals("ADMIN")) {
            throw new BusinessException("Admins must be added to correct organisationName");
        }
        if (request.userRole() == UserRole.USER && organisation.getOrganisationName().equals("ADMIN")) {
            throw new BusinessException("Users cannot be added to an admin organisationName");
        }

        final var user = userRepository.save(
                new User(userRepository.nextId(), request.username(), request.password(), request.firstName(),
                        request.lastName(), organisation.getOrganisationName(),
                        request.userRole()));
        if (user == null) {
            throw new BusinessException("Something went wrong");
        }
        usersSink.tryEmitNext(mapToUsersResponse(user));
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(final int id) {
        final var user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    public User updateUser(final UserUpdateDTO request) {
        final var user = userRepository.findById(request.userId());

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (!StringUtil.isNullOrEmpty(request.password())) {
            user.setPassword(request.password());
        }

        if (!StringUtil.isNullOrEmpty(request.firstName())) {
            user.setFirstName(request.firstName());
        }

        if (!StringUtil.isNullOrEmpty(request.lastName())) {
            user.setLastName(request.lastName());
        }

        if (!StringUtil.isNullOrEmpty(request.organisationName())
                && organisationRepository.existsByName(request.organisationName())) {
            user.setOrganisationName(request.organisationName());
        }

        userRepository.save(user);
        usersSink.tryEmitNext(mapToUsersResponse(user));
        return user;
    }

    public User updateUserStatus(final UserAccessDTO dto) {
        final var user = userRepository.findById(dto.userId());

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.setAccessStatus(dto.status());
        return user;
    }

    public List<Auction> getUserAuctions(final int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        return auctionRepository.findUserAuctions(id);
    }

    public Sinks.Many<UsersResponse> getSubscription() {
        return usersSink;
    }
}
