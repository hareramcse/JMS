package com.hs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class BasicJmsChat implements MessageListener {

	private JmsTemplate jmsTemplate;
	private Topic chatTopic;

	private static String userId;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("user name is required");
		} else {
			userId = args[0];
		}
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		BasicJmsChat basicJmsChat = (BasicJmsChat) context.getBean("basicJmsChat");
		TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) basicJmsChat.jmsTemplate
				.getConnectionFactory();
		try {
			TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
			basicJmsChat.publish(topicConnection, basicJmsChat.chatTopic, userId);
			basicJmsChat.subscribe(topicConnection, basicJmsChat.chatTopic, basicJmsChat);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void publish(TopicConnection topicConnection, Topic chatTopic, String userId) {
		TopicSession topicSession;
		try {
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicPublisher topicPublisher = topicSession.createPublisher(chatTopic);
			topicConnection.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String messageToSend = reader.readLine();
					if (messageToSend.equalsIgnoreCase("exit")) {
						topicConnection.close();
						System.exit(0);
					} else {
						TextMessage textMessage = topicSession.createTextMessage();
						textMessage.setText("\n[" + userId + ":" + messageToSend + "]");
						topicPublisher.publish(textMessage);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void subscribe(TopicConnection topicConnection, Topic chatTopic, BasicJmsChat commandLineChat) {
		try {
			TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicSubscriber topicSubscriber = topicSession.createSubscriber(chatTopic);
			topicSubscriber.setMessageListener(commandLineChat);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				String messageText = ((TextMessage) message).getText();
				if (!messageText.startsWith("[" + userId)) {
					System.out.println(messageText);
				}
			} catch (JMSException e) {
				System.out.println("An error occured");
				e.printStackTrace();
			}
		} else {
			String errMsg = "String is not of type TextMessage";
			System.err.println(errMsg);
			throw new RuntimeException();
		}
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Topic getChatTopic() {
		return chatTopic;
	}

	public void setChatTopic(Topic chatTopic) {
		this.chatTopic = chatTopic;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		BasicJmsChat.userId = userId;
	}
}