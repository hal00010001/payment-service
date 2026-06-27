package br.com.ambidextrous.paymentservice.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class PaymentLimitValidatorTest {

    @Test
    void shouldBeWithinLimit(){
        BigDecimal amount = new BigDecimal("500.00");
        boolean isWithinLimit = PaymentLimitValidator.isWithinLimit(amount);
        Assertions.assertThat(isWithinLimit).isTrue();
    }

    @Test
    void shouldNotAcceptNullAmount(){
        boolean isWithinLimit = PaymentLimitValidator.isWithinLimit(null);
        Assertions.assertThat(isWithinLimit).isFalse()
    }

}