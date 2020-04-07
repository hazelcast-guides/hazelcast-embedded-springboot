package guides.hazelcast.springboot;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandControllerIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    public void testPutRequest(){
        //when
        WebTestClient.ResponseSpec responseSpec = makeRequest("/put?key={key}&value={value}", "key1", "value1");

        //then
        responseSpec.expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.value").isEqualTo("value1");
    }

    @Test
    public void testGetRequest(){
        //given
        makeRequest("/put?key={key}&value={value}", "key1", "value1");

        //when
        WebTestClient.ResponseSpec responseSpec = makeRequest("/get?key={key}", "key1");

        //then
        responseSpec.expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.value").isEqualTo("value1");
    }

    private WebTestClient.ResponseSpec makeRequest(String uri, Object... parameters) {
        return webTestClient
                .get()
                .uri(uri, parameters)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange();
    }

    @Test
    public void testHazelcastCluster(){
        //given
        Hazelcast.newHazelcastInstance();

        //then
        assertEquals(2, hazelcastInstance.getCluster().getMembers().size());
    }
}
