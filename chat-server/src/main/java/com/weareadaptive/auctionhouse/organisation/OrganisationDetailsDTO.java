package com.weareadaptive.auctionhouse.organisation;

import com.weareadaptive.auctionhouse.user.User;

import java.util.List;
public record OrganisationDetailsDTO(Organisation organisation, List<User> users) {


}
