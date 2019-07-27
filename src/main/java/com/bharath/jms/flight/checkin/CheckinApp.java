package com.bharath.jms.flight.checkin;


import com.bharath.jms.flight.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.management.Query;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CheckinApp {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (
                ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext = cf.createContext("flightuser", "flightpassword");

        ) {
            JMSProducer producer = jmsContext.createProducer();
            ObjectMessage objectMessage = jmsContext.createObjectMessage();

            Passenger passenger1 = getPassenger("1");
            Passenger passenger2 = getPassenger("2");
            Passenger invalidPassenger = getPassenger(null);

            objectMessage.setObject(passenger1);
            producer.send(requestQueue, objectMessage);
            printRequest(objectMessage);

            objectMessage.setObject(passenger2);
            producer.send(requestQueue, objectMessage);
            printRequest(objectMessage);


            objectMessage.setObject(invalidPassenger);
            producer.send(requestQueue, objectMessage);
            printRequest(objectMessage);

            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);

            for (int i = 0; i <= 2; i++) {
                MapMessage replyMessage = (MapMessage) consumer.receive(30000);

                System.out.println("CorrelationId:" + replyMessage.getJMSMessageID());
                System.out.println("\tPassenger has valid reservation:" + replyMessage.getBoolean("hasReservation"));

            }


        }


    }

    private static void printRequest(ObjectMessage objectMessage) throws JMSException {
        System.out.println("Send request:");
        System.out.println("\tMessageId:" + objectMessage.getJMSMessageID());
        System.out.println("\tCorrel Id:" + objectMessage.getJMSCorrelationID());
        System.out.println("\tReply to:" + objectMessage.getJMSReplyTo());
        System.out.println("\tContent:" + objectMessage.getObject().toString());
    }

    private static Passenger getPassenger(String id) {
        Passenger passenger = new Passenger();
        passenger.setFirstName("Peter");
        passenger.setEmail("me@web.de");
        passenger.setId(id);
        passenger.setLastName("August");
        passenger.setPhone("0911-1234");

        return passenger;
    }

}
