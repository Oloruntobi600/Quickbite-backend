package com.quickbite.controller;

import com.quickbite.model.CartItem;
import com.quickbite.model.Order;
import com.quickbite.model.User;
import com.quickbite.request.AddCartItemRequest;
import com.quickbite.request.OrderRequest;
import com.quickbite.response.PaymentResponse;
import com.quickbite.service.OrderService;
import com.quickbite.service.PaymentService;
import com.quickbite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody OrderRequest req,
                                                       @RequestHeader("Authorization") String jwt) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        Order order =orderService.createOrder(req,user);
        PaymentResponse res=paymentService.createPaymentLink(order);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getOrderHistory(
                                             @RequestHeader("Authorization") String jwt) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        List<Order> orders =orderService.getUsersOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/{orderStatus}")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable String orderStatus,
            @RequestHeader("Authorization") String jwt) throws Exception {
        // Validate and handle the orderStatus
        if (!orderStatus.equals("OUT_FOR_DELIVERY") && !orderStatus.equals("DELIVERED") &&
                !orderStatus.equals("COMPLETED") && !orderStatus.equals("PENDING")) {
            throw new Exception("Invalid order status");
        }
        Order updatedOrder = orderService.updateOrder(orderId, orderStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @GetMapping("/admin/order/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getRestaurantsOrder(
            @PathVariable Long restaurantId,
            @RequestParam(value = "order_status", required = false) String orderStatus,
            @RequestHeader("Authorization") String jwt) throws Exception {
        List<Order> orders = orderService.getRestaurantsOrder(restaurantId, orderStatus);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.findOrderById(orderId);

        if (!order.getCustomer().getId().equals(user.getId())) {
            return new ResponseEntity<>("Unauthorized action", HttpStatus.FORBIDDEN);
        }

        // Only allow cancellation if the order is still pending
        if (!order.getOrderStatus().equals("PENDING")) {
            return new ResponseEntity<>("Order cannot be canceled", HttpStatus.BAD_REQUEST);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<>("Order canceled successfully", HttpStatus.OK);
    }

    @DeleteMapping("/admin/orders/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        // Assuming you have a method to check if the user is an admin
        User user = userService.findUserByJwtToken(jwt);
        if (!user.isAdmin()) {
            return new ResponseEntity<>("Unauthorized action", HttpStatus.FORBIDDEN);
        }

        Order order = orderService.findOrderById(orderId);

        // Only allow deletion if the order is completed
        if (!order.getOrderStatus().equals("COMPLETED")) {
            return new ResponseEntity<>("Order cannot be deleted", HttpStatus.BAD_REQUEST);
        }

        orderService.deleteOrder(orderId);
        return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
    }

}
