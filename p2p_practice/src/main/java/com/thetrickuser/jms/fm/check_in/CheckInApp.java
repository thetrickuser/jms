package com.thetrickuser.jms.fm.check_in;

import com.thetrickuser.jms.fm.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CheckInApp {

    public static void main(String[] args) throws NamingException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            ObjectMessage message = jmsContext.createObjectMessage();

            Passenger passenger = new Passenger();
            passenger.setEmail("123@email.com");
            passenger.setId(123);
            passenger.setFirstName("Adhyan");
            passenger.setLastName("Suryan");
            passenger.setPhone("1234567890");
            message.setObject(passenger);
            message.setJMSReplyTo(replyQueue);

            System.out.println("Sending message...");
            producer.send(requestQueue, message);
            System.out.println(message.getJMSMessageID());

            JMSConsumer consumer = jmsContext.createConsumer(message.getJMSReplyTo());
            TextMessage receivedMessage = (TextMessage) consumer.receive();
            System.out.println(receivedMessage.getJMSCorrelationID());
            System.out.println(receivedMessage.getText());


        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
