package com.bharath.jms.flight.reservation.listener;

import com.bharath.jms.flight.model.Passenger;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSContext;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ReservationListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;


        try (
                ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
                JMSContext jmsContext = cf.createContext();
        ) {
            printRequest(objectMessage);

            InitialContext initialContext = new InitialContext();
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
            MapMessage replyMessage = jmsContext.createMapMessage();
            Passenger passenger = (Passenger) objectMessage.getObject();


            if (passenger.getId() != null) {
                replyMessage.setBoolean("hasReservation", true);
            } else {
                System.out.println("invalid passenger" + passenger);
                //replyMessage.setJMSCorrelationID(message.getJMSMessageID());
            }

            JMSProducer producer = jmsContext.createProducer();
            producer.send(replyQueue, replyMessage);

            printReply(replyMessage);


        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static void printReply(MapMessage mapMessage) throws JMSException {
        System.out.println("Received request:");
        System.out.println("\tMessageId:" + mapMessage.getJMSMessageID());
        System.out.println("\tCorrel Id:" + mapMessage.getJMSCorrelationID());
        System.out.println("\tReply to:" + mapMessage.getJMSReplyTo());
        System.out.println("\tContent:" + mapMessage.toString());
    }

    private static void printRequest(ObjectMessage objectMessage) throws JMSException {
        System.out.println("Send reply:");
        System.out.println("\tMessageId:" + objectMessage.getJMSMessageID());
        System.out.println("\tCorrel Id:" + objectMessage.getJMSCorrelationID());
        System.out.println("\tReply to:" + objectMessage.getJMSReplyTo());
        System.out.println("\tContent:" + objectMessage.getObject().toString());
    }
}
