package fr.uparis.miage.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Deuxieme service : gestion des clients.
 * GET  /customers        liste
 * GET  /customers/{id}   un client (appele par car-rental avant chaque location)
 * POST /customers        ajouter un client
 */
@SpringBootApplication
@RestController
public class CustomerServiceApplication {

    public record Customer(Long id, @NotBlank String firstName, @NotBlank String lastName, @Email String email) {
    }

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public CustomerServiceApplication() {
        add(new Customer(null, "Cerine", "Kerrar", "cerine.kerrar@etu.u-paris.fr"));
        add(new Customer(null, "Walid", "Rais", "walid.rais@etu.u-paris.fr"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @GetMapping("/customers")
    public Collection<Customer> list() {
        return customers.values();
    }

    @GetMapping("/customers/{id}")
    public Customer get(@PathVariable Long id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client inconnu : " + id);
        }
        return customer;
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer add(@Valid @RequestBody Customer customer) {
        Customer saved = new Customer(nextId.getAndIncrement(), customer.firstName(), customer.lastName(), customer.email());
        customers.put(saved.id(), saved);
        return saved;
    }
}
