package com.weareadaptive.auctionhouse.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class OrganisationState {
    private final Map<String, OrganisationDetails> detailsMap;

    public OrganisationState() {
        this.detailsMap = new HashMap<>();
    }

    public Stream<OrganisationDetails> getAllDetails() {
        return detailsMap.values().stream();
    }

    public Stream<String> getAllOrganisations() {
        return detailsMap.keySet().stream();
    }

    public void addUserToOrganisation(final User u) {
        if (u.isAdmin()) { // Assuming admins should not be added to an organisation
            return;
        }

        String organisationName = u.getOrganisation();

        if (detailsMap.containsKey(organisationName)) {
            detailsMap.get(organisationName).users().add(u);
        } else {
            var users = new ArrayList<User>();
            users.add(u);
            detailsMap.put(organisationName, new OrganisationDetails(organisationName, users));
        }
    }
    public boolean removeUserFromOrganisation(final User u, final String oldOrganisation) {
        if (detailsMap.containsKey(oldOrganisation)) {
            return detailsMap.get(oldOrganisation).users().remove(u);
        }
        return false;
    }
}
