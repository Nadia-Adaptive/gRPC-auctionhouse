package com.weareadaptive.auctionhouse.organisation;

import com.weareadaptive.auctionhouse.model.Repository;

import java.util.HashMap;
import java.util.Map;

public class OrganisationRepository extends Repository<Organisation> {
    Map<String, Organisation> organisationNameIndex;

    public OrganisationRepository() {
        this.organisationNameIndex = new HashMap<>();
    }

    @Override
    public Organisation save(final Organisation o) {
        super.save(o);
        organisationNameIndex.put(o.getOrganisationName(), o);
        return o;
    }

    public Organisation findByName(final String organisationName) {
        return organisationNameIndex.get(organisationName);
    }

    public boolean existsByName(final String organisationName) {
        return organisationNameIndex.containsKey(organisationName);
    }
}
