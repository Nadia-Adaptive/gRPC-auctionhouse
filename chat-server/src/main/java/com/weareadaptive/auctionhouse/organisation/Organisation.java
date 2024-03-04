package com.weareadaptive.auctionhouse.organisation;

public class Organisation {
    private int organisationId;
    private String organisationName;

    public Organisation(final int organisationId, final String organisationName) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }


    public int getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }
}
