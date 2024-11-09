package com.yourrents.api.tenant;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yourrents.api.security.PrincipalAccessor;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PrincipalAccessor principalAccessor;

    public String getTenantId() {
        String accountId = principalAccessor.getSubject();
        return tenantRepository.getUserTenant(UUID.fromString(accountId)).map(Tenant::uuid)
                .map(UUID::toString).orElseThrow(() -> new TenantNotFoundException("Tenant not found for account " + accountId));
    }

    @Transactional
    public void initTenantForUser() {
        String userName = principalAccessor.getName();
        Tenant tenant = tenantRepository.add(new Tenant(null, userName + " Tenant"));
        String accountId = principalAccessor.getSubject();
        tenantRepository.linkTenantToUser(tenant.uuid(), UUID.fromString(accountId));
        tenantRepository.initTenantSchema(tenant.uuid());
    }
}
