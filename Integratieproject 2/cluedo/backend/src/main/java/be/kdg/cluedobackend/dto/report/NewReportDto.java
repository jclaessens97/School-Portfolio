package be.kdg.cluedobackend.dto.report;

import be.kdg.cluedobackend.model.report.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewReportDto {
    private int cluedoId;
    private int playerId;
    private List<ReportReason> reportReasons;
}
