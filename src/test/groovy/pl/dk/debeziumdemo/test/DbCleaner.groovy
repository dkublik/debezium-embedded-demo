package pl.dk.debeziumdemo.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class DbCleaner {

    @Autowired
    private final EntityManager entityManager


    @Transactional
    void clearTables() {
        entityManager.createNativeQuery("TRUNCATE TABLE orders CASCADE").executeUpdate()
        entityManager.createNativeQuery("TRUNCATE TABLE order_events CASCADE").executeUpdate()
    }
}
