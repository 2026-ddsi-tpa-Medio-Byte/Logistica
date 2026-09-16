package ar.edu.utn.dds.k3003.zAlumno.logging;

import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

 /*
 Cada vez que este servicio le pega a otro modulo (Donaciones o Donadores)
    via RestTemplate, reenvia el header X-Trace-Id tomando el valor actual del MDC.
    Asi la cadena de llamadas conserva el mismo traceId de punta a punta y en
    Datadog se puede ver el flujo completo cruzando los 3 modulos con un solo filtro
 */

@Component
public class TraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        String traceId = MDC.get(TraceIdFilter.MDC_TRACE_ID);
        if (traceId != null && !traceId.isBlank()) {
            request.getHeaders().set(TraceIdFilter.TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}