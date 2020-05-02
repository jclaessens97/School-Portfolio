package be.kdg.cluedobackend.dto.report;

import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportDto {
    private long reportId;
    private User reportedBy;
    private User reported;
    private List<ReportReason> reportReasons;
    private LocalDateTime timeStamp;
}
