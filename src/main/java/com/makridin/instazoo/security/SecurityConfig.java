package com.makridin.instazoo.security;

import com.makridin.instazoo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        proxyTargetClass = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JWTAuthenticationEntryPoint entryPoint;
    private CustomUserDetailsService detailsService;

    @Autowired
    public SecurityConfig(JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomUserDetailsService customUserDetailsService)
    {
        this.entryPoint = jwtAuthenticationEntryPoint;
        this.detailsService = customUserDetailsService;
    }

    @Bean
    public JWTAuthenticationFilter getJWTAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(SecurityConstants.SIGN_UP_URLS).permitAll()
                .anyRequest().authenticated();

        security.addFilterBefore(getJWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
