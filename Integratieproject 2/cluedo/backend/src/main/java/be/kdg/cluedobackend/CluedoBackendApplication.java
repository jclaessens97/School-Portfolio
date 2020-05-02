package be.kdg.cluedobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableNeo4jRepositories("be.kdg.cluedobackend.repository")
@EntityScan("be.kdg.cluedobackend.model")
@EnableTransactionManagement
@EnableScheduling
public class CluedoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CluedoBackendApplication.class, args);
    }

}
