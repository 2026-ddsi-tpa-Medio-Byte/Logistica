package ar.edu.utn.dds.k3003.zAlumno.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
  Appender de Logback que envia los logs a Datadog por HTTP, de forma ASINCRONICA
  y en LOTES, sin bloquear nunca el hilo que esta logueando.
 */
public class DatadogLogAppender extends AppenderBase<ILoggingEvent> {

    //Parametros configurables desde logback-spring.xml
    private String apiKey;
    private String site = "us5.datadoghq.com";
    private String service = "logistica";
    private String source = "java";
    private String host = "render";
    private int batchSize = 500;
    private long flushIntervalMs = 5000;
    private int queueCapacity = 10_000;

    //Estado interno
    private BlockingQueue<ILoggingEvent> queue;
    private Thread worker;
    private volatile boolean running = false;
    private HttpClient http;

    @Override
    public void start() {
        // Sin API key no arranca el envio remoto solo consola, no es un error
        if (apiKey == null || apiKey.isBlank()) {
            addWarn("DATADOG_API_KEY no definida; el appender de Datadog no se activa.");
            return;
        }
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.running = true;

        this.worker = new Thread(this::runLoop, "datadog-log-appender");
        this.worker.setDaemon(true);
        this.worker.start();

        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!running || queue == null) {
            return;
        }
        // offer() NO bloquea: si la cola esta llena, devuelve false y descartamos
        boolean encolado = queue.offer(event);
        if (!encolado) {
            // Drop silencioso. No logueamos aca para no generar recursion infinita
        }
    }

    private void runLoop() {
        List<ILoggingEvent> batch = new ArrayList<>(batchSize);
        while (running) {
            try {
                // Espera hasta flushIntervalMs por el primer evento del lote
                ILoggingEvent first = queue.poll(flushIntervalMs, TimeUnit.MILLISECONDS);
                if (first != null) {
                    batch.add(first);
                    queue.drainTo(batch, batchSize - 1);
                }
                if (!batch.isEmpty()) {
                    enviar(batch);
                    batch.clear();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Cualquier fallo de envio: descartamos el lote y seguimos. Nunca propagamos
                batch.clear();
            }
        }
        // flush final best-effort al cerrar
        try {
            queue.drainTo(batch);
            if (!batch.isEmpty()) enviar(batch);
        } catch (Exception ignored) {
        }
    }

    private void enviar(List<ILoggingEvent> batch) {
        String json = toJsonArray(batch);
        String url = "https://http-intake.logs." + site + "/api/v2/logs";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("DD-API-KEY", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            // Enviamos y descartamos la respuesta; si falla, no hacemos nada (drop)
            http.send(req, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            // Drop: Datadog caido o red mala no debe afectar el negocio.
        }
    }

    // Construye el array JSON de logs con el formato que espera la API de Datadog
    private String toJsonArray(List<ILoggingEvent> batch) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < batch.size(); i++) {
            ILoggingEvent e = batch.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
                    .append("\"ddsource\":\"").append(esc(source)).append("\",")
                    .append("\"service\":\"").append(esc(service)).append("\",")
                    .append("\"hostname\":\"").append(esc(host)).append("\",")
                    .append("\"status\":\"").append(esc(e.getLevel().toString())).append("\",")
                    .append("\"logger\":\"").append(esc(e.getLoggerName())).append("\",")
                    .append("\"traceId\":\"").append(esc(mdc(e, "traceId"))).append("\",")
                    .append("\"instanceId\":\"").append(esc(mdc(e, "instanceId"))).append("\",")
                    .append("\"requestId\":\"").append(esc(mdc(e, "requestId"))).append("\",")
                    .append("\"message\":\"").append(esc(e.getFormattedMessage())).append("\"")
                    .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String mdc(ILoggingEvent e, String key) {
        String v = e.getMDCPropertyMap().get(key);
        return v == null ? "" : v;
    }

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default   -> {
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
                }
            }
        }
        return out.toString();
    }

    @Override
    public void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
        super.stop();
    }

    //setters usados por logback-spring.xml
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public void setSite(String site) { if (site != null && !site.isBlank()) this.site = site; }
    public void setService(String service) { this.service = service; }
    public void setSource(String source) { this.source = source; }
    public void setHost(String host) { this.host = host; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    public void setFlushIntervalMs(long flushIntervalMs) { this.flushIntervalMs = flushIntervalMs; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
}