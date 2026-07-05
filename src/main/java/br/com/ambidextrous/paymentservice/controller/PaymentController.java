package br.com.ambidextrous.paymentservice.controller;

import br.com.ambidextrous.paymentservice.dto.PaymentRequest;
import br.com.ambidextrous.paymentservice.dto.PaymentResponse;
import br.com.ambidextrous.paymentservice.dto.PaymentUpdateRequest;
import br.com.ambidextrous.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest paymentRequest){
        return paymentService.createPayment(paymentRequest);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable Long paymentId){
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments(){
        return paymentService.getAllPayments();
    }

    @GetMapping("/payer/{payerId}")
    public List<PaymentResponse> getPaymentsByPayer(@PathVariable UUID payerId){
        return paymentService.getPaymentsByPayerId(payerId);
    }

    @PutMapping("/{paymentId}")
    public PaymentResponse updatePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentUpdateRequest updateRequest
    ){
        return paymentService.updatePayment(paymentId, updateRequest);
    }

}
