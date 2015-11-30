package com.pkm.genericdao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pkm.genericdao.concrete.dao.ConcreteDao;
import com.pkm.genericdao.concrete.dao.NullEntityConcreteDaoImpl;
import com.pkm.genericdao.concrete.entity.ConcreteEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/Concrete_ApplicationConfig.xml")
@Transactional(value = "genericDaoTestTransactionManager", propagation = Propagation.SUPPORTS)
public class ConcreteDaoIntegration {

	/** Concrete object with which to test */
	private static ConcreteEntity testConcrete;

	@Autowired
	private ConcreteDao concreteDao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Set up the DataSource so it can be bound to the JNDI name, (in lieu
		// of the app server doing this)
		try {
			SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
			DriverAdapterCPDS cpds = new DriverAdapterCPDS();
			cpds.setDriver("com.mysql.jdbc.Driver");
			cpds.setUrl("jdbc:mysql://localhost:3306/pkm");
			cpds.setUser("root");
			cpds.setPassword("heynow");

			SharedPoolDataSource dataSource = new SharedPoolDataSource();
			dataSource.setConnectionPoolDataSource(cpds);
			dataSource.setMaxActive(10);
			dataSource.setMaxWait(50);
			builder.bind("java:/datasources/TestDS", dataSource);
			builder.activate();
		} catch (NamingException ex) {
			ex.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testConcrete = this.getNewConcreteEntity();
	}

	@After
	public void tearDown() throws Exception {

		// Ensure that there are no remaining concretes in the repository
		// that were created by this Test class
		List<ConcreteEntity> remainingConcretees = concreteDao.getAll();

		Iterator<ConcreteEntity> iter = remainingConcretees.iterator();

		while (iter.hasNext()) {
			ConcreteEntity concrete = iter.next();
			if (concrete.getInsertUser().equals(getUserId())) {
				concreteDao.delete(concrete.getConcreteId());
			}
		}
	}

	@Test(expected=NullPointerException.class)
	public void testNullEntity() {
		ConcreteDao nullEntityConcreteDao = new NullEntityConcreteDaoImpl();

		//  should not get here
		System.err.println("Should not get here: " + nullEntityConcreteDao.toString());
	}

	@Test
	public void testGetConcretes() {
		assertNotNull(concreteDao);
		try {
			List<ConcreteEntity> concretes = concreteDao.getAll();

			assertNotNull(concretes);
			assertTrue(concretes instanceof List<?>);

		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testGetConcreteById() {
		assertNotNull(concreteDao);
		try {

			// Save the test Concrete Entity
			concreteDao.save(testConcrete);

			// Assert that the test concrete is not in the list of current concretes
			ConcreteEntity dbConcrete = concreteDao.get(testConcrete.getConcreteId());

			assertNotNull(dbConcrete);
			assertTrue(dbConcrete instanceof ConcreteEntity);
			assertTrue(dbConcrete.getName().equals(testConcrete.getName()));
			assertNotNull(dbConcrete.getInsertTime());
			assertNotNull(dbConcrete.getInsertUser());
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testSaveConcrete() {
		assertNotNull(concreteDao);
		try {

			// Assert that the test concrete is not in the list of current concretes
			List<ConcreteEntity> concretes_pre = concreteDao.getAll();

			assertFalse(concretes_pre.contains(testConcrete));

			// Add the test concrete, and assert that the concrete returned-as-saved is
			// equivalent to the test concrete
			ConcreteEntity savedConcrete = concreteDao.save(testConcrete);

			assertNotNull(savedConcrete);
			assertTrue(savedConcrete instanceof ConcreteEntity);
			assertTrue(savedConcrete.getName().equals(testConcrete.getName()));
			assertNotNull(savedConcrete.getInsertTime());
			assertNotNull(savedConcrete.getInsertUser());

			// Assert that that test concrete is now in the list of current concretes
			List<ConcreteEntity> concretes_post = concreteDao.getAll();

			assertTrue(concretes_post.contains(savedConcrete));

			// Save the savedConcrete in the testConcrete field
			testConcrete = savedConcrete;
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testUpdateConcrete() {
		assertNotNull(concreteDao);
		try {

			concreteDao.save(testConcrete);

			// Assert that the test concrete is in the list of current concretes
			List<ConcreteEntity> concretes_pre = concreteDao.getAll();

			assertTrue(concretes_pre.contains(testConcrete));

			// Update the test concrete with a new catchphrase
			testConcrete.setUpdateTime(getNowTimestamp());
			testConcrete.setUpdateUser(getUserId());

			ConcreteEntity updatedConcrete = concreteDao.save(testConcrete);

			assertNotNull(updatedConcrete);
			assertTrue(updatedConcrete instanceof ConcreteEntity);
			assertTrue(updatedConcrete.getName().equals(testConcrete.getName()));
			assertNotNull(updatedConcrete.getInsertTime());
			assertNotNull(updatedConcrete.getInsertUser());
			assertNotNull(updatedConcrete.getUpdateTime());
			assertNotNull(updatedConcrete.getUpdateUser());

			// Assert that that test concrete is now in the list of current concretes
			List<ConcreteEntity> concretes_post = concreteDao.getAll();

			assertTrue(concretes_post.contains(updatedConcrete));
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testDeleteConcrete() {
		assertNotNull(concreteDao);
		try {
			concreteDao.save(testConcrete);

			// Assert that the test concrete is in the list of current concretes
			List<ConcreteEntity> concretes_pre = concreteDao.getAll();

			assertTrue(concretes_pre.contains(testConcrete));

			// Remove the test concrete
			concreteDao.delete(testConcrete.getConcreteId());

			// Assert that that test concrete is now in the list of current concretes
			List<ConcreteEntity> concretes_post = concreteDao.getAll();

			assertFalse(concretes_post.contains(testConcrete));
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testDeleteConcreteThatDoesNotExist() {
		assertNotNull(concreteDao);
		try {

			// Get the list of current concretes
			List<ConcreteEntity> concretes = concreteDao.getAll();

			Integer maxConcreteId = 0;

			// Get the current maximum concreteId
			if ((concretes != null) && (!concretes.isEmpty())) {
				for (ConcreteEntity concrete : concretes) {
					maxConcreteId = Math.max(maxConcreteId.intValue(), concrete.getConcreteId());
				}
			}

			// Add 1 to the maximum concreteId to get a concreteId that does
			// not exist
			Integer bogusConcreteId = maxConcreteId += 1;

			// Remove the bogus concreteId
			ConcreteEntity bogusConcrete = concreteDao.delete(bogusConcreteId);

			assertNull(bogusConcrete);
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Failed with RuntimeException: " + e);
		}
	}

	@Test
	public void testCountAll() {

		Long initialCount = concreteDao.countAll();
		assertNotNull(initialCount);

		concreteDao.save(testConcrete);

		Long postSaveCount = concreteDao.countAll();
		assertNotNull(postSaveCount);
		assertTrue(postSaveCount.intValue() == initialCount.intValue() + 1);
	}

	private ConcreteEntity getNewConcreteEntity() {

		// Set up the Test ConcreteEntity
		long time = System.currentTimeMillis();

		ConcreteEntity newConcrete = new ConcreteEntity();
		newConcrete.setName("TestConcrete_" + time);
		newConcrete.setInsertTime(getNowTimestamp());
		newConcrete.setInsertUser(getUserId());

		return (newConcrete);
	}

	/**
	 * Gets the Timestamp for right now
	 * 
	 * @return Timestamp representing right now
	 */
	private static Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * Gets the userName of the who is saving this information
	 * 
	 * @return String for the name of who will be saving this information
	 */
	private static String getUserId() {
		return ConcreteDaoIntegration.class.getSimpleName();
	}
}
