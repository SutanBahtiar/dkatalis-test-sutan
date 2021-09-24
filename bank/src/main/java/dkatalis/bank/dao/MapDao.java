package dkatalis.bank.dao;

import dkatalis.bank.util.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MapDao<T> {

    protected final Map<Long, T> dataSource;

    protected MapDao(Map<Long, T> dataSource) {
        this.dataSource = dataSource;
    }

    protected long save(T t, long id) {
        this.dataSource.put(id, t);
        return id;
    }

    protected T get(long id) {
        return this.dataSource.get(id);
    }

    protected List<T> getList() {
        return new ArrayList<>(dataSource.values());
    }

    @SuppressWarnings("unchecked")
    protected long getId() {
        return Generator.getId((Map<Long, Object>) this.dataSource);
    }
}
