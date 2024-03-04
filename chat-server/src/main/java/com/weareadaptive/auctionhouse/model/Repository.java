package com.weareadaptive.auctionhouse.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Repository<T> {
    protected Map<Integer, T> dataStore;

    private final AtomicInteger idSerialiser;

    public Repository() {
        dataStore = new HashMap<>();

        idSerialiser = new AtomicInteger();
        idSerialiser.set(0);
    }

    public T findById(final int id) {
        return dataStore.get(id);
    }

    public T save(final T model) {
        dataStore.put(idSerialiser.get(), model);
        return model;
    }

    public T remove(final int id) {
        return dataStore.remove(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    public boolean existsById(final int id) {
        return dataStore.containsKey(id);
    }

    public int nextId() {
        return idSerialiser.addAndGet(1);
    }
}
