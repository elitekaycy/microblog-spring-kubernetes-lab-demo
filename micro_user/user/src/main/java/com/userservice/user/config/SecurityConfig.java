package com.userservice.user.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${jwt.key}")
  public String jwtKey;

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails userDetails = User.withUsername("kaycy")
        .password("{noop}password")
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(userDetails);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((auth) -> {
          auth.requestMatchers("/auth/**").permitAll();
          auth.anyRequest().authenticated();
        })
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> {
          ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
              .accessDeniedHandler(new BearerTokenAccessDeniedHandler());
        })
        .oauth2ResourceServer(
            oauth2ResourceServerCustomizer -> oauth2ResourceServerCustomizer.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  public AuthenticationManager authManager(UserDetailsService userDetailsService) {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    return new ProviderManager(authProvider);
  }

  @Bean
  JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] bytes = jwtKey.getBytes();
    SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");
    return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MacAlgorithm.HS512).build();
  }
}
