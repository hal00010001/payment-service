package br.com.ambidextrous.paymentservice.service;

import br.com.ambidextrous.paymentservice.dto.PaymentRequest;
import br.com.ambidextrous.paymentservice.exceptions.PaymentLimitException;
import br.com.ambidextrous.paymentservice.model.enums.PaymentSource;
import br.com.ambidextrous.paymentservice.model.enums.PaymentStatus;
import br.com.ambidextrous.paymentservice.repository.PaymentRepository;
import br.com.ambidextrous.paymentservice.validator.PaymentLimitValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldSavePaymentWhenLimitIsNotExceeded(){
        Mockito.when(paymentRepository.sumPaymentsByPayerIdAndDate(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new BigDecimal("200.00"));

        Mockito.when(paymentRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var paymentRequest = PaymentRequest.builder()
                .payerId(UUID.randomUUID())
                .paymentSource(PaymentSource.PIX)
                .amount(new BigDecimal("100.50"))
                .build();

        var createdPayment = paymentService.createPayment(paymentRequest);
//        Assertions.assertThat(createdPayment.getId()).isNotNull(); Id não existe, acusará erro
        Assertions.assertThat(createdPayment.getPayerId()).isEqualTo(paymentRequest.getPayerId());
        Assertions.assertThat(createdPayment.getPaymentSource()).isEqualTo(PaymentSource.PIX);
        Assertions.assertThat(createdPayment.getAmount()).isEqualByComparingTo(paymentRequest.getAmount());
        Assertions.assertThat(createdPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        Mockito.verify(paymentRepository).save(Mockito.any());
    }

    @Test
    void shouldNotSavePaymentWhenDailyLimitIsExceeded(){

        var paymentRequest = PaymentRequest.builder()
                .payerId(UUID.randomUUID())
                .paymentSource(PaymentSource.PIX)
                .amount(new BigDecimal("2000.01"))
                .build();

        Assertions.assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(PaymentLimitException.class)
                .hasMessageContaining("limit exceeded");
    }

    @Test
    void shouldNotSavePaymentWhenValueEqualZero(){

        var paymentRequest = PaymentRequest.builder()
                .payerId(UUID.randomUUID())
                .paymentSource(PaymentSource.PIX)
                .amount(BigDecimal.ZERO)
                .build();

        Assertions.assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(PaymentLimitException.class)
                .hasMessageContaining("greater than zero");

    }

    @Test
    void shouldNotSavePaymentWhenValueIsNegative(){

        var paymentRequest = PaymentRequest.builder()
                .payerId(UUID.randomUUID())
                .paymentSource(PaymentSource.PIX)
                .amount(new BigDecimal("-0.01"))
                .build();

        Assertions.assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(PaymentLimitException.class)
                .hasMessageContaining("greater than zero");
    }

}