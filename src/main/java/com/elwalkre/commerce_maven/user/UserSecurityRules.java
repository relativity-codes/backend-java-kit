package com.elwalkre.commerce_maven.user;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import com.elwalkre.commerce_maven.common.SecurityRules;

@Component
public class UserSecurityRules implements SecurityRules {
    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/**").authenticated();
    }
}
