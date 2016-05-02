package com.queue.test2;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleTopicPublisher {
	 private final static String JNDI_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	 private final static String jbossUrl = "remote://localhost:4447";
	 private final static String USER_NAME="jms";
	 private final static String PASSWORD="jboss1";
	 private final static String JMS_FACTORY = "jms/RemoteConnectionFactory";
	 /*
	     * To Run this program pass queue name 
	     * 'jms/topic/test' and 'no. of message(count integer value)' 
	     * you want to send to the queue as param 
	*/
	//get initial context object
	private static InitialContext getInitialContext() throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, jbossUrl);
		env.put(Context.SECURITY_PRINCIPAL, USER_NAME);
		env.put(Context.SECURITY_CREDENTIALS, PASSWORD);
		return new InitialContext(env);
	}

	public static void main(String args[]) {
		String topicName = null;
		InitialContext ic=null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicPublisher topicPublisher = null;
		TextMessage message = null;
		final int NUM_MSGS;
		if ((args.length < 1) || (args.length > 2)) {
			System.out.println("Usage: java "
					+ "SimpleTopicPublisher <topic-name> "
					+ "[<number-of-messages>]");
			System.exit(1);
		}
		// get topic name from command prompt
		topicName = new String(args[0]);
		System.out.println("Topic name is " + topicName);
		// get no. of messages
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
			e.printStackTrace();
			System.exit(1);
		}
		/*
		 * Look up connection factory and topic. If either does not exist, exit.
		 */
		try {
			topicConnectionFactory = (TopicConnectionFactory) ic.lookup(JMS_FACTORY);
			topic = (Topic) ic.lookup(topicName);
		} catch (NamingException e) {
			System.out.println("JNDI API lookup failed: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		/*
		 * Create connection. Create session from connection; false means
		 * session is not transacted. Create publisher and text message. Send
		 * messages, varying text slightly. Finally, close connection.
		 */
		try {
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicPublisher = topicSession.createPublisher(topic);
			message = topicSession.createTextMessage();
			for (int i = 0; i < NUM_MSGS; i++) {
				message.setText("This is message " + (i + 1));
				System.out.println("Publishing message: " + message.getText());
				topicPublisher.publish(message);
			}
		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			if (topicConnection != null) {
				try {
					topicConnection.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
