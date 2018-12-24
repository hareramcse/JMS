package com.hs;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSProducer {
	public static void main(String[] args) {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		try {
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination1 = session.createQueue("HELLOWWORLD.TESTQ");
			MessageProducer messageProducer = session.createProducer(destination1);
			messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			String text = "Hello world from " + Thread.currentThread().getName();
			TextMessage message = session.createTextMessage(text);
			System.out.println("Sent message " + message.hashCode() + " " + Thread.currentThread().getName());
			messageProducer.send(message);
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}