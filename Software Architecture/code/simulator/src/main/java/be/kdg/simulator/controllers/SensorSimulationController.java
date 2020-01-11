package be.kdg.simulator.controllers;

import be.kdg.simulator.config.sensorservice.SensorGenerationProperties;
import be.kdg.simulator.services.SensorSimulationService;
import be.kdg.simulator.services.impl.SensorSimulationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class to handle all frontend interaction w/ sensor simulation
 */
@Controller
@RequestMapping("/sensor-simulation")
public class SensorSimulationController {
    private final SensorSimulationService sensorSimulationService;

    @Autowired
    public SensorSimulationController(SensorSimulationServiceImpl sensorSimulationService) {
        this.sensorSimulationService = sensorSimulationService;
    }

    @GetMapping(value = "")
    public ModelAndView sensorSimulation(ModelAndView mav) {
        mav.setViewName("sensorsimulation_page");
        mav.addObject("properties", sensorSimulationService.getSensorGenerationProperties());
        mav.addObject("isBusy", sensorSimulationService.isSimulatorRunning());
        return mav;
    }

    @PostMapping(value = "/start")
    public ModelAndView startSensorSimulation(
        @ModelAttribute SensorGenerationProperties sensorGenerationProperties,
        ModelAndView mav
    ) {
        sensorSimulationService.updateSensorGenerationProperties(sensorGenerationProperties);
        sensorSimulationService.startSimulationAsync();

        mav.setViewName("redirect:/sensor-simulation");
        mav.addObject("properties", sensorGenerationProperties);
        return mav;
    }
}
