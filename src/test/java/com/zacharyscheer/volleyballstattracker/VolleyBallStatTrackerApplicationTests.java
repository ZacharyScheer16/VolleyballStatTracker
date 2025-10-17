package com.zacharyscheer.volleyballstattracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // <-- NEW IMPORT

@SpringBootTest
@ActiveProfiles("test") // <-- NEW ANNOTATION
class VolleyBallStatTrackerApplicationTests {
    // ... test methods
}