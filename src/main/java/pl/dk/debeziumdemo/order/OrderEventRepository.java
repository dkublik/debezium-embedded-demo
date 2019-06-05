package pl.dk.debeziumdemo.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderEventRepository extends JpaRepository<OrderPlacedEvent, UUID> {

    List<OrderPlacedEvent> findByOrderId(UUID orderId);
}
