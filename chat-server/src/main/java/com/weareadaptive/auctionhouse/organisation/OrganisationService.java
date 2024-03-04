package com.weareadaptive.auctionhouse.organisation;

import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.OrganisationsResponse;
import com.weareadaptive.auctionhouse.user.UserRepository;
import reactor.core.publisher.Sinks;

import java.util.List;

import static reactor.core.publisher.Sinks.many;
import static reactor.util.concurrent.Queues.SMALL_BUFFER_SIZE;

public class OrganisationService {
    private final OrganisationRepository repository;
    private final UserRepository userRepository;
    public Sinks.Many<OrganisationsResponse> subscriptionSink =
            many().multicast().onBackpressureBuffer(SMALL_BUFFER_SIZE, false);

    public OrganisationService(final OrganisationRepository repository, final UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<OrganisationDTO> getAll() {
        return repository.findAll().stream().map(OrganisationMapper::mapToOrganisationDTO).toList();
    }

    public OrganisationDetailsDTO getOrganisation(final int id) {
        final var organisation = repository.findById(id);

        if (organisation == null) {
            throw new NotFoundException("Organisation not found");
        }
        final var users =
                userRepository.findAll().stream()
                        .filter(user -> user.getOrganisationName().equals(organisation.getOrganisationName()))
                        .toList();

        return new OrganisationDetailsDTO(organisation, users);
    }

    public OrganisationDetailsDTO getOrganisation(final String organisationName) {
        final var organisation = repository.findByName(organisationName);
        if (organisation == null) {
            throw new NotFoundException("Organisation not found");
        }
        final var users =
                userRepository.findAll().stream().filter(user -> user.getOrganisationName().equals(organisationName))
                        .toList();

        return new OrganisationDetailsDTO(organisation, users);
    }

    public Organisation addOrganisation(final String organisationName) {
        final var existingOrganisation = repository.findByName(organisationName);
        if (existingOrganisation != null) {
            return existingOrganisation;
        }

        final var organisation = repository.save(new Organisation(repository.nextId(), organisationName));

        subscriptionSink.tryEmitNext(OrganisationMapper.mapToOrganisationsResponse(organisation, false));
        System.out.println("SCREAM");
        return organisation;
    }

    public Sinks.Many<OrganisationsResponse> getSubscriptionSink() {
        return subscriptionSink;
    }
}
