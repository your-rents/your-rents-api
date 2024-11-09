package com.yourrents.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
public class TenantServiceTest {

    @MockBean
    private PrincipalAccessor principalAccessor;

    @Autowired
    private TenantService tenantService;

    @Test
    void getTenantId() {
        Mockito.when(principalAccessor.getSubject()).thenReturn("00000000-0000-0000-0000-000000000002");
        assertThat(tenantService.getTenantId()).isEqualTo("00000000-0000-0000-0000-000000000001");
    }

}
