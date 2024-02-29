package com.weareadaptive.auctionhouse.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserState extends State<User> {
    private final Map<String, User> usernameIndex;

    public UserState() {
        usernameIndex = new HashMap<>();
    }

    public Optional<User> findUserByUsername(final String username, final String password) {
        return stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .filter(user -> user.validatePassword(password))
                .findFirst();
    }

    @Override
    protected void onAdd(final User model) {
        super.onAdd(model);
        usernameIndex.put(model.getUsername(), get(model.getId()));
    }

    public boolean containsUser(final String uName) {
        return usernameIndex.containsKey(uName);
    }
}
