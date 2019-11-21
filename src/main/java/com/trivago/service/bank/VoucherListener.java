package com.trivago.service.bank;

import com.trivago.service.reservation.ReservationBusiness;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class VoucherListener implements MessageListener
{
    private ReservationBusiness reservationBusiness;

    public void setReservationBusiness(ReservationBusiness reservationBusiness)
    {
        this.reservationBusiness = reservationBusiness;
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            TextMessage textMessage = (TextMessage) message;
            reservationBusiness.makeReservation(textMessage.getText());
        }catch (Exception e)
        {
            System.out.println("VoucherListener Exeption: " + e.getMessage());
        }
    }
}
