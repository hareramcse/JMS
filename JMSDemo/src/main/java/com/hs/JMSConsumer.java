package com.hs;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSConsumer {
	public static void main(String[] args) {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		try {
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("HELLOWWORLD.TESTQ");
			MessageConsumer messageConsumer = session.createConsumer(destination);

			Message message = messageConsumer.receive(1000);
			if (message instanceof TextMessage) {
				TextMessage txtmsg = (TextMessage) message;
				String txt = txtmsg.getText();
				System.out.println("received " + txt);
			} else {
				System.out.println("received " + message);
			}
			messageConsumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}