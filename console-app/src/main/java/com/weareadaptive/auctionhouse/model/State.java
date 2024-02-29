package com.weareadaptive.auctionhouse.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class State<T extends Model> {
    public static final String ITEM_ALREADY_EXISTS = "Item already exists";
    private final Map<Integer, T> models;
    private int currentId = 0;

    public State() {
        models = new HashMap<>();
    }

    public int nextId() {
        return currentId++;
    }

    protected void onAdd(final T model) {

    }

    public void add(final T model) {
        if (models.containsKey(model.getId())) {
            throw new BusinessException(ITEM_ALREADY_EXISTS);
        }
        onAdd(model);
        models.put(model.getId(), model);
    }

    void setNextId(final int id) {
        this.currentId = id;
    }

    public T get(final int id) {
        return models.get(id);
    }

    public Stream<T> stream() {
        return models.values().stream();
    }

    protected void update(final int id, final T newModel) {
        models.replace(id, newModel);
    }
}
