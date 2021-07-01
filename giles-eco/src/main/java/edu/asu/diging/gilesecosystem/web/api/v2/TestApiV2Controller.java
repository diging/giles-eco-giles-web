package edu.asu.diging.gilesecosystem.web.api.v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestApiV2Controller {

    @RequestMapping(value = "/api/v2/test", method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
