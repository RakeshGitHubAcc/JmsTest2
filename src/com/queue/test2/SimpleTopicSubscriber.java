package com.queue.test2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * The SimpleTopicSubscriber class consists only of a main method, which
 * receives one or more messages from a topic using asynchronous message
 * delivery. It uses the message listener TextListener. Run this program in
 * conjunction with SimpleTopicPublisher.
 * 
 * Specify a topic name on the command line when you run the program. To end the
 * program, enter Q or q on the command line.
 */

public class SimpleTopicSubscriber {

	private final static String JNDI_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	private final static String jbossUrl = "remote://localhost:4447";
	private final static String USER_NAME = "jms";
	private final static String PASSWORD = "jboss1";
	private final static String JMS_FACTORY = "jms/RemoteConnectionFactory";
	
	/*
     * To Run this program pass queue name 
     * 'jms/topic/test' and 'no. of message(count integer value)' 
     * you want to send to the queue as param 
     */

	// get initial context object
	private static InitialContext getInitialContext() throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, jbossUrl);
		env.put(Context.SECURITY_PRINCIPAL, USER_NAME);
		env.put(Context.SECURITY_CREDENTIALS, PASSWORD);
		return new InitialContext(env);
	}

	public static void main(String[] args) {
		String topicName = null;
		InitialContext ic = null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicSubscriber topicSubscriber = null;
		TextListener topicListener = null;
		TextMessage message = null;
		InputStreamReader inputStreamReader = null;
		char answer = '\0';
		/*
		 * Read topic name from command line and display it.
		 */
		if (args.length != 1) {
			System.out.println("Usage: java "
					+ "SimpleTopicSubscriber <topic-name>");
			System.exit(1);
		}
		topicName = new String(args[0]);
		System.out.println("Topic name is " + topicName);
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
			topicConnectionFactory = (TopicConnectionFactory) ic
					.lookup(JMS_FACTORY);
			topic = (Topic) ic.lookup(topicName);
		} catch (NamingException e) {
			System.out.println("JNDI API lookup failed: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		/*
		 * Create connection. Create session from connection; false means
		 * session is not transacted. Create subscriber. Register message
		 * listener (TextListener). Receive text messages from topic. When all
		 * messages have been received, enter Q to quit. Close connection.
		 */
		try {
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicSubscriber = topicSession.createSubscriber(topic);
			topicListener = new TextListener();
			topicSubscriber.setMessageListener(topicListener);
			topicConnection.start();
			System.out.println("To end program, enter Q or q, "+ "then <return>");
			inputStreamReader = new InputStreamReader(System.in);
			while (!((answer == 'q') || (answer == 'Q'))) {
				try {
					answer = (char) inputStreamReader.read();
				} catch (IOException e) {
					System.out.println("I/O exception: " + e.toString());
				}
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