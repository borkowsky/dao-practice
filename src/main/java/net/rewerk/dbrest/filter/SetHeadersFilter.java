package net.rewerk.dbrest.filter;

import jakarta.servlet.*;

import java.io.IOException;

public class SetHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        chain.doFilter(request, response);
    }
}
