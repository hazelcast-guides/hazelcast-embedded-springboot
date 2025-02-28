package guides.hazelcast.springboot;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandControllerIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    public void testPutRequest(){
        //when
        WebTestClient.ResponseSpec responseSpec = webTestClient
                .post()
                .uri("/put?key={key}&value={value}", "key1", "value1")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange();

        //then
        responseSpec.expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.value").isEqualTo("value1");
    }

    @Test
    public void testGetRequest(){
        //given
        webTestClient
                .post()
                .uri("/put?key={key}&value={value}", "key1", "value1")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange();

        //when
        WebTestClient.ResponseSpec responseSpec = webTestClient
                .get()
                .uri("/get?key={key}", "key1")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange();

        //then
        responseSpec.expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.value").isEqualTo("value1");
    }

    @Test
    public void testHazelcastCluster() {
        Config config = Config.load();
        config.setProperty("hazelcast.logging.type", "log4j2");

        //given
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        //then
        try {
            await()
                    .atMost(Duration.ofMinutes(2))
                    .until(() -> hazelcastInstance.getCluster().getMembers().size() == 2);
        } finally {
            hz.shutdown();
        }
    }
}
