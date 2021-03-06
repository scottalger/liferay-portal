/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.cluster;

import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.cluster.messaging.ClusterForwardMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.test.AdviseWith;
import com.liferay.portal.test.ApsectJMockingNewClassLoaderJUnitTestRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 * @author Shuyang Zhou
 */
@RunWith(ApsectJMockingNewClassLoaderJUnitTestRunner.class)
public class ClusterLinkImplTest {

	@AdviseWith(adviceClasses = {DisableClusterLinkAdvice.class})
	@Test
	public void testDestroy1() throws Exception {
		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testDestroy2() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(2);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Assert.assertEquals(2, jChannels.size());

		JChannel jChannel = jChannels.get(0);

		Assert.assertTrue(jChannel.isOpen());

		jChannel = jChannels.get(1);

		Assert.assertTrue(jChannel.isOpen());

		clusterLinkImpl.destroy();

		jChannels = getJChannels(clusterLinkImpl);

		Assert.assertEquals(2, jChannels.size());

		jChannel = jChannels.get(0);

		Assert.assertFalse(jChannel.isOpen());

		jChannel = jChannels.get(1);

		Assert.assertFalse(jChannel.isOpen());
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class, LoggerAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testGetChannel() throws Exception {
		LoggerAdvice.setLoggerInfo(ClusterLinkImpl.class.getName(), Level.FINE);

		TransportationConfigurationAdvice.setChannelCount(2);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Assert.assertEquals(2, jChannels.size());

		JChannel jChannel = jChannels.get(0);

		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL1));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL2));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL3));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL4));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL5));

		jChannel = jChannels.get(1);

		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL6));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL7));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL8));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL9));
		Assert.assertSame(
			jChannel, clusterLinkImpl.getChannel(Priority.LEVEL10));

		clusterLinkImpl.destroy();
	}

	@AdviseWith(adviceClasses = {DisableClusterLinkAdvice.class})
	@Test
	public void testGetLocalTransportAddresses1() throws Exception {
		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<Address> addresses = clusterLinkImpl.getLocalTransportAddresses();

		Assert.assertSame(Collections.emptyList(), addresses);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testGetLocalTransportAddresses2() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(2);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<Address> addresses = clusterLinkImpl.getLocalTransportAddresses();

		Assert.assertEquals(2, addresses.size());

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Assert.assertSame(
			getJGroupsAddress(jChannels, 0), getRealAddress(addresses, 0));
		Assert.assertSame(
			getJGroupsAddress(jChannels, 1), getRealAddress(addresses, 1));

		clusterLinkImpl.destroy();
	}

	@AdviseWith(adviceClasses = {DisableClusterLinkAdvice.class})
	@Test
	public void testGetTransportAddressesByPriority1() throws Exception {
		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<Address> addresses = clusterLinkImpl.getTransportAddresses(
			Priority.LEVEL1);

		Assert.assertSame(Collections.emptyList(), addresses);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testGetTransportAddressesByPriority2() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(2);

		ClusterLinkImpl clusterLinkImpl1 = getClusterLinkImpl();
		ClusterLinkImpl clusterLinkImpl2 = getClusterLinkImpl();

		List<JChannel> jChannels1 = getJChannels(clusterLinkImpl1);

		Assert.assertEquals(2, jChannels1.size());

		List<JChannel> jChannels2 = getJChannels(clusterLinkImpl2);

		Assert.assertEquals(2, jChannels2.size());

		List<Address> addresses1 = clusterLinkImpl1.getTransportAddresses(
			Priority.LEVEL1);

		Assert.assertEquals(2, addresses1.size());

		List<Address> addresses2 = clusterLinkImpl1.getTransportAddresses(
			Priority.LEVEL6);

		Assert.assertEquals(2, addresses2.size());

		Assert.assertEquals(
			getJGroupsAddress(jChannels1, 0), getRealAddress(addresses1, 0));
		Assert.assertEquals(
			getJGroupsAddress(jChannels1, 1), getRealAddress(addresses2, 0));
		Assert.assertEquals(
			getJGroupsAddress(jChannels2, 0), getRealAddress(addresses1, 1));
		Assert.assertEquals(
			getJGroupsAddress(jChannels2, 1), getRealAddress(addresses2, 1));

		clusterLinkImpl1.destroy();
		clusterLinkImpl2.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testInitChannel1() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(
			ClusterLinkImpl.MAX_CHANNEL_COUNT + 1);

		try {
			getClusterLinkImpl();

			Assert.fail();
		}
		catch (IllegalStateException ise) {
		}
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testInitChannel2() throws Exception {
		try {
			getClusterLinkImpl();

			Assert.fail();
		}
		catch (IllegalStateException ise) {
		}
	}

	@AdviseWith(adviceClasses = {DisableClusterLinkAdvice.class})
	@Test
	public void testSendMulticastMessage1() throws Exception {
		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		Message message = createMessage();

		clusterLinkImpl.sendMulticastMessage(message, Priority.LEVEL1);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendMulticastMessage2() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl1 = getClusterLinkImpl();
		ClusterLinkImpl clusterLinkImpl2 = getClusterLinkImpl();
		ClusterLinkImpl clusterLinkImpl3 = getClusterLinkImpl();

		List<JChannel> jChannels1 = getJChannels(clusterLinkImpl1);
		List<JChannel> jChannels2 = getJChannels(clusterLinkImpl2);
		List<JChannel> jChannels3 = getJChannels(clusterLinkImpl3);

		TestReceiver testReceiver1 = getTestReceiver(jChannels1, 0);
		TestReceiver testReceiver2 = getTestReceiver(jChannels2, 0);
		TestReceiver testReceiver3 = getTestReceiver(jChannels3, 0);

		Message message = createMessage();

		clusterLinkImpl1.sendMulticastMessage(message, Priority.LEVEL1);

		String localMessage1 = testReceiver1.waitLocalMessage();
		String remoteMessage1 = testReceiver1.waitRemoteMessage();

		String localMessage2 = testReceiver2.waitLocalMessage();
		String remoteMessage2 = testReceiver2.waitRemoteMessage();

		String localMessage3 = testReceiver3.waitLocalMessage();
		String remoteMessage3 = testReceiver3.waitRemoteMessage();

		String messageKey = (String)message.getPayload();

		Assert.assertEquals(messageKey, localMessage1);
		Assert.assertNull(remoteMessage1);
		Assert.assertNull(localMessage2);
		Assert.assertEquals(messageKey, remoteMessage2);
		Assert.assertNull(localMessage3);
		Assert.assertEquals(messageKey, remoteMessage3);

		clusterLinkImpl1.destroy();
		clusterLinkImpl2.destroy();
		clusterLinkImpl3.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class, LoggerAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendMulticastMessage3() throws Exception {
		LoggerAdvice.setLoggerInfo(
			ClusterLinkImpl.class.getName(), Level.WARNING);

		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		Message message = createMessage();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		JChannel jChannel = jChannels.get(0);

		jChannel.close();

		clusterLinkImpl.sendMulticastMessage(message, Priority.LEVEL1);

		assertLogger(
			"Unable to send multicast message " + message, Exception.class);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class, LoggerAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendMulticastMessage4() throws Exception {
		LoggerAdvice.setLoggerInfo(
			ClusterLinkImpl.class.getName(), Level.WARNING);

		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Message message = createMessage();

		JChannel jChannel = jChannels.get(0);

		jChannel.disconnect();

		clusterLinkImpl.sendMulticastMessage(message, Priority.LEVEL1);

		assertLogger(
			"Unable to send multicast message " + message, Exception.class);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(adviceClasses = {DisableClusterLinkAdvice.class})
	@Test
	public void testSendUnicastMessage1() throws Exception {
		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		Message message = createMessage();

		clusterLinkImpl.sendUnicastMessage(
			new AddressImpl(new MockAddress()), message, Priority.LEVEL1);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendUnicastMessage2() throws Exception {
		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl1 = getClusterLinkImpl();
		ClusterLinkImpl clusterLinkImpl2 = getClusterLinkImpl();

		List<JChannel> jChannels1 = getJChannels(clusterLinkImpl1);
		List<JChannel> jChannels2 = getJChannels(clusterLinkImpl2);

		TestReceiver testReceiver1 = getTestReceiver(jChannels1, 0);
		TestReceiver testReceiver2 = getTestReceiver(jChannels2, 0);

		Message message = createMessage();

		clusterLinkImpl1.sendUnicastMessage(
			new AddressImpl(jChannels2.get(0).getAddress()), message,
			Priority.LEVEL1);

		String localMessage1 = testReceiver1.waitLocalMessage();
		String remoteMessage1 = testReceiver1.waitRemoteMessage();
		String localMessage2 = testReceiver2.waitLocalMessage();
		String remoteMessage2 = testReceiver2.waitRemoteMessage();

		String messageKey = (String)message.getPayload();

		Assert.assertNull(localMessage1);
		Assert.assertNull(remoteMessage1);
		Assert.assertNull(localMessage2);
		Assert.assertEquals(messageKey, remoteMessage2);

		clusterLinkImpl1.destroy();
		clusterLinkImpl2.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class, LoggerAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendUnicastMessage3() throws Exception {
		LoggerAdvice.setLoggerInfo(
			ClusterLinkImpl.class.getName(), Level.WARNING);

		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Message message = createMessage();

		JChannel jChannel = jChannels.get(0);

		jChannel.close();

		clusterLinkImpl.sendUnicastMessage(
			new AddressImpl(new MockAddress()), message, Priority.LEVEL1);

		assertLogger(
			"Unable to send unicast message " + message, Exception.class);

		clusterLinkImpl.destroy();
	}

	@AdviseWith(
		adviceClasses = {
			EnableClusterLinkAdvice.class, LoggerAdvice.class,
			TransportationConfigurationAdvice.class
		}

	)
	@Test
	public void testSendUnicastMessage4() throws Exception {
		LoggerAdvice.setLoggerInfo(
			ClusterLinkImpl.class.getName(), Level.WARNING);

		TransportationConfigurationAdvice.setChannelCount(1);

		ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		Message message = createMessage();

		JChannel jChannel = jChannels.get(0);

		jChannel.disconnect();

		clusterLinkImpl.sendUnicastMessage(
			new AddressImpl(new MockAddress()), message, Priority.LEVEL1);

		assertLogger(
			"Unable to send unicast message " + message, Exception.class);

		clusterLinkImpl.destroy();
	}

	@Aspect
	public static class DisableClusterLinkAdvice {

		@Around(
			"set(* com.liferay.portal.util.PropsValues.CLUSTER_LINK_ENABLED)")
		public Object disableClusterLink(
				ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {

			return proceedingJoinPoint.proceed(new Object[]{Boolean.FALSE});
		}

	}

	@Aspect
	public static class EnableClusterLinkAdvice {

		@Around(
			"set(* com.liferay.portal.util.PropsValues.CLUSTER_LINK_ENABLED)")
		public Object enableClusterLink(ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {

			return proceedingJoinPoint.proceed(new Object[]{Boolean.TRUE});
		}

	}

	@Aspect
	public static class LoggerAdvice {

		public static CaptureHandler getCaptureHandler() {
			return _captureHandler;
		}

		public static void setLoggerInfo(String loggerName, Level loggerLevel) {
			_loggerName = loggerName;
			_loggerLevel = loggerLevel;
		}

		@Before(
			"call(public com.liferay.portal.kernel.log.Jdk14LogImpl.new(" +
				"java.util.logging.Logger)) && args(logger)")
		public void createLogger(Logger logger) {
			String loggerName = logger.getName();

			if (loggerName.equals(_loggerName)) {
				for (Handler handler : logger.getHandlers()) {
					logger.removeHandler(handler);
				}

				logger.setLevel(_loggerLevel);
				logger.setUseParentHandlers(false);

				_captureHandler = new CaptureHandler();

				logger.addHandler(_captureHandler);
			}
		}

		private static CaptureHandler _captureHandler;
		private static Level _loggerLevel;
		private static String _loggerName;

	}

	@Aspect
	public static class TransportationConfigurationAdvice {

		public static void setChannelCount(int channelCount) {
			_CHANNEL_COUNT = channelCount;
		}

		@Around(
			"execution(* com.liferay.portal.util.PropsUtil.getProperties(" +
				"String, boolean))")
		public Object getTransportationConfigurationProperties(
				ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {

			Object[] arguments = proceedingJoinPoint.getArgs();

			if (PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT.equals(
					arguments[0]) &&
				Boolean.TRUE.equals(arguments[1])) {

				Properties properties = new Properties();

				for (int i = 0; i < _CHANNEL_COUNT; i++) {
					properties.put(
						PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT +
							CharPool.POUND + i, "udp.xml");
				}

				return properties;
			}

			return proceedingJoinPoint.proceed();
		}

		private static int _CHANNEL_COUNT = 0;

	}

	protected void assertLogger(String message, Class<?> exceptionClass) {
		CaptureHandler captureHandler = LoggerAdvice.getCaptureHandler();

		Assert.assertNotNull(captureHandler);

		List<LogRecord> logRecords = captureHandler.getLogRecords();

		if (message == null) {
			Assert.assertEquals(0, logRecords.size());

			return;
		}

		Assert.assertEquals(1, logRecords.size());

		LogRecord logRecord = logRecords.get(0);

		Assert.assertEquals(message, logRecord.getMessage());

		if (exceptionClass == null) {
			Assert.assertNull(logRecord.getThrown());
		}
		else {
			Assert.assertNotNull(logRecord.getThrown());
		}

		captureHandler.flush();
	}

	protected Message createMessage() {
		Message message = new Message();

		message.setPayload(UUID.randomUUID().toString());

		return message;
	}

	protected ClusterLinkImpl getClusterLinkImpl() throws Exception {
		ClusterLinkImpl clusterLinkImpl = new ClusterLinkImpl();

		clusterLinkImpl.setClusterForwardMessageListener(
			new ClusterForwardMessageListener());

		clusterLinkImpl.afterPropertiesSet();

		List<JChannel> jChannels = getJChannels(clusterLinkImpl);

		if (jChannels != null) {
			for (JChannel channel : jChannels) {
				channel.setReceiver(
					new TestReceiver(new AddressImpl(channel.getAddress())));
			}
		}

		return clusterLinkImpl;
	}

	protected List<JChannel> getJChannels(ClusterLinkImpl clusterLinkImpl)
		throws Exception {

		Field field = ReflectionUtil.getDeclaredField(
			ClusterLinkImpl.class, "_transportChannels");

		return (List<JChannel>)field.get(clusterLinkImpl);
	}

	protected org.jgroups.Address getJGroupsAddress(
		List<JChannel> jChannels, int index) {

		JChannel jChannel = jChannels.get(index);

		return jChannel.getAddress();
	}

	protected Object getRealAddress(List<Address> addresses, int index) {
		Address address = addresses.get(index);

		return address.getRealAddress();
	}

	protected TestReceiver getTestReceiver(
		List<JChannel> jChannels, int index) {

		JChannel jChannel = jChannels.get(index);

		return (TestReceiver)jChannel.getReceiver();
	}

	private static class CaptureHandler extends Handler {

		@Override
		public void close() throws SecurityException {
			_logRecords.clear();
		}

		@Override
		public void flush() {
			_logRecords.clear();
		}

		public List<LogRecord> getLogRecords() {
			return _logRecords;
		}

		@Override
		public boolean isLoggable(LogRecord logRecord) {
			return false;
		}

		@Override
		public void publish(LogRecord logRecord) {
			_logRecords.add(logRecord);
		}

		private List<LogRecord> _logRecords =
			new CopyOnWriteArrayList<LogRecord>();

	}

	private class MockAddress implements org.jgroups.Address {

		public int compareTo(org.jgroups.Address jGroupsAddress) {
			return 0;
		}

		public void readExternal(ObjectInput objectInput) {
		}

		public void readFrom(DataInput dataInput) throws Exception {
		}

		public int size() {
			return 0;
		}

		public void writeExternal(ObjectOutput objectOutput) {
		}

		public void writeTo(DataOutput dataOutput) throws Exception {
		}

	}

	private class TestReceiver extends BaseReceiver {

		public TestReceiver(Address address) {
			_localAddress = address;
		}

		@Override
		public void receive(org.jgroups.Message message) {
			org.jgroups.Address sourceJGroupsAddress = message.getSrc();

			Message content = (Message)message.getObject();

			String messageKey = (String)content.getPayload();

			try {
				if (sourceJGroupsAddress.equals(
						_localAddress.getRealAddress())) {

					_localMessageExchanger.exchange(messageKey);
				}
				else {
					_remoteMessageExchanger.exchange(messageKey);
				}
			}
			catch (InterruptedException ie) {
				Assert.fail();
			}
		}

		@Override
		public void viewAccepted(View view) {
			super.view = view;
		}

		public String waitLocalMessage() throws Exception {
			try {
				return _localMessageExchanger.exchange(
					null, 1000, TimeUnit.MILLISECONDS);
			}
			catch (TimeoutException te) {
				return null;
			}
		}

		public String waitRemoteMessage() throws Exception {
			try {
				return _remoteMessageExchanger.exchange(
					null, 1000, TimeUnit.MILLISECONDS);
			}
			catch (TimeoutException te) {
				return null;
			}
		}

		private Address _localAddress;
		private Exchanger<String> _localMessageExchanger =
			new Exchanger<String>();
		private Exchanger<String> _remoteMessageExchanger =
			new Exchanger<String>();

	}

}