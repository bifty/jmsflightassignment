package com.bharath.jms.flight.reservation;

import com.bharath.jms.flight.reservation.listener.ReservationListener;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReservationSystem {

    public static void main(String[] args) throws NamingException, InterruptedException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        JMSContext jmsContext = cf.createContext("reservationuser", "reservationpassword");

        JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
        consumer.setMessageListener(new ReservationListener());

        Thread.sleep(10000);

    }

}
