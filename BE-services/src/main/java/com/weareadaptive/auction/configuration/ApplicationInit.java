package com.weareadaptive.auction.configuration;

import com.weareadaptive.auction.organisation.OrganisationRepository;
import com.weareadaptive.auction.user.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInit {
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    public ApplicationInit(final UserRepository userRepository, final OrganisationRepository organisationRepository) {
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createInitData() {
    }
}
