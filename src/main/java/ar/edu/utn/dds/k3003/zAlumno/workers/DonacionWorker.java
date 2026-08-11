package ar.edu.utn.dds.k3003.zAlumno.workers;

import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.DonacionMensaje;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DonacionWorker {

    @Autowired
    private LogisticaService logisticaService;

    @RabbitListener(queues = RabbitMQConfig.COLA_DONACIONES)
    public void procesarDonacion(DonacionMensaje mensaje) {
        System.out.println("[WORKER] Mensaje recibido: donacion " + mensaje.donacionid());

        try {
            logisticaService.procesarDonacionDesdeCola(
                    mensaje.depositoid(),
                    mensaje.donacionid(),
                    mensaje.productoid(),
                    mensaje.cantidad()
            );
            System.out.println("[WORKER] Donacion " + mensaje.donacionid() + " procesada");
        } catch (Exception e) {
            System.out.println("[WORKER] Error procesando donacion " + mensaje.donacionid() + ": " + e.getMessage());
        }
    }
}