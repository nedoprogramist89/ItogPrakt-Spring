package com.example.springmodels.config;

import com.example.springmodels.models.ModelUser;
import com.example.springmodels.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            ModelUser user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("Такой пользователь не существует");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isActive(),
                    true,
                    true,
                    true,
                    user.getRoles()
            );
        }).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Статические ресурсы должны быть разрешены первыми
                .antMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**", "/favicon.ico").permitAll()
                .antMatchers("/login", "/registration").permitAll()
                .antMatchers("/api/**").permitAll() // REST API доступен всем
                .antMatchers("/admin/**").hasAnyAuthority("ADMIN")
                // Просмотр товаров, категорий, производителей доступен всем аутентифицированным
                .antMatchers("/categories", "/manufacturers", "/products").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers("/categories/**", "/manufacturers/**", "/products/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // Корзина и адреса только для обычных пользователей - проверка в контроллерах через @PreAuthorize
                .antMatchers("/cart/**", "/addresses/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // Заказы доступны всем, но логика разная - проверка в контроллерах через @PreAuthorize
                .antMatchers("/orders/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // Отзывы доступны всем аутентифицированным
                .antMatchers("/reviews/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // Платежи доступны всем аутентифицированным
                .antMatchers("/payments/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // Главная страница доступна всем аутентифицированным
                .antMatchers("/").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
                .csrf().disable()
                .cors().disable();

        return http.build();
    }
}
