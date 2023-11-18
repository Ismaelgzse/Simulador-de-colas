package es.tfg.simuladorteoriacolas.SPA;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPAController {

    @GetMapping({"/app/**/{path:[^\\.]*}","/{path:app[^\\.]*}"})
    public String redirect(){
        return "forward:/app/index.html";
    }

}
