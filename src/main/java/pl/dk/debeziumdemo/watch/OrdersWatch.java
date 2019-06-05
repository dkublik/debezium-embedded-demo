package pl.dk.debeziumdemo.watch;

import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import pl.dk.debeziumdemo.email.EmailSender;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.OPERATION;

@Slf4j
class OrdersWatch {

    private final Configuration debeziumConfiguration;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private EmbeddedEngine engine;

    private final EmailSender emailSender;

    OrdersWatch(Configuration debeziumConfiguration, EmailSender emailSender) {
        this.debeziumConfiguration = debeziumConfiguration;
        this.emailSender = emailSender;
    }

    void start() {
        engine = EmbeddedEngine.create()
            .using(debeziumConfiguration)
            .notifying(this::handleEvent)
            .build();
        executor.execute(engine);
    }

    @PostConstruct
    void postConstrutct() {
        start();
    }

    @PreDestroy
    void stop() {
        if (engine != null) {
            engine.stop();
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));
        if (operation != Operation.CREATE) {
            log.error("unknown operation");
            return;
        }
        Struct after = (Struct) sourceRecordValue.get(AFTER);
        UUID orderId = UUID.fromString(after.getString("order_id"));
        emailSender.sendEmail(orderId);
    }
}
