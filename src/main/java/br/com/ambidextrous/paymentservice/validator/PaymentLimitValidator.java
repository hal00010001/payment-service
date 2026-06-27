package br.com.ambidextrous.paymentservice.validator;

import java.math.BigDecimal;

public class PaymentLimitValidator {

    private static final BigDecimal MAX_LIMIT = new BigDecimal("2000.00");

    public static boolean isWithinLimit(BigDecimal amount){
        if (amount == null) return false;
        return amount.compareTo(MAX_LIMIT) <= 0;
    }

}
