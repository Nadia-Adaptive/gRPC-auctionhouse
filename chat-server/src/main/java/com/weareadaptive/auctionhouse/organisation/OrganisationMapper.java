package com.weareadaptive.auctionhouse.organisation;

import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.OrganisationResponse;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.OrganisationsResponse;

import java.util.List;

import static com.weareadaptive.auctionhouse.user.UserMapper.mapToUserResponseList;

public class OrganisationMapper {
    public static OrganisationResponse mapToOrganisationResponse(final OrganisationDetailsDTO dto) {
        final var organisation = dto.organisation();
        return OrganisationResponse.newBuilder()
                .setOrganisationId(organisation.getOrganisationId())
                .setOrganisationName(organisation.getOrganisationName())
                .addAllUsers(mapToUserResponseList(dto.users()))
                .build();
    }

    public static OrganisationResponse mapToOrganisationResponse(final OrganisationDTO organisation) {
        return OrganisationResponse.newBuilder()
                .setOrganisationId(organisation.organisationId())
                .setOrganisationName(organisation.organisationName())
                .build();
    }

    public static OrganisationResponse mapToOrganisationResponse(final Organisation organisation) {
        return OrganisationResponse.newBuilder()
                .setOrganisationId(organisation.getOrganisationId())
                .setOrganisationName(organisation.getOrganisationName())
                .build();
    }

    public static OrganisationsResponse mapToOrganisationsResponse(final Organisation organisation,
                                                                   final boolean isInitialData) {
        return mapToOrganisationsResponse(mapToOrganisationDTO(organisation), isInitialData);
    }

    public static OrganisationsResponse mapToOrganisationsResponse(final OrganisationDTO organisation,
                                                                   final boolean isInitialData) {
        return OrganisationsResponse.newBuilder()
                .setInitialData(isInitialData)
                .addOrganisations(mapToOrganisationResponse(organisation))
                .build();
    }

    public static OrganisationsResponse mapToOrganisationsResponse(final List<OrganisationDTO> organisations,
                                                                   final boolean isInitialData) {
        return OrganisationsResponse.newBuilder()
                .setInitialData(isInitialData)
                .addAllOrganisations(
                        organisations
                                .stream()
                                .map(OrganisationMapper::mapToOrganisationResponse)
                                .toList())
                .build();
    }


    public static OrganisationDTO mapToOrganisationDTO(final Organisation organisation) {
        return new OrganisationDTO(organisation.getOrganisationId(), organisation.getOrganisationName());
    }
}
