package com.pkm.genericdao.concrete.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.pkm.genericdao.concrete.entity.ConcreteEntity;
import com.pkm.genericdao.dao.GenericDaoImpl;

@Repository("concreteDao")
public class ConcreteDaoImpl extends GenericDaoImpl<ConcreteEntity, Integer> implements ConcreteDao {

	@Autowired
	@Qualifier("genericDaoTestSessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public Class<ConcreteEntity> getEntityClass() {
		return ConcreteEntity.class;
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
