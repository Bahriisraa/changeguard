package com.changeguard.change;

import com.changeguard.change.client.ApprovalClient;
import com.changeguard.change.client.HealthClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ChangeServiceApplicationTests {

    @MockBean ApprovalClient approval;
    @MockBean HealthClient health;

    @Test
    void contextLoads() {
    }
}
