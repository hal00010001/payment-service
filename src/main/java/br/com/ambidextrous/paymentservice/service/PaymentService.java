package br.com.ambidextrous.paymentservice.service;

import br.com.ambidextrous.paymentservice.dto.PaymentRequest;
import br.com.ambidextrous.paymentservice.dto.PaymentResponse;
import br.com.ambidextrous.paymentservice.dto.PaymentUpdateRequest;
import br.com.ambidextrous.paymentservice.exceptions.PaymentLimitException;
import br.com.ambidextrous.paymentservice.exceptions.PaymentNotFoundException;
import br.com.ambidextrous.paymentservice.model.Payment;
import br.com.ambidextrous.paymentservice.model.enums.PaymentStatus;
import br.com.ambidextrous.paymentservice.repository.PaymentRepository;
import br.com.ambidextrous.paymentservice.validator.PaymentLimitValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest){
        checkDailyLimit(paymentRequest);
        var payment = Payment.builder()
                .payerId(paymentRequest.getPayerId())
                .paymentSource(paymentRequest.getPaymentSource())
                .amount(paymentRequest.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        var savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", PaymentResponse.class);

        return new ModelMapper().map(savedPayment, PaymentResponse.class);
    }

    @Transactional
    public PaymentResponse updatePayment(Long paymentId, PaymentUpdateRequest updateRequest){
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        var newStatus = updateRequest.getStatus();
        payment.setStatus(newStatus);
        var updatePayment = paymentRepository.save(payment);
        log.info("Payment updated with ID: {}, new status: {}", paymentId, newStatus);
        return new ModelMapper().map(updatePayment, PaymentResponse.class);
    }

    public PaymentResponse getPaymentById(Long paymentId){
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return new ModelMapper().map(payment, PaymentResponse.class);
    }

    public List<PaymentResponse> getPaymentsByPayerId(UUID payerId){
        return paymentRepository.findAllByPayerId(payerId).stream()
                .map(payment -> new ModelMapper().map(payment, PaymentResponse.class))
                .collect(Collectors.toList());
    }

    private void checkDailyLimit(PaymentRequest paymentRequest){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        BigDecimal dailyTotal = paymentRepository.sumPaymentsByPayerIdAndDate(paymentRequest.getPayerId(), startOfDay, endOfDay);

        if (dailyTotal == null){
            dailyTotal = BigDecimal.ZERO;
        }

        BigDecimal novoTotal = dailyTotal.add(paymentRequest.getAmount());

        if (!PaymentLimitValidator.isWithinLimit(novoTotal)){
            throw new PaymentLimitException("Daily payment limit exceeded for source: " + paymentRequest.getPaymentSource());
        }
    }

}
