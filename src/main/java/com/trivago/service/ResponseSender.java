package com.trivago.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class ResponseSender extends JmsTemplate
{
    public void sendMessage(final String message)
    {
        this.send("queue/B", new MessageCreator()
        {
            public Message createMessage(final Session session) throws JMSException
            {
                return session.createTextMessage(message);
            }
        });
    }
}
