package com.weareadaptive.auction.organisation;

import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganisationService {
    private final OrganisationRepository repository;
    private final UserRepository userRepository;

    public OrganisationService(final OrganisationRepository repository, final UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Organisation> getAll() {
        return repository.findAll();
    }

    public OrganisationDTO getOrganisation(final int id) {
        final var organisation = repository.findById(id).orElse(null);
        if (organisation == null) {
            throw new NotFoundException("Organisation not found");
        }
        final var users =
                userRepository.findAll().stream()
                        .filter(user -> user.getOrganisationName().equals(organisation.getName()))
                        .toList();
        return new OrganisationDTO(organisation.getId(), organisation.getName(), users);
    }

    public OrganisationDTO getOrganisation(final String organisationName) {
        final var organisation = repository.findByName(organisationName).orElse(null);
        if (organisation == null) {
            throw new NotFoundException("Organisation not found");
        }
        final var users =
                userRepository.findAll().stream().filter(user -> user.getOrganisationName().equals(organisationName))
                        .toList();
        return new OrganisationDTO(organisation.getId(), organisation.getName(), users);
    }

    public Organisation addOrganisation(final String organisationName) {
        final var existingOrganisation = repository.findByName(organisationName).orElse(null);
        if (existingOrganisation != null) {
            return existingOrganisation;
        }

        final var organisation = repository.save(new Organisation(organisationName));
        return organisation;
    }
}
