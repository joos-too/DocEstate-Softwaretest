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
                  "objectType": "EINFAMILIENHAUS",
                  "constructionYear": "1998",
                  "lotSize": 450.5,
                  "livingSpace": 132.75,
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
                .andExpect(jsonPath("$.objectType").value("EINFAMILIENHAUS"))
                .andExpect(jsonPath("$.constructionYear").value("1998"))
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
                  "objectType": "EIGENTUMSWOHNUNG",
                  "constructionYear": "2008",
                  "lotSize": 120.0,
                  "livingSpace": 84.5,
                  "address": {
                    "city": "Berlin",
                    "postalCode": "10117",
                    "street": "Friedrichstraße",
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
                .andExpect(jsonPath("$.objectType").value("EIGENTUMSWOHNUNG"))
                .andExpect(jsonPath("$.constructionYear").value("2008"))
                .andExpect(jsonPath("$.address.city").value("Berlin"));
    }

    @Test
    void propertyEditTest() throws Exception {
        String createBody = """
                {
                  "name": "Altbau",
                  "objectType": "MEHRFAMILIENHAUS",
                  "constructionYear": "1974",
                  "lotSize": 310.0,
                  "livingSpace": 210.0,
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
                  "objectType": "DOPPELHAUSHAELFTE",
                  "constructionYear": "1980",
                  "lotSize": 333.3,
                  "livingSpace": 215.5,
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
                .andExpect(jsonPath("$.objectType").value("DOPPELHAUSHAELFTE"))
                .andExpect(jsonPath("$.constructionYear").value("1980"))
                .andExpect(jsonPath("$.lotSize").value(333.3))
                .andExpect(jsonPath("$.livingSpace").value(215.5))
                .andExpect(jsonPath("$.address.street").value("Neuer Weg"));
    }

    @Test
    void propertyPartialEditTest() throws Exception {
        String createBody = """
                {
                  "name": "Reihenhaus",
                  "objectType": "DOPPELHAUSHAELFTE",
                  "constructionYear": "2001",
                  "lotSize": 240.0,
                  "livingSpace": 145.0,
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
                  "livingSpace": 150.5,
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
                .andExpect(jsonPath("$.objectType").value("DOPPELHAUSHAELFTE"))
                .andExpect(jsonPath("$.constructionYear").value("2001"))
                .andExpect(jsonPath("$.lotSize").value(240.0))
                .andExpect(jsonPath("$.livingSpace").value(150.5))
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
                  "objectType": "EINFAMILIENHAUS",
                  "constructionYear": "1995",
                  "lotSize": 500.0,
                  "livingSpace": 180.0,
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
                  "objectType": "EINFAMILIENHAUS",
                  "constructionYear": "2010",
                  "lotSize": 220.0,
                  "livingSpace": 98.5,
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
