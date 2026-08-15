package com.meetingintel.meeting_intel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.ai.openai.api-key=test",
        "groq.api.key=test",
        "groq.api.url=test",
        "groq.model=test",
        "spring.mail.host=smtp.gmail.com",
        "spring.mail.username=test@gmail.com",
        "spring.mail.password=test"
})
class MeetingIntelApplicationTests {

    @Test
    void contextLoads() {
    }
}