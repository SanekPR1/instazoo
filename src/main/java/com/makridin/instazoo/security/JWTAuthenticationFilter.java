package com.makridin.instazoo.security;

import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private JWTTokenProvider jwtTokenProvider;
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public JWTAuthenticationFilter(JWTTokenProvider jwtTokenProvider,
                                   CustomUserDetailsService userDetailsService)
    {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.isToeknValid(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                User user = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
                        null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(Exception ex) {
            LOG.error("Couldn't set user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String beartToken = request.getHeader(SecurityConstants.HEADER_STRING);
        if (StringUtils.hasText(beartToken) && beartToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return beartToken.split(" ")[1];
        }
        return null;
    }
}
