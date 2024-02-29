package com.weareadaptive.auction.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DTO {
    @JsonIgnore
    private int id;


    public int getId() {
        return id;
    }
}
