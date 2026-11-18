package com.hotel.soa.payment.soap;

import com.hotel.soa.payment.entity.Payment;
import com.hotel.soa.payment.service.PaymentService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Endpoint
public class PaymentSoapEndpoint {

    private static final String NAMESPACE_URI = "http://hotel.com/soap/payment";

    private final PaymentService paymentService;

    public PaymentSoapEndpoint(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessPaymentRequest")
    @ResponsePayload
    public ProcessPaymentResponse processPayment(@RequestPayload ProcessPaymentRequest request) {
        ProcessPaymentResponse response = new ProcessPaymentResponse();
        try {
            BigDecimal amount = BigDecimal.valueOf(request.getAmount());
            Payment payment = paymentService.processPayment(
                    request.getBookingId(),
                    request.getCustomerId(),
                    amount,
                    request.getPaymentMode()
            );
            response.setTransactionId(payment.getTransactionId());
            response.setStatus("SUCCESS");
            response.setMessage("Payment processed successfully");
            response.setReceiptNumber(payment.getReceiptNumber());
        } catch (Exception e) {
            response.setTransactionId("NONE");
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
            response.setReceiptNumber("NONE");
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPaymentDetailsRequest")
    @ResponsePayload
    public GetPaymentDetailsResponse getPaymentDetails(@RequestPayload GetPaymentDetailsRequest request) {
        GetPaymentDetailsResponse response = new GetPaymentDetailsResponse();
        try {
            Payment payment = paymentService.getPaymentByTransactionId(request.getTransactionId());
            response.setTransactionId(payment.getTransactionId());
            response.setBookingId(payment.getBooking().getId());
            response.setCustomerId(payment.getCustomer().getId());
            response.setAmount(payment.getAmount().doubleValue());
            response.setPaymentMode(payment.getPaymentMode().name());
            response.setStatus(payment.getStatus().name());
            response.setReceiptNumber(payment.getReceiptNumber());
            response.setPaymentDate(payment.getPaymentDate() != null ?
                    payment.getPaymentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
            response.setMessage("Payment details retrieved successfully");
        } catch (Exception e) {
            response.setTransactionId(request.getTransactionId());
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
