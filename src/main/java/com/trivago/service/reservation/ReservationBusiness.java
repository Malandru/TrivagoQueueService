package com.trivago.service.reservation;

import com.bank.VoucherDocument;
import com.bank.VoucherDocument.Voucher;
import com.hotel.reservations.RequestDocument;
import com.hotel.reservations.RequestDocument.Request;
import com.hotel.reservations.ResponseDocument;
import com.hotel.reservations.service.ReservationServiceStub;
import com.trivago.service.ResponseSender;
import org.apache.xmlbeans.XmlException;

import java.rmi.RemoteException;

public class ReservationBusiness
{
    private ResponseSender responseSender;

    public void setResponseSender(ResponseSender responseSender)
    {
        this.responseSender = responseSender;
    }

    public void makeReservation(String xml)
    {
        try
        {
            System.out.println("Voucher recibido: " + xml);
            System.out.println("Parseando voucher...");
            VoucherDocument document = VoucherDocument.Factory.parse(xml);
            Voucher voucher = document.getVoucher();
            System.out.println("Verificando pago...");
            if(voucher.getPagado())
            {
                System.out.println("Solicitando reservacion...");
                if(requestReservation(voucher))
                    facturar(voucher);
                else
                    responseSender.sendMessage("Solo te cobramos pero no te reservamos jeje");
            }
            else
                responseSender.sendMessage("No se hizo el pago, sorry bro");
        }catch (XmlException e)
        {
            System.out.println("XML Exeption: Error al parsear el voucher");
            responseSender.sendMessage("No se pudo parsear el pago carnal >c");
        }
        catch (RemoteException e)
        {
            System.out.println("Remote Exeption: Error al solicitar la reservacion");
            responseSender.sendMessage("Error al solicitar la reservacion, pero si se te cobro jaja");
        }
        System.out.println("Reservation Business finalizado");
    }

    private boolean requestReservation(Voucher voucher) throws RemoteException
    {
        RequestDocument document = RequestDocument.Factory.newInstance();
        Request request = document.addNewRequest();
        request.setCheckin(voucher.getCheckIn());
        request.setCheckout(voucher.getCheckOut());
        request.setCuarto(voucher.getCuarto());

        String reservationLocation = "http://192.168.43.123:8080/axis2/services/ReservationService/";
        ReservationServiceStub reservationServiceStub = new ReservationServiceStub(reservationLocation);
        ResponseDocument responseDocument = reservationServiceStub.reservationOperation(document);
        return responseDocument.getResponse().getReservado();
    }

    private void facturar(Voucher voucher)
    {
        String factura = "Factura chida\nCuarto: %s\nFecha de ingreso: %s\nFecha de salida: %s";
        factura = String.format(factura, voucher.getCuarto(), voucher.getCheckIn(), voucher.getCheckOut());
        responseSender.sendMessage(factura);
    }
}
