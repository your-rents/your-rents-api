package com.yourrents.api.tenant;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.yourrents.api.security.PrincipalAccessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    @Autowired
    private TenantService tenantService;
    @Autowired
    private PrincipalAccessor principalAccessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            tenantService.getTenantId();
        } catch (TenantNotFoundException e) {
            log.info("Building Tenant for user {} ({})", principalAccessor.getUsername(), principalAccessor.getSubject());
            tenantService.initTenantForUser();
        }
        filterChain.doFilter(request, response); 
    }
}
