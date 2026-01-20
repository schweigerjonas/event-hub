package de.othr.event_hub.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import de.othr.event_hub.service.AccountUserDetailsService;
import de.othr.event_hub.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AccountUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomWebAuthenticationDetailsSource customDetailsSource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // if the authorization content in the header is not null and starts with
        // "Bearer "...
        // extract from the header the token and the username...
        if (authorizationHeader != null && authorizationHeader.startsWith(jwtService.getPREFIX())) {
            jwt = authorizationHeader.substring(jwtService.getPREFIX().length());
            username = jwtService.extractUsername(jwt);
        }

        // if the user is not empty and the user is not yet authenticated, we will
        // update the security context holder with the token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // trying to find the user in the DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // if the token is valid, we will proceed with the login...
            if (jwtService.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // putting the information of the request object in the
                // usernamePasswordAuthenticationToken
                // usernamePasswordAuthenticationToken
                // .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                usernamePasswordAuthenticationToken.setDetails(customDetailsSource.buildDetails(request));

                // informing to the SecurityContextHolder that the user is authenticated
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // continue with the chain of filters
        chain.doFilter(request, response);
    }
}
