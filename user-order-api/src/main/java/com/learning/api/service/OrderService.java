package com.learning.api.service;

import com.learning.api.dto.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderById(Long id);
    List<OrderDto> getOrdersByUserId(Long userId);
    List<OrderDto> getAllOrders();
    OrderDto updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
}
