package com.learning.api.service;

import com.learning.api.dto.OrderDto;
import com.learning.api.dto.OrderEvent;
import com.learning.api.entity.Order;
import com.learning.api.entity.User;
import com.learning.api.exception.ResourceNotFoundException;
import com.learning.api.repository.OrderRepository;
import com.learning.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        // Fetch User and handle potential 404
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderDto.getUserId()));

        Order order = convertToEntity(orderDto, user);
        order.setOrderDate(LocalDateTime.now()); // Capture purchase timestamp

        Order savedOrder = orderRepository.save(order);
        OrderDto savedDto = convertToDto(savedOrder);

        // ============================================================
        // EVENT-DRIVEN MESSAGING: Publish OrderEvent to Kafka
        // ============================================================
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedDto.getId())
                .productName(savedDto.getProductName())
                .quantity(savedDto.getQuantity())
                .userEmail(user.getEmail())
                .timestamp(savedDto.getOrderDate())
                .build();
        
        kafkaProducerService.sendOrderEvent(orderEvent);

        return savedDto;
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Optional: Re-map User if userId has changed
        if (!existingOrder.getUser().getId().equals(orderDto.getUserId())) {
            User newUser = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderDto.getUserId()));
            existingOrder.setUser(newUser);
        }

        existingOrder.setProductName(orderDto.getProductName());
        existingOrder.setQuantity(orderDto.getQuantity());

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToDto(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // Entity <-> DTO Mappings
    private Order convertToEntity(OrderDto dto, User user) {
        return Order.builder()
                .id(dto.getId())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .orderDate(dto.getOrderDate())
                .user(user)
                .build();
    }

    private OrderDto convertToDto(Order entity) {
        return OrderDto.builder()
                .id(entity.getId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .orderDate(entity.getOrderDate())
                .userId(entity.getUser().getId())
                .build();
    }
}
