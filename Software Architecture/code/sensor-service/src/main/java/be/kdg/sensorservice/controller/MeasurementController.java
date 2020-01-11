package be.kdg.sensorservice.controller;

import be.kdg.sensorservice.domain.model.Filter;
import be.kdg.sensorservice.domain.model.Measurement;
import be.kdg.sensorservice.domain.model.SensorType;
import be.kdg.sensorservice.dto.FilterDto;
import be.kdg.sensorservice.dto.MeasurementDto;
import be.kdg.sensorservice.services.MeasurementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class to show the list and graph pages
 */
@Controller
@RequestMapping(value = "measurements")
public class MeasurementController {
    private final MeasurementService measurementService;
    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementController(MeasurementService measurementService, ModelMapper modelMapper) {
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/list")
    public ModelAndView list(ModelAndView mav, @ModelAttribute @Valid FilterDto filterDto) {
        final Filter filter = modelMapper.map(filterDto, Filter.class);
        final List<Measurement> measurements = measurementService.getMeasurements(filter);
        final List<MeasurementDto> measurementDtos = measurements
            .stream()
            .map(m -> modelMapper.map(m, MeasurementDto.class))
            .collect(Collectors.toList());

        mav.setViewName("measurements/list_page");
        mav.addObject("filterDto", new FilterDto());
        mav.addObject("measurements", measurementDtos);
        mav.addObject("sensorTypes", SensorType.values());
        return mav;
    }

    @GetMapping(value = "/graph")
    public ModelAndView graph(ModelAndView mav) {
        mav.setViewName("measurements/graph_page");
        mav.addObject("sensorTypes", SensorType.values());
        return mav;
    }
}
