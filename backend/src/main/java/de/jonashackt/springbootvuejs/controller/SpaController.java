package de.jonashackt.springbootvuejs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller()
@RequestMapping("/")
public class SpaController {

    private static final Logger LOG = LoggerFactory.getLogger(SpaController.class);

    // Forwards all routes to FrontEnd except: '/', '/index.html', '/api', '/api/**'
    // Required because of 'mode: history' usage in frontend routing, see README for further details
    @RequestMapping(value = "{_:^(?!index\\.html|api).*$}")
    public String redirectSpa() {
        LOG.debug("URL entered directly into the Browser, so we need to redirect...");
        
        return "forward:/";
    }
}
