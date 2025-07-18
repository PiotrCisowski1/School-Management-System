package com.cisowski.schoolmanagement.common.security;

import com.cisowski.schoolmanagement.users.common.service.SchoolUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private AuthEntryPoint authEntryPoint;
    @Lazy
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        /*
            AuthenticationManager registered as Spring Bean to avoid error 'parameter required as bean could not be found'
         */
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new SchoolUserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authEntryPoint))
                .logout(LogoutConfigurer::permitAll)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/students/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/parents/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/teachers/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.POST, "/teachers/*/teacher-availability").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.DELETE, "/teachers/*/teacher-availability/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.GET, "/teachers/*/teacher-availability/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.GET, "/teachers/*/teacher-availability").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers("/teachers/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/yearbooks/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/subjects/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/schedules/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/classrooms/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/grades").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.PATCH, "/grades/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.DELETE, "/grades/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.GET, "/grades/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers(HttpMethod.GET, "/grades/student/*").hasAnyAuthority("ADMINISTRATOR", "TEACHER")
                                .requestMatchers("/grades/**").hasAuthority("ADMINISTRATOR")
                                .requestMatchers("/**").hasAnyAuthority("SYS_ADMIN"))
                .build();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            SYS_ADMIN > ADMINISTRATOR
            ADMINISTRATOR > TEACHER
            TEACHER > STUDENT
            PARENT > STUDENT       \s
        """);  // '>' mean 'include'; admin is also user - can reach any user endpoint
        return hierarchy;
    }
}
