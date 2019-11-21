package com.trivago.service.bank;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class PaymentSender extends JmsTemplate
{
    public void sendMessage(final String message)
    {
        this.send("queue/paymentQueue", new MessageCreator()
        {
            public Message createMessage(final Session session) throws JMSException
            {
                return session.createTextMessage(message);
            }
        });
    }
}
