package com.weareadaptive.auction.organisation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.weareadaptive.auction.user.User;

import java.util.List;

@JsonIgnoreProperties
public record OrganisationDTO(int id, String name, List<User> users) {


}
