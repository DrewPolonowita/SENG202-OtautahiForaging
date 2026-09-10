package seng202.team4.repository;

import seng202.team4.exceptions.DuplicateEntryException;
import java.util.List;

/**
 * Interface for Database Access Objects (DAOs) that provides common functionality for database access
 * @author Morgan English
 * @param <T> Generic type
 */
public interface DAOInterface<T> {
    /**
     * Gets all of T from the database
     * @return List of all objects type T from the database
     */
    List<T> getAll();

    /**
     * Gets a single object of type T from the database by id
     * @param id id of object to get
     * @return Object of type T that has id given
     */
    T getOne(int id);

    /**
     * Adds a single object of type T to database
     * @param toAdd object of type T to add
     * @return object insert id if inserted correctly
     * @throws DuplicateEntryException if the object already exists
     *
     */
    int add(T toAdd) throws DuplicateEntryException;

    /**
     * Deletes and object from database that matches id given
     * @param id id of object to delete
     */
    //void delete(int id);

    /**
     * Updates an object in the database
     * @param toUpdate Object that needs to be updated
     * (this object must be able to identify itself and its previous self)
     */
    void update(T toUpdate);

}
