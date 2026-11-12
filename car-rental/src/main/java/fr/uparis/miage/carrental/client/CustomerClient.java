package fr.uparis.miage.carrental.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Appel REST vers le deuxieme service (customer-service).
 * Dans Kubernetes, l'URL est http://customer-service:8080 (nom du Service).
 */
@Component
public class CustomerClient {

    private final RestClient restClient;

    public CustomerClient(RestClient.Builder builder,
                          @Value("${customer-service.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<Customer> findById(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(Customer.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "customer-service injoignable : " + e.getMessage());
        }
    }
}
