package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depositos")
public class LogisticaController {
    private final LogisticaService losgisticaService;

    @Autowired
    public LogisticaController(LogisticaService logisticaService){
        this.losgisticaService = logisticaService;
    }

    @GetMapping
    public ResponseEntity<List<LogisticaDTOs.DepositoDTO>> obtenerTodos() {
        List<LogisticaDTOs.DepositoDTO> lista = losgisticaService.obtenerTodosDepositosDTO();

        if (lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> buscarDepositoIDDTO(@PathVariable String id){
        LogisticaDTOs.DepositoDTO deposito = losgisticaService.buscarDepositoIDDTO(id);

        if(deposito == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deposito);
    }

    @PostMapping
    public ResponseEntity<LogisticaDTOs.DepositoDTO> crearDeposito(@RequestBody LogisticaDTOs.DepositoDTO nuevo) {
        LogisticaDTOs.DepositoDTO guardado = losgisticaService.agregarDeposito(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
