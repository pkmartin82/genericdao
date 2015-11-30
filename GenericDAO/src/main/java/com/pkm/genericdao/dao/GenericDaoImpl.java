package com.pkm.genericdao.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

@Repository
public abstract class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {

	protected Logger logger;

	/**
	 * Default Constructor
	 */
	public GenericDaoImpl() {
		logger = Logger.getLogger(this.getClass());

		if (this.getEntityClass() == null) {
			throw new NullPointerException("Entity class is null");
		}
	}

	/**
	 * Method to return the specific SessionFactory that a child class will annotate
	 * with the @Qualifier tag 
	 *  
	 * @return SessionFactory
	 */
	protected abstract SessionFactory getQualifiedSessionFactory();

	@Override
	public T save(T t) {

		// SaveOrUpdate the Hero in the SessionFactory
		logger.info("Saving (or Updating) " + t + " into the repository");
		Session session = getQualifiedSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(t);
		session.getTransaction().commit();
		return (t);
	}

	@Override
	public Long countAll() {
		Session session = getQualifiedSessionFactory().getCurrentSession();
		Class<T> type = this.getEntityClass();
		Criteria crit = session.createCriteria(type);
		crit.setProjection(Projections.rowCount());
		Long count = (Long) crit.uniqueResult();
		return (count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		Session session = getQualifiedSessionFactory().getCurrentSession();
		Class<T> type = this.getEntityClass();
		Criteria criteria = session.createCriteria(type);
		List<T> entities = (List<T>) criteria.list();
		return (entities);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(PK id) {
		Session session = getQualifiedSessionFactory().getCurrentSession();
		Class<T> type = this.getEntityClass();
		T t = (T) session.get(type, id);
		return (t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T delete(PK id) {
		Session session = getQualifiedSessionFactory().getCurrentSession();
		Class<T> type = this.getEntityClass();
		T t = (T) session.get(type, id);

		if (t != null) {
			logger.info(type.getSimpleName() + " with Id = '" + id + "' exists, removing from repository");
			session.beginTransaction();
			session.delete(t);
			session.getTransaction().commit();
		} else {
			logger.warn(type.getSimpleName() + " with Id = '" + id + "' does not exist in the repository");
		}

		return (t);
	}
}
