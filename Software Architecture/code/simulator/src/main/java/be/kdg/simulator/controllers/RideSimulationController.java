package be.kdg.simulator.controllers;

import be.kdg.simulator.services.RideSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class to handle all frontend interaction w/ ride simulation
 */
@Controller
@RequestMapping("/ride-simulation")
public class RideSimulationController {
    private final RideSimulationService rideSimulationService;

    @Autowired
    public RideSimulationController(RideSimulationService rideSimulationService) {
        this.rideSimulationService = rideSimulationService;
    }

    @GetMapping(value = "")
    public ModelAndView sensorSimulation(ModelAndView mav) {
        mav.setViewName("ridesimulation_page");
        return mav;
    }

    @PostMapping(value = "/start")
    public ModelAndView startRideSimulation(
        @RequestParam("file") MultipartFile file,
        ModelAndView mav
    ) {
        rideSimulationService.startSimulationAsync(file);

        mav.setViewName("redirect:/ride-simulation");
        return mav;
    }
}
