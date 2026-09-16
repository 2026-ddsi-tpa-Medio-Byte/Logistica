package ar.edu.utn.dds.k3003.zAlumno.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



 /*Por cada request entrante:
    lee el header X-Trace-Id; si no viene, genera un UUID (este servicio es el
    punto de entrada de la cadena).*/

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String MDC_TRACE_ID = "traceId";

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);

    private final InstanceInfo instanceInfo;

    public TraceIdFilter(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // No ensuciar los logs con el health-check que Render pinja constantemente.
        return request.getRequestURI().startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put("instanceId", instanceInfo.getInstanceId());
        MDC.put("requestId", requestId);

        // Devolver el traceId al cliente para que pueda correlacionar del lado de afuera.
        response.setHeader(TRACE_ID_HEADER, traceId);

        long start = System.currentTimeMillis();
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());
        try {
            chain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start;
            log.info("<-- {} {} status={} took={}ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), took);
            MDC.clear();
        }
    }
}