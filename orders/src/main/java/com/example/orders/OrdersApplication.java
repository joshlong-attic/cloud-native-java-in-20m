package com.example.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class OrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }

}

@Controller
class CustomerOrderController {

    private final Map<Integer, Collection<Order>> db =
            new ConcurrentHashMap<>();

    CustomerOrderController() {
        for (var customerId = 1; customerId <= 8; customerId++) {
            var listOfOrders = new ArrayList<Order>();
            for (var orderId = 1; orderId <= (Math.random() * 100); orderId++) {
                listOfOrders.add(new Order(customerId, orderId));
            }
            this.db.put(customerId, listOfOrders);
        }
    }

    @MessageMapping("orders.{customerId}")
    Flux<Order> getOrdersFor(@DestinationVariable Integer customerId) {
        var orders = this.db.get(customerId);
        return Flux.fromIterable(orders);
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {

    private Integer customerId;
    private Integer id;
}