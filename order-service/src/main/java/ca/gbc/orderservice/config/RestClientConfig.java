package ca.gbc.orderservice.config;

import ca.gbc.orderservice.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public InventoryClient inventoryClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl(inventoryServiceUrl)
            .build();

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyClient = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyClient.createClient(InventoryClient.class);
    }

}
