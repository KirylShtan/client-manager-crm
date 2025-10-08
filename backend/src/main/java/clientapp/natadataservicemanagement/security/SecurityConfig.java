package clientapp.natadataservicemanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/ActualClients/search").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/ActualClients/paginated").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/ActualClients").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_negative_clients/search").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_negative_clients").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_negative_clients/negpaginated").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_positive_clients").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_positive_clients/pospaginated").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/archived_positive_clients/search").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/ActualClients/**").hasRole("ADMIN")
                        .requestMatchers("/api/archived_negative_clients/**").hasRole("ADMIN")
                        .requestMatchers("/api/archived_positive_clients/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("worker")
                .password("{noop}worker123")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin,user);
    }
}
