package de.docestate.softwaretest;

import de.docestate.softwaretest.repos.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SoftwaretestApplicationTests {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void unauthenticatedRequestsAreRejected() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void propertyReadAndWriteTest() throws Exception {
        String requestBody = """
                {
                  "name": "Haus Lengsdorf",
                  "address": {
                    "city": "Bonn",
                    "postalCode": "53127",
                    "street": "Zur Marterkapelle",
                    "houseNumber": "29"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Haus Lengsdorf"))
                .andExpect(jsonPath("$.address.city").value("Bonn"));

        mockMvc.perform(get("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Haus Lengsdorf"));
    }

    @Test
    void propertyCanBeReadById() throws Exception {
        String requestBody = """
                {
                  "name": "Stadtwohnung",
                  "address": {
                    "city": "Berlin",
                    "postalCode": "10117",
                    "street": "Friedrichstrasse",
                    "houseNumber": "100"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/properties/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Stadtwohnung"))
                .andExpect(jsonPath("$.address.city").value("Berlin"));
    }

    @Test
    void propertyEditTest() throws Exception {
        String createBody = """
                {
                  "name": "Altbau",
                  "address": {
                    "city": "Hamburg",
                    "postalCode": "20095",
                    "street": "Musterweg",
                    "houseNumber": "8"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        String updateBody = """
                {
                  "name": "Modernisierter Altbau",
                  "address": {
                    "city": "Hamburg",
                    "postalCode": "20095",
                    "street": "Neuer Weg",
                    "houseNumber": "9"
                  }
                }
                """;

        mockMvc.perform(put("/api/properties/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Modernisierter Altbau"))
                .andExpect(jsonPath("$.address.street").value("Neuer Weg"));
    }

    @Test
    void propertyPartialEditTest() throws Exception {
        String createBody = """
                {
                  "name": "Reihenhaus",
                  "address": {
                    "city": "Koeln",
                    "postalCode": "50667",
                    "street": "Domkloster",
                    "houseNumber": "4"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        String partialUpdateBody = """
                {
                  "address": {
                    "street": "Trankgasse"
                  }
                }
                """;

        mockMvc.perform(put("/api/properties/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Reihenhaus"))
                .andExpect(jsonPath("$.address.city").value("Koeln"))
                .andExpect(jsonPath("$.address.postalCode").value("50667"))
                .andExpect(jsonPath("$.address.street").value("Trankgasse"))
                .andExpect(jsonPath("$.address.houseNumber").value("4"));
    }

    @Test
    void propertyDeleteTest() throws Exception {
        String createBody = """
                {
                  "name": "Einfamilienhaus",
                  "address": {
                    "city": "Leipzig",
                    "postalCode": "04109",
                    "street": "Ring",
                    "houseNumber": "3"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/properties/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createFailsForInvalidPostalCode() throws Exception {
        String requestBody = """
                {
                  "name": "Testobjekt",
                  "address": {
                    "city": "Bonn",
                    "postalCode": "5312",
                    "street": "Musterweg",
                    "houseNumber": "29"
                  }
                }
                """;

        mockMvc.perform(post("/api/properties")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Postal code must contain exactly 5 digits."))
                .andExpect(jsonPath("$.fieldErrors['address.postalCode']")
                        .value("Postal code must contain exactly 5 digits."));
    }
}
