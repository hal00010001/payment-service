package br.com.ambidextrous.paymentservice.exceptions;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message){
        super(message);
    }

}
