package com.quickbite.service;

import com.quickbite.model.Order;
import com.quickbite.response.PaymentResponse;
import com.stripe.exception.StripeException;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentService {

    public PaymentResponse createPaymentLink(Order order) throws StripeException;
}
