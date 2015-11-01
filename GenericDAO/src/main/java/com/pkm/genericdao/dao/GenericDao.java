package com.pkm.genericdao.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, PK extends Serializable> {

	public Class<T> getEntityClass();

	// ================================================================================================================
	// CREATE, UPDATE
	// ================================================================================================================

	/**
	 * Saves an object of type T to the repository
	 * 
	 * @param t - the object to save
	 * @return T - the newly saved object, which may be different from the object passed in
	 */
	public T save(T t);

	// ================================================================================================================
	// READ
	// ================================================================================================================

	/**
	 * Counts all the objects of type T in the repository
	 * 
	 * @return Long - the count of objects of type T
	 */
	public Long countAll();

	/**
	 * Returns a list of all objects of type T from the repository
	 * 
	 * @return List<T> - the list of all objects of type T
	 */
	public List<T> getAll();

	/**
	 * Returns an object of type T with primary key PK
	 * 
	 * @param id - the primary key identifier
	 * @return T - the object of type T with primary key PK
	 */
	public T get(PK id);

	// ================================================================================================================
	// DELETE
	// ================================================================================================================

	/**
	 * Deletes an object of type T with primary key PK
	 *  
	 * @param id - the primary key identifier
	 * @return T - the object of type T with primary key PK that was deleted
	 */
	public T delete(PK id);

}
