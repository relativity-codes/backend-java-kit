package com.standard.commerce_maven.mailing;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import com.standard.commerce_maven.common.SecurityRules;

@Component
public class MailingSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/api/mailing/**").hasAnyRole("ADMIN", "SUPER_ADMIN");
    }

}
