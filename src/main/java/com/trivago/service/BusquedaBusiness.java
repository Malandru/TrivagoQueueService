package com.trivago.service;

import com.bank.PaymentDocument;
import com.bank.PaymentDocument.Payment;
import com.hotel.rooms.RequestDocument;
import com.hotel.rooms.RequestDocument.Request;
import com.hotel.rooms.ResponseDocument;
import com.hotel.rooms.Room;
import com.hotel.rooms.service.RoomsServiceStub;
import com.trivago.BusquedaDocument;
import com.trivago.BusquedaDocument.Busqueda;
import com.trivago.service.bank.PaymentSender;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import java.rmi.RemoteException;

public class BusquedaBusiness
{
    private ResponseSender responseSender;
    private PaymentSender paymentSender;

    public void setResponseSender(ResponseSender responseSender)
    {
        this.responseSender = responseSender;
    }

    public void setPaymentSender(PaymentSender paymentSender)
    {
        this.paymentSender = paymentSender;
    }

    public void searchHotel(String xml)
    {
        try
        {
            BusquedaDocument document = BusquedaDocument.Factory.parse(xml);
            Busqueda busqueda = document.getBusqueda();

            Room room = requestRoom(busqueda.getCheckIn(), busqueda.getCheckOut());
            if(room == null)
            {
                System.out.println("No se encontraron cuartos disponibles :/");
                responseSender.sendMessage("nel perro");
            }
            else
            {
                Payment payment = buildPayment();
                payment.setOrigen(busqueda.getTarjeta());
                payment.setDestino("12345");
                payment.setCantidad(room.getPrecioNoche());

                /**
                 * Use al charlotte como auxiliar
                 */
                payment.setCheckIn(busqueda.getCheckIn());
                payment.setCheckOut(busqueda.getCheckOut());
                payment.setCuarto(room.getNumeroCuarto());

                /**
                 * Usado para parsear en el banco
                 */
                XmlOptions options = new XmlOptions();
                options.put(XmlOptions.SAVE_PRETTY_PRINT);
                options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
                options.setSaveOuter();

                paymentSender.sendMessage(payment.xmlText(options));
            }
        }
        catch (XmlException e)
        {
            System.out.println("XML Exeption: No se pudo parsear la busqueda");
            responseSender.sendMessage("Error al parsear la busquedaRequest :c");
        }
        catch (RemoteException e)
        {
            System.out.println("Remote Exeption: No se pudo conectar con RoomService");
            responseSender.sendMessage("Error al solicitar los cuartos disponibles");
        }
    }

    private Room requestRoom(String checkIn, String checkOut) throws RemoteException
    {
        RequestDocument document = RequestDocument.Factory.newInstance();
        Request request = document.addNewRequest();
        request.setCheckin(checkIn);
        request.setCheckout(checkOut);

        String roomsLocation = "http://192.168.43.123:8080/axis2/services/RoomsService/";
        RoomsServiceStub roomsServiceStub = new RoomsServiceStub(roomsLocation);
        ResponseDocument responseDocument = roomsServiceStub.roomsOperation(document);
        return responseDocument.getResponse().getCuarto();
    }

    private Payment buildPayment()
    {
        return  PaymentDocument.Factory.
                newInstance().addNewPayment();
    }
}
