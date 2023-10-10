package com.thetrickuser.jms.hm.clinicals;

import com.thetrickuser.jms.hm.eligibilitycheck.listeners.EligibilityCheckListener;
import com.thetrickuser.jms.hm.model.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClinicalsApp {

    public static void main(String[] args) throws NamingException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQXAConnectionFactory cf = new ActiveMQXAConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            JMSProducer producer = jmsContext.createProducer();
            ObjectMessage objectMessage = jmsContext.createObjectMessage();

            Patient patient = new Patient();
            patient.setId(123);
            patient.setName("Adhyan");
            patient.setCopay(13000d);
            patient.setAmountToBePaid(94000d);
            patient.setInsuranceProvider("Medibuddy");

            objectMessage.setObject(patient);

            producer.send(requestQueue, objectMessage);

            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
            MapMessage replyMessage = (MapMessage) consumer.receive(30000);
            System.out.println("Patient " + patient.getName() + " is " + replyMessage.getBoolean("eligible") + " for insurance.");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
