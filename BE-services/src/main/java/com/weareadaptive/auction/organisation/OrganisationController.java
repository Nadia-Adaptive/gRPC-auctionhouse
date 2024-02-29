package com.weareadaptive.auction.organisation;

import com.weareadaptive.auction.authentication.AuthenticationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/organisations")
public class OrganisationController {
    private final OrganisationService service;
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public OrganisationController(final OrganisationService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Organisation>> getAllOrganisations() {
        logger.info("All organisations requested.");
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganisationDTO> getOrganisation(@PathVariable final int id) {
        logger.info("Organisation with id " + id + " requested.");
        return ResponseEntity.ok().body(service.getOrganisation(id));
    }
}
