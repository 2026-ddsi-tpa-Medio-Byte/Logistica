package ar.edu.utn.dds.k3003.zAlumno.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpRequestMetricsFilter extends OncePerRequestFilter {

    private final Counter httpRequestsTotal;
    private final Counter httpErrors5xx;

    public HttpRequestMetricsFilter(MeterRegistry meterRegistry) {
        this.httpRequestsTotal = Counter.builder("logistica.http.requests.total")
                .description("Total de requests HTTP recibidos por el módulo Logística")
                .tag("modulo", "logistica")
                .register(meterRegistry);

        this.httpErrors5xx = Counter.builder("logistica.http.errores.5xx")
                .description("Total de respuestas HTTP 5xx del módulo Logística")
                .tag("modulo", "logistica")
                .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        httpRequestsTotal.increment();

        filterChain.doFilter(request, response);

        if (response.getStatus() >= 500) {
            httpErrors5xx.increment();
        }
    }
}
