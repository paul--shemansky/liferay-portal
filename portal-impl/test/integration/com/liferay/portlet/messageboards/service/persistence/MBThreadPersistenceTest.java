/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.messageboards.service.persistence;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.TransactionalTestRule;
import com.liferay.portal.test.runners.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.test.RandomTestUtil;

import com.liferay.portlet.messageboards.NoSuchThreadException;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.model.impl.MBThreadModelImpl;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class MBThreadPersistenceTest {
	@ClassRule
	public static TransactionalTestRule transactionalTestRule = new TransactionalTestRule(Propagation.REQUIRED);

	@BeforeClass
	public static void setupClass() throws TemplateException {
		try {
			DBUpgrader.upgrade();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		TemplateManagerUtil.init();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBThread> iterator = _mbThreads.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread mbThread = _persistence.create(pk);

		Assert.assertNotNull(mbThread);

		Assert.assertEquals(mbThread.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBThread newMBThread = addMBThread();

		_persistence.remove(newMBThread);

		MBThread existingMBThread = _persistence.fetchByPrimaryKey(newMBThread.getPrimaryKey());

		Assert.assertNull(existingMBThread);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBThread();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread newMBThread = _persistence.create(pk);

		newMBThread.setUuid(RandomTestUtil.randomString());

		newMBThread.setGroupId(RandomTestUtil.nextLong());

		newMBThread.setCompanyId(RandomTestUtil.nextLong());

		newMBThread.setUserId(RandomTestUtil.nextLong());

		newMBThread.setUserName(RandomTestUtil.randomString());

		newMBThread.setCreateDate(RandomTestUtil.nextDate());

		newMBThread.setModifiedDate(RandomTestUtil.nextDate());

		newMBThread.setCategoryId(RandomTestUtil.nextLong());

		newMBThread.setRootMessageId(RandomTestUtil.nextLong());

		newMBThread.setRootMessageUserId(RandomTestUtil.nextLong());

		newMBThread.setMessageCount(RandomTestUtil.nextInt());

		newMBThread.setViewCount(RandomTestUtil.nextInt());

		newMBThread.setLastPostByUserId(RandomTestUtil.nextLong());

		newMBThread.setLastPostDate(RandomTestUtil.nextDate());

		newMBThread.setPriority(RandomTestUtil.nextDouble());

		newMBThread.setQuestion(RandomTestUtil.randomBoolean());

		newMBThread.setStatus(RandomTestUtil.nextInt());

		newMBThread.setStatusByUserId(RandomTestUtil.nextLong());

		newMBThread.setStatusByUserName(RandomTestUtil.randomString());

		newMBThread.setStatusDate(RandomTestUtil.nextDate());

		_mbThreads.add(_persistence.update(newMBThread));

		MBThread existingMBThread = _persistence.findByPrimaryKey(newMBThread.getPrimaryKey());

		Assert.assertEquals(existingMBThread.getUuid(), newMBThread.getUuid());
		Assert.assertEquals(existingMBThread.getThreadId(),
			newMBThread.getThreadId());
		Assert.assertEquals(existingMBThread.getGroupId(),
			newMBThread.getGroupId());
		Assert.assertEquals(existingMBThread.getCompanyId(),
			newMBThread.getCompanyId());
		Assert.assertEquals(existingMBThread.getUserId(),
			newMBThread.getUserId());
		Assert.assertEquals(existingMBThread.getUserName(),
			newMBThread.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingMBThread.getCreateDate()),
			Time.getShortTimestamp(newMBThread.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingMBThread.getModifiedDate()),
			Time.getShortTimestamp(newMBThread.getModifiedDate()));
		Assert.assertEquals(existingMBThread.getCategoryId(),
			newMBThread.getCategoryId());
		Assert.assertEquals(existingMBThread.getRootMessageId(),
			newMBThread.getRootMessageId());
		Assert.assertEquals(existingMBThread.getRootMessageUserId(),
			newMBThread.getRootMessageUserId());
		Assert.assertEquals(existingMBThread.getMessageCount(),
			newMBThread.getMessageCount());
		Assert.assertEquals(existingMBThread.getViewCount(),
			newMBThread.getViewCount());
		Assert.assertEquals(existingMBThread.getLastPostByUserId(),
			newMBThread.getLastPostByUserId());
		Assert.assertEquals(Time.getShortTimestamp(
				existingMBThread.getLastPostDate()),
			Time.getShortTimestamp(newMBThread.getLastPostDate()));
		AssertUtils.assertEquals(existingMBThread.getPriority(),
			newMBThread.getPriority());
		Assert.assertEquals(existingMBThread.getQuestion(),
			newMBThread.getQuestion());
		Assert.assertEquals(existingMBThread.getStatus(),
			newMBThread.getStatus());
		Assert.assertEquals(existingMBThread.getStatusByUserId(),
			newMBThread.getStatusByUserId());
		Assert.assertEquals(existingMBThread.getStatusByUserName(),
			newMBThread.getStatusByUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingMBThread.getStatusDate()),
			Time.getShortTimestamp(newMBThread.getStatusDate()));
	}

	@Test
	public void testCountByUuid() {
		try {
			_persistence.countByUuid(StringPool.BLANK);

			_persistence.countByUuid(StringPool.NULL);

			_persistence.countByUuid((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUUID_G() {
		try {
			_persistence.countByUUID_G(StringPool.BLANK,
				RandomTestUtil.nextLong());

			_persistence.countByUUID_G(StringPool.NULL, 0L);

			_persistence.countByUUID_G((String)null, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUuid_C() {
		try {
			_persistence.countByUuid_C(StringPool.BLANK,
				RandomTestUtil.nextLong());

			_persistence.countByUuid_C(StringPool.NULL, 0L);

			_persistence.countByUuid_C((String)null, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByGroupId() {
		try {
			_persistence.countByGroupId(RandomTestUtil.nextLong());

			_persistence.countByGroupId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByRootMessageId() {
		try {
			_persistence.countByRootMessageId(RandomTestUtil.nextLong());

			_persistence.countByRootMessageId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C() {
		try {
			_persistence.countByG_C(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByG_C(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_CArrayable() {
		try {
			_persistence.countByG_C(RandomTestUtil.nextLong(),
				new long[] { RandomTestUtil.nextLong(), 0L });
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_NotC() {
		try {
			_persistence.countByG_NotC(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByG_NotC(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_S() {
		try {
			_persistence.countByG_S(RandomTestUtil.nextLong(),
				RandomTestUtil.nextInt());

			_persistence.countByG_S(0L, 0);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByC_P() {
		try {
			_persistence.countByC_P(RandomTestUtil.nextLong(),
				RandomTestUtil.nextDouble());

			_persistence.countByC_P(0L, 0D);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByL_P() {
		try {
			_persistence.countByL_P(RandomTestUtil.nextDate(),
				RandomTestUtil.nextDouble());

			_persistence.countByL_P(RandomTestUtil.nextDate(), 0D);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_L() {
		try {
			_persistence.countByG_C_L(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

			_persistence.countByG_C_L(0L, 0L, RandomTestUtil.nextDate());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_S() {
		try {
			_persistence.countByG_C_S(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

			_persistence.countByG_C_S(0L, 0L, 0);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_SArrayable() {
		try {
			_persistence.countByG_C_S(RandomTestUtil.nextLong(),
				new long[] { RandomTestUtil.nextLong(), 0L },
				RandomTestUtil.nextInt());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_NotS() {
		try {
			_persistence.countByG_C_NotS(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

			_persistence.countByG_C_NotS(0L, 0L, 0);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_NotSArrayable() {
		try {
			_persistence.countByG_C_NotS(RandomTestUtil.nextLong(),
				new long[] { RandomTestUtil.nextLong(), 0L },
				RandomTestUtil.nextInt());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_NotC_S() {
		try {
			_persistence.countByG_NotC_S(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

			_persistence.countByG_NotC_S(0L, 0L, 0);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_NotC_NotS() {
		try {
			_persistence.countByG_NotC_NotS(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

			_persistence.countByG_NotC_NotS(0L, 0L, 0);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBThread newMBThread = addMBThread();

		MBThread existingMBThread = _persistence.findByPrimaryKey(newMBThread.getPrimaryKey());

		Assert.assertEquals(existingMBThread, newMBThread);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchThreadException");
		}
		catch (NoSuchThreadException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		try {
			_persistence.filterFindByGroupId(0, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator<MBThread> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("MBThread", "uuid", true,
			"threadId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"categoryId", true, "rootMessageId", true, "rootMessageUserId",
			true, "messageCount", true, "viewCount", true, "lastPostByUserId",
			true, "lastPostDate", true, "priority", true, "question", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBThread newMBThread = addMBThread();

		MBThread existingMBThread = _persistence.fetchByPrimaryKey(newMBThread.getPrimaryKey());

		Assert.assertEquals(existingMBThread, newMBThread);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread missingMBThread = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBThread);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		MBThread newMBThread1 = addMBThread();
		MBThread newMBThread2 = addMBThread();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread1.getPrimaryKey());
		primaryKeys.add(newMBThread2.getPrimaryKey());

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbThreads.size());
		Assert.assertEquals(newMBThread1,
			mbThreads.get(newMBThread1.getPrimaryKey()));
		Assert.assertEquals(newMBThread2,
			mbThreads.get(newMBThread2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbThreads.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		MBThread newMBThread = addMBThread();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbThreads.size());
		Assert.assertEquals(newMBThread,
			mbThreads.get(newMBThread.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbThreads.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		MBThread newMBThread = addMBThread();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread.getPrimaryKey());

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbThreads.size());
		Assert.assertEquals(newMBThread,
			mbThreads.get(newMBThread.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = MBThreadLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod() {
				@Override
				public void performAction(Object object) {
					MBThread mbThread = (MBThread)object;

					Assert.assertNotNull(mbThread);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		MBThread newMBThread = addMBThread();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(MBThread.class,
				MBThread.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("threadId",
				newMBThread.getThreadId()));

		List<MBThread> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		MBThread existingMBThread = result.get(0);

		Assert.assertEquals(existingMBThread, newMBThread);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(MBThread.class,
				MBThread.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("threadId",
				RandomTestUtil.nextLong()));

		List<MBThread> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		MBThread newMBThread = addMBThread();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(MBThread.class,
				MBThread.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("threadId"));

		Object newThreadId = newMBThread.getThreadId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("threadId",
				new Object[] { newThreadId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingThreadId = result.get(0);

		Assert.assertEquals(existingThreadId, newThreadId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(MBThread.class,
				MBThread.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("threadId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("threadId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		if (!PropsValues.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			return;
		}

		MBThread newMBThread = addMBThread();

		_persistence.clearCache();

		MBThreadModelImpl existingMBThreadModelImpl = (MBThreadModelImpl)_persistence.findByPrimaryKey(newMBThread.getPrimaryKey());

		Assert.assertTrue(Validator.equals(
				existingMBThreadModelImpl.getUuid(),
				existingMBThreadModelImpl.getOriginalUuid()));
		Assert.assertEquals(existingMBThreadModelImpl.getGroupId(),
			existingMBThreadModelImpl.getOriginalGroupId());

		Assert.assertEquals(existingMBThreadModelImpl.getRootMessageId(),
			existingMBThreadModelImpl.getOriginalRootMessageId());
	}

	protected MBThread addMBThread() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread mbThread = _persistence.create(pk);

		mbThread.setUuid(RandomTestUtil.randomString());

		mbThread.setGroupId(RandomTestUtil.nextLong());

		mbThread.setCompanyId(RandomTestUtil.nextLong());

		mbThread.setUserId(RandomTestUtil.nextLong());

		mbThread.setUserName(RandomTestUtil.randomString());

		mbThread.setCreateDate(RandomTestUtil.nextDate());

		mbThread.setModifiedDate(RandomTestUtil.nextDate());

		mbThread.setCategoryId(RandomTestUtil.nextLong());

		mbThread.setRootMessageId(RandomTestUtil.nextLong());

		mbThread.setRootMessageUserId(RandomTestUtil.nextLong());

		mbThread.setMessageCount(RandomTestUtil.nextInt());

		mbThread.setViewCount(RandomTestUtil.nextInt());

		mbThread.setLastPostByUserId(RandomTestUtil.nextLong());

		mbThread.setLastPostDate(RandomTestUtil.nextDate());

		mbThread.setPriority(RandomTestUtil.nextDouble());

		mbThread.setQuestion(RandomTestUtil.randomBoolean());

		mbThread.setStatus(RandomTestUtil.nextInt());

		mbThread.setStatusByUserId(RandomTestUtil.nextLong());

		mbThread.setStatusByUserName(RandomTestUtil.randomString());

		mbThread.setStatusDate(RandomTestUtil.nextDate());

		_mbThreads.add(_persistence.update(mbThread));

		return mbThread;
	}

	private static Log _log = LogFactoryUtil.getLog(MBThreadPersistenceTest.class);
	private List<MBThread> _mbThreads = new ArrayList<MBThread>();
	private MBThreadPersistence _persistence = MBThreadUtil.getPersistence();
}