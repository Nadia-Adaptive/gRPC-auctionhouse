package com.weareadaptive.auctionhouse.user;


import com.weareadaptive.auctionhouse.model.Repository;

import java.util.HashMap;
import java.util.Map;

public class UserRepository extends Repository<User> {
    Map<String, User> usernameIndex;

    public UserRepository() {
        usernameIndex = new HashMap<>();
    }

    public User save(final User user) {
        super.save(user);
        usernameIndex.put(user.getUsername(), user);
        return user;
    }

    public User findByUsername(final String username) {
        return usernameIndex.get(username);
    }
}
