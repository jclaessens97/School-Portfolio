package be.kdg.sensorservice.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates chartDto
 */
public class ChartDtoBuilder {
    private ChartDto dto;

    public ChartDtoBuilder() {
        dto = new ChartDto();
    }

    public ChartDto LineChartDto() {
        dto.getCols().add(dto.new Column('A', "Date", "date"));
        dto.getCols().add(dto.new Column('B',"Value", "number"));
        return dto;
    }

    public ChartDto HeatmapChartDto() {
        dto.getCols().add(dto.new Column('A', "location", "string"));
        ChartDto.Column column = dto.new Column('C',"weight", "number");
        column.addRole("style");
        dto.getCols().add(column);
        return dto;
    }

    public void addLineChartRow(LocalDate date, double value) {
        List<ChartDto.Cell> cells = new ArrayList<>();
        cells.add(dto.new StringCell("Date("+date.getYear()+","+date.getMonthValue()+","+date.getDayOfMonth()+")"));
        cells.add(dto.new NumberCell(value));
        dto.addRow(cells);
    }

    public void addHeatmapChartRow(double xCoord, double yCoord, double value) {
        List<ChartDto.Cell> cells = new ArrayList<>();
        cells.add(dto.new StringCell("new google.maps.LatLng("+xCoord+", "+yCoord+")"));
        cells.add(dto.new NumberCell(value));
        dto.addRow(cells);
    }
}
