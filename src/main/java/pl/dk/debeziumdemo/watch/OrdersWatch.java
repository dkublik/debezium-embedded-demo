package pl.dk.debeziumdemo.watch;

import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.OPERATION;
import static java.util.List.copyOf;

@Slf4j
class OrdersWatch {

    private final Configuration debeziumConfiguration;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private EmbeddedEngine engine;

    private final List<Struct> changesCaptured = new ArrayList<>();

    OrdersWatch(Configuration debeziumConfiguration) {
        this.debeziumConfiguration = debeziumConfiguration;
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

    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));
        if (operation != Operation.CREATE) {
            log.error("unknown operation");
            return;
        }
        Struct after = (Struct) sourceRecordValue.get(AFTER);
        changesCaptured.add(after);
    }

    List<Struct> getChangesCaptured() {
        return copyOf(changesCaptured);
    }
}
