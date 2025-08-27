package com.forge_mind.forge_mind.Config;

import com.forge_mind.forge_mind.Service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthService authService;

  public SecurityConfig(AuthService authService) {
    this.authService = authService;
  }

  @Bean
  public JwtAuthFilter jwtAuthFilter() {
    return new JwtAuthFilter(authService);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .cors(cors -> {}) // Enable CORS using WebConfig
        .cors(Customizer.withDefaults())

        .csrf(c -> c.disable())
        .formLogin(f -> f.disable())
        .httpBasic(h -> h.disable())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // this is to
                                                                                                // say that it
                                                                                                // won't create
                                                                                                // sessions and
                                                                                                // use those.
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/signup", "/signup/verify", "/login").permitAll()

            // Secure these endpoints
            .requestMatchers(
                "/signup/all",
                "/signup/interact",
                "/signup/allposts/**",
                "/signup/allcateg",
                "/signup/user-votes")
            .authenticated()

            // Secure everything else by default
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}
