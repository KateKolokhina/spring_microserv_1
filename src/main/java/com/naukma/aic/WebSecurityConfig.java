package com.naukma.aic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    /**
     * Encoder of password
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method builder for security configuration
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select email, password, 'true' from user " +
                                "where email=?")
                .authoritiesByUsernameQuery(
                        "select email, status from user " +
                                "where email=?");
    }

    /**
     * Method for security configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .logout()
                .and()
                .authorizeRequests()

                .antMatchers("/user/all", "/zvit/**").hasAuthority("ADMIN")
                .antMatchers("/user/**","/category/**", "/product/**",
                        "receipt/**").hasAnyAuthority("USER","ADMIN")
                .and().exceptionHandling().accessDeniedPage("/accessDenied")
                .and()
                .formLogin()
                .defaultSuccessUrl("/")
                .defaultSuccessUrl("/success_login")
                .permitAll();
    }

}