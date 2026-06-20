package com.learning.order.service;

import com.learning.order.dto.OrderDto;
import com.learning.order.entity.Order;
import com.learning.order.exception.ResourceNotFoundException;
import com.learning.order.repository.OrderRepository;
import com.learning.order.saga.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String TOPIC = "order-events";

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        log.info(">>> [SAGA ORCHESTRATOR] Initiating SAGA Order Creation...");

        // Create Order in PENDING state
        Order order = convertToEntity(orderDto);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);
        OrderDto savedDto = convertToDto(savedOrder);

        // Publish event to trigger user validation
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedDto.getId())
                .userId(savedDto.getUserId())
                .productName(savedDto.getProductName())
                .quantity(savedDto.getQuantity())
                .build();

        log.info(">>> [SAGA ORCHESTRATOR] Emitting OrderCreatedEvent to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, savedOrder.getId().toString(), event);

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

        existingOrder.setProductName(orderDto.getProductName());
        existingOrder.setQuantity(orderDto.getQuantity());
        existingOrder.setUserId(orderDto.getUserId());

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

    private Order convertToEntity(OrderDto dto) {
        return Order.builder()
                .id(dto.getId())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .orderDate(dto.getOrderDate())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .build();
    }

    private OrderDto convertToDto(Order entity) {
        return OrderDto.builder()
                .id(entity.getId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .orderDate(entity.getOrderDate())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .build();
    }
}
