package pl.dk.debeziumdemo.watch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class WatchConfig {

    @Bean
    OrdersWatch ordersWatch(OrdersDbInfoProvider ordersDbInfoProvider) {
        return new OrdersWatch(debeziumConfiguration(ordersDbInfoProvider));
    }

    io.debezium.config.Configuration debeziumConfiguration(OrdersDbInfoProvider ordersDbInfoProvider) {
        return io.debezium.config.Configuration.create()
            .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore")
            .with("offset.flush.interval.ms", 60000)
            .with("name", "orders-postgres-connector")
            .with("database.server.name", "orders")
            .with("database.hostname", "localhost")
            .with("database.port", ordersDbInfoProvider.getPort())
            .with("database.user", "postgres")
            .with("database.password", "postgres")
            .with("database.dbname", "demo")
            .with("table.whitelist", "public.orders")
           // .with("snapshot.mode", "never")
            .build();
    }
}
