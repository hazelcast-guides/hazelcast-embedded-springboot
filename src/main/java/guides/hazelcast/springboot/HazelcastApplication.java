package guides.hazelcast.springboot;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HazelcastApplication {
    public static void main(String[] args) {
        SpringApplication.run(HazelcastApplication.class, args);
    }

    @Bean
    public IMap<String, String> map(HazelcastInstance instance) {
        return instance.getMap("map");
    }
}
