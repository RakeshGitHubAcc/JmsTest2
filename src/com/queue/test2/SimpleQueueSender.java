package com.queue.test2;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleQueueSender {
	    private final static String JNDI_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	    private final static String JMS_FACTORY = "jms/RemoteConnectionFactory";
	    private final static String jbossUrl = "remote://localhost:4447";
	    private final static String USER_NAME="jms";
	    private final static String PASSWORD="jboss1";
	    /*
	     * To Run this program pass queue name 
	     * 'jms/queue/test' and 'no. of message(count integer value)' 
	     * you want to send to the queue as param 
	     */
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
		QueueSender queueSender = null;
		TextMessage message = null;
		final int NUM_MSGS;
		if ((args.length < 1) || (args.length > 2)) {
			System.out.println("Usage: java SimpleQueueSender "
					+ "<queue-name> [<number-of-messages>]");
			System.exit(1);
		}
		//get the queue name from args to main method
		queueName = new String(args[0]);
		System.out.println("Queue name is " + queueName);
		if (args.length == 2) {
			NUM_MSGS = (new Integer(args[1])).intValue();
		} else {
			NUM_MSGS = 1;
		}
		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		try {
			ic = getInitialContext();
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
		 * session is not transacted. Create sender and text message. Send
		 * messages, varying text slightly. Send end-of-messages message.
		 * Finally, close connection.
		 */
		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			/*
			 * First agrument  is for transactional 
			 * Second agrument is for automatically acknowledges
			 */
			queueSession = queueConnection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			queueSender = queueSession.createSender(queue);
			message = queueSession.createTextMessage();
			for (int i = 0; i < NUM_MSGS; i++) {
				message.setText("This is message " + (i + 1));
				System.out.println("Sending message: " + message.getText());
				queueSender.send(message);
			}
			/*
			 * Send a non-text control message indicating end of messages.
			 */
			queueSender.send(queueSession.createMessage());
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
