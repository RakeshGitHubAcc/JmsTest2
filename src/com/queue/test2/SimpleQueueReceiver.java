package com.queue.test2;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleQueueReceiver {
	 private final static String JNDI_FACTORY =
	            "org.jboss.naming.remote.client.InitialContextFactory";
	    private final static String JMS_FACTORY = "jms/RemoteConnectionFactory";
	  //  private final static String QUEUE = "jms/queue/test";
	    private final static String jbossUrl = "remote://localhost:4447";
	    private final static String USER_NAME="jms";
	    private final static String PASSWORD="jboss1";
	    
	 private static InitialContext getInitialContext() throws NamingException {
	        Hashtable env = new Hashtable();
	        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
	        env.put(Context.PROVIDER_URL, jbossUrl);
	        env.put(Context.SECURITY_PRINCIPAL, USER_NAME);
	        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);
	        return new InitialContext(env);
	    }
	public static void main(String[] args) {
		String queueName = null;
		InitialContext ic=null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueReceiver queueReceiver = null;
		TextMessage message = null;
		/*
		 * Read queue name from command line and display it.
		 */
		if (args.length != 1) {
			System.out.println("Usage: java "
					+ "SimpleQueueReceiver <queue-name>");
			System.exit(1);
		}
		queueName = new String(args[0]);
		System.out.println("Queue name is " + queueName);
		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		try {
			ic= getInitialContext();
		} catch (NamingException e) {
			System.out.println("Could not create JNDI API " + "context: "
					+ e.toString());
			System.exit(1);
		}
		/*
		 * Look up connection factory and queue. If either does not exist, exit.
		 */
		try {
			queueConnectionFactory = (QueueConnectionFactory) ic.lookup(JMS_FACTORY);
			queue = (Queue) ic.lookup(queueName);
		} catch (NamingException e) {
			System.out.println("JNDI API lookup failed: " + e.toString());
			System.exit(1);
		}
		/*
		 * Create connection. Create session from connection; false means
		 * session is not transacted. Create receiver, then start message
		 * delivery. Receive all text messages from queue until a non-text
		 * message is received indicating end of message stream. Close
		 * connection.
		 */
		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			queueReceiver = queueSession.createReceiver(queue);
			queueConnection.start();
			while (true) {
				Message m = queueReceiver.receive(1);
				if (m != null) {
					if (m instanceof TextMessage) {
						message = (TextMessage) m;
						System.out.println("Reading message: "
								+ message.getText());
					} else {
						break;
					}
				}
			}
		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			if (queueConnection != null) {
				try {
					queueConnection.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
