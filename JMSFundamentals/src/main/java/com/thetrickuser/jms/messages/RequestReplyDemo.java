package com.thetrickuser.jms.messages;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Random;

public class RequestReplyDemo {

    public static void main(String[] args) throws NamingException {
        // create an initial context by specifying it in jndi.properties file
        InitialContext initialContext = new InitialContext();

        // create a queue to for sending requests
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        // create a queue to receive replies
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {

            // create a producer to send messages
            JMSProducer requestProducer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage("Hello from producer");

            // this line tells that this particular message is waiting for replies on replyQueue
            message.setJMSReplyTo(replyQueue);

            // create a consumer to consume replies from replyQueue
            JMSConsumer replyConsumer = jmsContext.createConsumer(message.getJMSReplyTo());

            // send message to request queue
            requestProducer.send(requestQueue, message);
            System.out.println(message.getText());
            System.out.println(message.getJMSMessageID());

            // create a consumer to receive message from requestQueue
            JMSConsumer requestConsumer = jmsContext.createConsumer(requestQueue);
            Message receivedMessage = requestConsumer.receive();

            // create a reply producer
            JMSProducer replyProducer = jmsContext.createProducer();
            TextMessage replyMessage = jmsContext.createTextMessage("Hello from consumer");

            // attach the reply message's correlation id to received message's message id
            replyMessage.setJMSCorrelationID(receivedMessage.getJMSMessageID());

            // send message to the queue on which the received message is waiting for replies
            replyProducer.send(receivedMessage.getJMSReplyTo(), replyMessage);

            // replyConsumer receives the reply
            TextMessage receivedReply = (TextMessage) replyConsumer.receive();
            System.out.println(receivedReply.getText());

            // make sure jms correlation id of received reply is same as message id of sent message request
            System.out.println(receivedReply.getJMSCorrelationID());


        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
