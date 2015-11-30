package com.pkm.genericdao.concrete.dao;

import org.hibernate.SessionFactory;

import com.pkm.genericdao.concrete.entity.ConcreteEntity;
import com.pkm.genericdao.dao.GenericDao;

public interface ConcreteDao extends GenericDao<ConcreteEntity, Integer> {

	public void setSessionFactory(SessionFactory sessionFactory);
}
