package fr.uparis.miage.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerServiceApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void getAndCreateCustomers() throws Exception {
        mvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Cerine"));

        mvc.perform(get("/customers/42")).andExpect(status().isNotFound());

        mvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Benoit\",\"lastName\":\"Charroux\",\"email\":\"b@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }
}
