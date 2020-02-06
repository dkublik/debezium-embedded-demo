package pl.dk.debeziumdemo.test

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import pl.dk.debeziumdemo.watch.OrdersDbInfoProvider

import javax.sql.DataSource

@Configuration
class TestContainersConfig {

    private static final String POSTGRES_DOCKER_IAMGE = 'debezium/postgres:10.0'

    private static final int POSTGRES_EXPOSED_PORT = 5432

    private GenericContainer postgresContainer

    TestContainersConfig() {
        postgresContainer = new GenericContainer(POSTGRES_DOCKER_IAMGE)
                .withExposedPorts(POSTGRES_EXPOSED_PORT)
                .withEnv([
                        'POSTGRES_DB': 'demo',
                        'POSTGRES_USER': 'postgres',
                        'POSTGRES_PASSWORD': 'postgres'
                ])
        postgresContainer.start()
    }

    @Bean
    OrdersDbInfoProvider ordersDbInfoProvider() {
        final port = postgresContainer.firstMappedPort
        return new OrdersDbInfoProvider() {
            @Override
            int getPort() {
                return port
            }
        }
    }

    @Bean
    DataSource dataSource(DataSourceProperties dataSourceProperties, OrdersDbInfoProvider ordersDbInfoProvider) {
        dataSourceProperties.url = replacePort(dataSourceProperties.url, ordersDbInfoProvider.port)
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build()
    }

    private String replacePort(String url, int newPort) {
        return url.replaceFirst(':\\d+', ":$newPort")
    }
}
