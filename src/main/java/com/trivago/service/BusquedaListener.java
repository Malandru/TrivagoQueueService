package com.trivago.service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class BusquedaListener implements MessageListener
{
    private BusquedaBusiness busquedaBusiness;
    public void setBusquedaBusiness(BusquedaBusiness busquedaBusiness)
    {
        this.busquedaBusiness = busquedaBusiness;
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            TextMessage textMessage = (TextMessage) message;
            busquedaBusiness.searchHotel(textMessage.getText());
        }catch (Exception e)
        {
            System.out.println("BusquedaListener Exeption: " + e.getMessage());
        }
    }
}
