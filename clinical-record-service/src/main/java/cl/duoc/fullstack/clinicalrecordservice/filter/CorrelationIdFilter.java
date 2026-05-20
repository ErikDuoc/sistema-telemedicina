package cl.duoc.fullstack.clinicalrecordservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = UUID.randomUUID().toString();

        MDC.put("X-Correlation-ID", correlationId);
        response.setHeader("X-Correlation-ID", correlationId);

        try{
            filterChain.doFilter(request,response);
        } finally {
            MDC.clear();
        }
    }
}