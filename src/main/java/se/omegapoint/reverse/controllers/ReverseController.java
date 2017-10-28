package se.omegapoint.reverse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.omegapoint.reverse.model.ReversedData;
import se.omegapoint.reverse.services.ReverseService;

@RestController
@RequestMapping("arne")
public class ReverseController {

    private final ReverseService reverseService;

    @Autowired
    public ReverseController(ReverseService reverseService) {
        this.reverseService = reverseService;
    }

    @RequestMapping(value = "/reverse/{data}", method = RequestMethod.GET, produces="application/json")
    public ReversedData reverse(@PathVariable("data") String data) {
        try {
            return reverseService.reverse(data);
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("Data: " + data);
            return null;
        }
    }

}
