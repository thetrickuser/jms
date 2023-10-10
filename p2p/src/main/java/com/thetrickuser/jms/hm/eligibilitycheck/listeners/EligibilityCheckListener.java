package com.thetrickuser.jms.hm.eligibilitycheck.listeners;

import com.thetrickuser.jms.hm.model.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;

        try (ActiveMQXAConnectionFactory cf = new ActiveMQXAConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            InitialContext initialContext = new InitialContext();
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
            MapMessage replyMessage = jmsContext.createMapMessage();
            Patient patient = (Patient) objectMessage.getObject();

            if (patient.getInsuranceProvider().equalsIgnoreCase("Medibuddy")) {
                if (patient.getCopay() < 20000d && patient.getAmountToBePaid() < 250000d) {
                    replyMessage.setBoolean("eligible", true);
                } else {
                    replyMessage.setBoolean("eligible", false);
                }
            }

            JMSProducer replyProducer = jmsContext.createProducer();
            replyProducer.send(replyQueue, replyMessage);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
