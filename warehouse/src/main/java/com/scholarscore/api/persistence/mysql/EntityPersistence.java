package com.scholarscore.api.persistence.mysql;

import java.io.Serializable;
import java.util.Collection;

public interface EntityPersistence<T extends Serializable> {
    public Collection<T> selectAll(
            long id);

    public T select(
            long parentId,
            long id);

    public Long insert (
            long parentId,
            T entity);

    public Long update(
            long parentId,
            long id,
            T entity);

    public Long delete(long is);
}
