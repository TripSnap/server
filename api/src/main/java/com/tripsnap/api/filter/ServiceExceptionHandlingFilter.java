package com.tripsnap.api.filter;

import com.google.gson.Gson;
import com.tripsnap.api.exception.ServiceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class ServiceExceptionHandlingFilter extends OncePerRequestFilter {
    private final Gson gson = new Gson();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            doFilter(request, response, filterChain);
        } catch (ServiceException e) {
            response.setStatus(e.getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.print(gson.toJson(e.getBody()));
        }
    }
}
