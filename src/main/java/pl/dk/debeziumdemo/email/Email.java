package pl.dk.debeziumdemo.email;

import lombok.Value;

import java.util.UUID;

@Value
public class Email {

    String body;

    public boolean isForOrder(UUID orderId) {
        return body.contains(orderId.toString());
    }
}
