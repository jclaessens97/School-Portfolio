package be.kdg.cluedobackend.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportDetail {
    private UUID userId;
    private String userName;
    private Integer count;
}
