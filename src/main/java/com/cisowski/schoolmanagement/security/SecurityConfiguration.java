package com.cisowski.schoolmanagement.security;

import com.cisowski.schoolmanagement.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
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
                /*

                .authorizeHttpRequests(request ->
                        request.requestMatchers("/test/publicHello")
                                .permitAll())
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/test/adminHello")
                                .hasRole("ADMIN"))

                .authorizeHttpRequests(request ->
                        request.anyRequest().authenticated())
               */
                .authorizeHttpRequests((request ->
                        request.anyRequest().permitAll()))
//                .authorizeHttpRequests(request ->
//                        request.requestMatchers("/admin/**").hasAuthority("SYS_ADMIN"))
//                .authorizeHttpRequests(request ->
//                        request.requestMatchers("/student/**").hasAuthority("STUDENT"))
//                .authorizeHttpRequests(request ->
//                        request.requestMatchers("/teacher/**").hasAuthority("TEACHER"))
//                .authorizeHttpRequests(request ->
//                        request.requestMatchers("/*").hasAnyAuthority("SYS_ADMIN"))
                .formLogin(formLogin ->
                        formLogin.loginPage("/login").permitAll())
                .httpBasic(Customizer.withDefaults())
                .logout(LogoutConfigurer::permitAll)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();


    }
    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("SYS_ADMIN > ADMINISTRATOR > PRINCIPAL > TEACHER \n SYS_ADMIN > PARENT > STUDENT");
        return hierarchy;
    }
}
