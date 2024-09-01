package com.quickbite.service;

import com.quickbite.model.Order;
import com.quickbite.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Value("${strip.api.key}")
    private String stripeSecretKey;

    @Override
    public PaymentResponse createPaymentLink(Order order) throws StripeException {

        Stripe.apiKey=stripeSecretKey;
        SessionCreateParams params= SessionCreateParams.builder().addPaymentMethodType(
                SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payments/success/" +order.getId())
                .setCancelUrl("http://localhost:3000/payments/fail")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("NGN")
                        .setUnitAmount((Long) order.getTotalPrice()*100)
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName("Quickchow Food")
                                .build())
                            .build()
                        )
                        .build()
                )
                .build();

        Session session =Session.create(params);

        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(session.getUrl());

        return res;
    }
}
