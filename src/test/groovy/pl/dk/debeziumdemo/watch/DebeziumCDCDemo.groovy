package pl.dk.debeziumdemo.watch

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.dk.debeziumdemo.DebeziumDemoApp
import spock.lang.Specification

import static java.util.UUID.randomUUID
import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await

@ContextConfiguration(classes = [DebeziumDemoApp])
@SpringBootTest
@ActiveProfiles('test')
@Slf4j
class DebeziumCDCDemo extends Specification {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate

    @Autowired
    OrdersWatch ordersWatch

    static final UUID ORDER_ID = randomUUID()

    def "should capture changes"() {
        when:
        jdbcTemplate.update('INSERT INTO orders(id, description) VALUES (:id, :description)',
                [
                        id: ORDER_ID,
                        description: 'some description'
                ]
        )

        then:
        await().atMost(5, SECONDS).until {
            ordersWatch.changesCaptured.size() > 0
        }
        ordersWatch.changesCaptured[0].getString('id') == ORDER_ID.toString()
        ordersWatch.changesCaptured[0].getString('description') == 'some description'
    }
}
