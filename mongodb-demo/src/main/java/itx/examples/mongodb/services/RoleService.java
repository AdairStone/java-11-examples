package itx.examples.mongodb.services;

import itx.examples.mongodb.dto.Role;

import java.util.Collection;

/**
 * Service providing data access for Roles.
 */
public interface RoleService {

    /**
     * Return all available roles in the database.
     * @return
     */
    Collection<Role> getAll();

    /**
     * Insert role into database.
     * @param role
     * @throws DataException
     */
    void insert(Role role) throws DataException;

    /**
     * Remove Role from database.
     * @param id
     * @throws DataException
     */
    void remove(String id) throws DataException;

    /**
     * Remove all roles from database.
     * @throws DataException
     */
    void removeAll() throws DataException;

}
