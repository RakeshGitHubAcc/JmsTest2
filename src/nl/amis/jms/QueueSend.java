package nl.amis.jms;

import java.util.Hashtable;

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

public class QueueSend {
	private final static String JNDI_FACTORY = 
	        "org.jboss.naming.remote.client.InitialContextFactory";
	    private final static String JMS_FACTORY = "jms/RemoteConnectionFactory";
	    private final static String QUEUE = "jms/queue/test";
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
	public static void main(String[] args) throws Exception{
		  InitialContext ic = getInitialContext();
	        QueueConnectionFactory qconFactory = 
	            (QueueConnectionFactory)ic.lookup(JMS_FACTORY);
	        QueueConnection qcon = 
	            qconFactory.createQueueConnection(USER_NAME,PASSWORD);
	        QueueSession qsession = qcon.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
	        Queue queue = (Queue)ic.lookup(QUEUE);
	        QueueSender qsender =  qsession.createSender(queue);

	        qcon.start();

	        TextMessage msg = qsession.createTextMessage();;
	        msg.setText("HelloWorld from sender...Test");
	        qsender.send(msg);    

	        qsender.close();
	        qsession.close();
	        qcon.close();

	}

}
