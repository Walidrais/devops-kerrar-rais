package fr.uparis.miage.carrental;

import fr.uparis.miage.carrental.client.Customer;
import fr.uparis.miage.carrental.client.CustomerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarRentalApplicationTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CustomerClient customerClient;

    @Test
    void listsSeededCars() throws Exception {
        mvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void rentThenReturnCar() throws Exception {
        when(customerClient.findById(1L))
                .thenReturn(Optional.of(new Customer(1L, "Cerine", "Kerrar", "cerine@example.com")));

        String body = """
                {"customerId": 1, "begin": "2026-12-01", "end": "2026-12-03"}
                """;
        mvc.perform(post("/cars/AA-111-AA/rent").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Cerine Kerrar"))
                .andExpect(jsonPath("$.totalPrice").value(105));

        mvc.perform(post("/cars/AA-111-AA/rent").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());

        mvc.perform(post("/cars/AA-111-AA/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true));
    }

    @Test
    void unknownCustomerIsRejected() throws Exception {
        when(customerClient.findById(99L)).thenReturn(Optional.empty());
        mvc.perform(post("/cars/BB-222-BB/rent").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\": 99, \"begin\": \"2026-12-01\", \"end\": \"2026-12-02\"}"))
                .andExpect(status().isNotFound());
    }
}
