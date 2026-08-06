package com.smartnotification.config;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import com.smartnotification.security.TokenCapturingAuthorizationRequestResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.smartnotification.repository.UserRepository;
import com.smartnotification.service.JwtService;
import com.smartnotification.security.JwtFilter;
import com.smartnotification.security.OAuth2SuccessHandler;

@Configuration
public class SecurityConfig {

	// change the field list and constructor
	private final JwtFilter jwtFilter;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final ClientRegistrationRepository clientRegistrationRepository; // new

	public SecurityConfig(
	        JwtFilter jwtFilter,
	        UserRepository userRepository,
	        JwtService jwtService,
	        ClientRegistrationRepository clientRegistrationRepository) { // new param
	    this.jwtFilter = jwtFilter;
	    this.userRepository = userRepository;
	    this.jwtService = jwtService;
	    this.clientRegistrationRepository = clientRegistrationRepository; // new
	}

    @Bean
    @Order(1)
    SecurityFilterChain authSecurity(
            HttpSecurity http) throws Exception {
        http
            .securityMatcher("/auth/**")
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain appSecurity(
            HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    (request, response, ex) ->
                        response.sendError(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Unauthorized"
                        )
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/gmail/push",
                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/api/health"
                ).permitAll()
                .requestMatchers(
                    "/gmail"
                ).permitAll()
                .requestMatchers(
                    "/accounts/**"
                ).authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
            	    .authorizationEndpoint(endpoint -> endpoint
            	        .authorizationRequestResolver(
            	            new TokenCapturingAuthorizationRequestResolver(
            	                clientRegistrationRepository, jwtService
            	            )
            	        )
            	    )
            	    .successHandler(
            	        new OAuth2SuccessHandler(
            	            userRepository,
            	            jwtService
            	        )
            	    )
            	)
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}