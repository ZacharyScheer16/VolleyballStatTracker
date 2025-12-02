package com.zacharyscheer.volleyballstattracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharyscheer.volleyballstattracker.models.Player;
import com.zacharyscheer.volleyballstattracker.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles; // <-- NEW IMPORT
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 💡 We activate the 'test' profile to ensure application-test.properties is loaded
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // <-- THIS FORCES H2 CONFIGURATION
public class PlayerControllerTest {

    private static final String API_PLAYER_URL = "/api/player";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Clears the database before each test to ensure tests are isolated.
     */
    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
    }

    // --- TEST 1: POST (Create Player) ---
    @Test
    void createPlayer_shouldReturnCreatedPlayer() throws Exception {
        // Arrange: PlayerRequest JSON body
        String playerJsonBody = """
            {
              "name": "Justus Clarke",
              "number": 8,
              "position": "Outside Hitter"
            }
        """;

        // Act & Assert
        mockMvc.perform(post(API_PLAYER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJsonBody))
                .andExpect(status().isCreated()) // Expect 201 OK
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Justus Clarke"))
                .andExpect(jsonPath("$.number").value(8));
    }

    // --- TEST 2: GET (Read All Players) ---
//    @Test
//    void getAllPlayers_shouldReturnListOfPlayers() throws Exception {
//        // Arrange: Insert two players directly into the database using the Builder pattern
//        playerRepository.save(Player.builder().name("Justus Clarke").number(8).position("OH").build());
//        playerRepository.save(Player.builder().name("Karch Kiraly").number(1).position("OH").build());
//
//        // Act & Assert
//        mockMvc.perform(get(API_PLAYER_URL)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$", hasSize(2)))
//
//                // FIX: Use containsInAnyOrder to check for both names regardless of order
//                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Justus Clarke", "Karch Kiraly")));
//    }
//
//    // --- TEST 3: GET (Read Player by Number) ---
//    @Test
//    void getPlayerByNumber_shouldReturnPlayer() throws Exception {
//        // Arrange
//        playerRepository.save(Player.builder().name("Justus Clarke").number(8).position("OH").build());
//
//        // Act & Assert
//        mockMvc.perform(get(API_PLAYER_URL + "/number/{number}", 8)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Justus Clarke"));
//    }

    @Test
    void getPlayerByNumber_shouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get(API_PLAYER_URL + "/number/{number}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
