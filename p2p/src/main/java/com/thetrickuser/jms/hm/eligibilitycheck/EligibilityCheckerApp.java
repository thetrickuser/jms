package com.thetrickuser.jms.hm.eligibilitycheck;

import com.thetrickuser.jms.hm.eligibilitycheck.listeners.EligibilityCheckListener;
import com.thetrickuser.jms.hm.model.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckerApp {

    public static void main(String[] args) throws NamingException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQXAConnectionFactory cf = new ActiveMQXAConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            consumer.setMessageListener(new EligibilityCheckListener());

            Thread.sleep(5000);
        }
    }
}
