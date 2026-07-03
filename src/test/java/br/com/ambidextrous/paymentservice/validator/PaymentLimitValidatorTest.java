package br.com.ambidextrous.paymentservice.validator;

import br.com.ambidextrous.paymentservice.exceptions.PaymentLimitException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class PaymentLimitValidatorTest {

    @Test
    void shouldBeWithinLimit(){
//        BigDecimal amount = new BigDecimal("500.00");
//        BigDecimal amount = new BigDecimal("1999.99");
        BigDecimal amount = new BigDecimal("2000.00");
        boolean isWithinLimit = PaymentLimitValidator.isWithinLimit(amount);
        Assertions.assertThat(isWithinLimit).isTrue();
    }

    @Test
    void shouldNotAcceptNullAmount(){
        boolean isWithinLimit = PaymentLimitValidator.isWithinLimit(null);
        Assertions.assertThat(isWithinLimit).isFalse();
    }

    @Test
    void shouldNotAcceptGreaterThanLimit(){
//        BigDecimal amount = new BigDecimal("3000.00");
        BigDecimal amount = new BigDecimal("2000.01");
        boolean isWithinLimit = PaymentLimitValidator.isWithinLimit(amount);
        Assertions.assertThat(isWithinLimit).isFalse();
    }

    @Test
    void shouldNotAcceptZeroAmount(){
        Assertions.assertThatThrownBy(() -> PaymentLimitValidator.isWithinLimit(BigDecimal.ZERO))
                .isInstanceOf(PaymentLimitException.class)
                .hasMessage("Amount must be greater than zero");
    }

    @Test
    void shouldNotAcceptNegativeAmount(){
        BigDecimal amount = new BigDecimal("-5.00");
        Assertions.assertThatThrownBy(() -> PaymentLimitValidator.isWithinLimit(amount))
                .isInstanceOf(PaymentLimitException.class)
                .hasMessage("Amount must be greater than zero");
    }

}