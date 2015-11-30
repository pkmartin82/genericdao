package com.pkm.genericdao.concrete.dao;

import org.hibernate.SessionFactory;

import com.pkm.genericdao.concrete.entity.ConcreteEntity;
import com.pkm.genericdao.dao.GenericDaoImpl;

public class NullEntityConcreteDaoImpl extends GenericDaoImpl<ConcreteEntity, Integer> implements ConcreteDao {

	private SessionFactory sessionFactory;

	@Override
	public Class<ConcreteEntity> getEntityClass() {
		return null;
	}

	@Override
	protected SessionFactory getQualifiedSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;	
	}
}
