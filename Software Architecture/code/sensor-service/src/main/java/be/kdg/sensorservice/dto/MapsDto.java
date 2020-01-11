package be.kdg.sensorservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MapsDto {
    private final double lat;
    private final double lng;
    private final double weight;
}
