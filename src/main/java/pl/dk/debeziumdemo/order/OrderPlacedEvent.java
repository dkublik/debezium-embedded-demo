package pl.dk.debeziumdemo.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "order_events")
@Getter
@NoArgsConstructor(access = PRIVATE)
public class OrderPlacedEvent {

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant created = now();

    @Id
    private UUID id = randomUUID();

    private UUID orderId;

    public OrderPlacedEvent(UUID orderId) {
        this.orderId = orderId;
    }
}
