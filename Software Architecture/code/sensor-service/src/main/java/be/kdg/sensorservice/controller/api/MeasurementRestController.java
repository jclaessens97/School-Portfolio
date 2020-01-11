package be.kdg.sensorservice.controller.api;

import be.kdg.sensorservice.domain.model.Filter;
import be.kdg.sensorservice.domain.model.Measurement;
import be.kdg.sensorservice.domain.model.SensorType;
import be.kdg.sensorservice.dto.ChartDto;
import be.kdg.sensorservice.dto.ChartDtoBuilder;
import be.kdg.sensorservice.dto.MapsDto;
import be.kdg.sensorservice.services.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * RestController to get data for graphs
 */
@Controller
@RequestMapping(value = "measurements/api")
public class MeasurementRestController {
    private final MeasurementService measurementService;

    @Autowired
    public MeasurementRestController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @GetMapping(value = "/values_per_time")
    @ResponseBody
    public ChartDto getValuesPerTime(@RequestParam(value = "type", required = true) SensorType type) {
        Filter filter = new Filter();
        filter.setType(type);

        List<Measurement> measurements = measurementService.getMeasurements(filter);
        ChartDtoBuilder dtoBuilder = new ChartDtoBuilder();
        for (Measurement m: measurements) {
            dtoBuilder.addLineChartRow(m.getTimeStamp().toLocalDate(), m.getValue());
        }

        return dtoBuilder.LineChartDto();
    }

    @GetMapping(value = "/values_per_coord")
    @ResponseBody
    public List<MapsDto> getValuesPerCoord(@RequestParam(value = "type", required = true) SensorType type) {
        Filter filter = new Filter();
        filter.setType(type);

        List<Measurement> measurements = measurementService.getMeasurements(filter);

        List<MapsDto> dto = new ArrayList<>();
        for (Measurement m: measurements) {
            dto.add(new MapsDto(m.getXCoord(), m.getYCoord(), m.getValue()));
        }

        return dto;
    }

}
