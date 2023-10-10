package com.thetrickuser.jms.fm.reservation.listener;

import com.thetrickuser.jms.fm.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReservationSystemListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("Message received");
        ObjectMessage objectMessage = (ObjectMessage) message;

        try (ActiveMQXAConnectionFactory cf = new ActiveMQXAConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            InitialContext initialContext = new InitialContext();
            Passenger passenger = (Passenger) objectMessage.getObject();
            System.out.println("Passenger details: " + passenger.toString());

            JMSProducer replyProducer = jmsContext.createProducer();
            TextMessage replyMessage = jmsContext.createTextMessage("Passenger successfully checked in");
            replyMessage.setJMSCorrelationID(message.getJMSMessageID());
            replyProducer.send(message.getJMSReplyTo(), replyMessage);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
