package cl.duoc.fullstack.gatewayservice.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(-1)
public class CorrelationIdFilter implements GlobalFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String headerId = exchange.getRequest().getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        String correlationId = (headerId == null || headerId.isBlank())
                ? UUID.randomUUID().toString()
                : headerId;

        MDC.put(CORRELATION_ID_HEADER, correlationId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header(CORRELATION_ID_HEADER, correlationId))
                .build();

        return chain.filter(mutatedExchange)
                .then(Mono.fromRunnable(() -> MDC.clear()));
    }
}
