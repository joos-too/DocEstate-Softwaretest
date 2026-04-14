package de.docestate.softwaretest;

import de.docestate.softwaretest.property.persistence.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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
    void propertyReadAndWriteTest() throws Exception {
        String requestBody = """
                {
                  "bezeichnung": "Haus Lengsdorf",
                  "adresse": {
                    "ort": "Bonn",
                    "postleitzahl": "53127",
                    "strasse": "Zur Marterkapelle",
                    "hausnummer": "29"
                  }
                }
                """;

        mockMvc.perform(post("/api/immobilien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bezeichnung").value("Haus Lengsdorf"))
                .andExpect(jsonPath("$.adresse.ort").value("Bonn"));

        mockMvc.perform(get("/api/immobilien"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].bezeichnung").value("Haus Lengsdorf"));
    }

    @Test
    void propertyCanBeReadById() throws Exception {
        String requestBody = """
                {
                  "bezeichnung": "Stadtwohnung",
                  "adresse": {
                    "ort": "Berlin",
                    "postleitzahl": "10117",
                    "strasse": "Friedrichstrasse",
                    "hausnummer": "100"
                  }
                }
                """;

        mockMvc.perform(post("/api/immobilien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/immobilien/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.bezeichnung").value("Stadtwohnung"))
                .andExpect(jsonPath("$.adresse.ort").value("Berlin"));
    }

    @Test
    void propertyEditTest() throws Exception {
        String createBody = """
                {
                  "bezeichnung": "Altbau",
                  "adresse": {
                    "ort": "Hamburg",
                    "postleitzahl": "20095",
                    "strasse": "Musterweg",
                    "hausnummer": "8"
                  }
                }
                """;

        mockMvc.perform(post("/api/immobilien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        String updateBody = """
                {
                  "bezeichnung": "Modernisierter Altbau",
                  "adresse": {
                    "ort": "Hamburg",
                    "postleitzahl": "20095",
                    "strasse": "Neuer Weg",
                    "hausnummer": "9"
                  }
                }
                """;

        mockMvc.perform(put("/api/immobilien/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bezeichnung").value("Modernisierter Altbau"))
                .andExpect(jsonPath("$.adresse.strasse").value("Neuer Weg"));
    }

    @Test
    void propertyPartialEditTest() throws Exception {
        String createBody = """
                {
                  "bezeichnung": "Reihenhaus",
                  "adresse": {
                    "ort": "Köln",
                    "postleitzahl": "50667",
                    "strasse": "Domkloster",
                    "hausnummer": "4"
                  }
                }
                """;

        mockMvc.perform(post("/api/immobilien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        String partialUpdateBody = """
                {
                  "adresse": {
                    "strasse": "Trankgasse"
                  }
                }
                """;

        mockMvc.perform(put("/api/immobilien/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bezeichnung").value("Reihenhaus"))
                .andExpect(jsonPath("$.adresse.ort").value("Köln"))
                .andExpect(jsonPath("$.adresse.postleitzahl").value("50667"))
                .andExpect(jsonPath("$.adresse.strasse").value("Trankgasse"))
                .andExpect(jsonPath("$.adresse.hausnummer").value("4"));
    }

    @Test
    void propertyDeleteTest() throws Exception {
        String createBody = """
                {
                  "bezeichnung": "Einfamilienhaus",
                  "adresse": {
                    "ort": "Leipzig",
                    "postleitzahl": "04109",
                    "strasse": "Ring",
                    "hausnummer": "3"
                  }
                }
                """;

        mockMvc.perform(post("/api/immobilien")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated());

        Long id = propertyRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/immobilien/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/immobilien"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
