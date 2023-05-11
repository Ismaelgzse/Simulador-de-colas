package es.tfg.simuladorteoriacolas.security.prueba;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prueba")
public class Prueba {
    @GetMapping
    public ResponseEntity<String> prueba(){
        return ResponseEntity.ok("Prueba con exito");
    }
}
