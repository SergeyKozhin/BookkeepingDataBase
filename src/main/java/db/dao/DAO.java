package db.dao;

import javax.sql.DataSource;
import java.util.List;

public interface DAO<T> {

    int add(T entity);

    T get(int id);

    void update(T entity);

    void delete(int id);

    List<T> getAll();
}
