package be.kdg.cluedobackend.model.report;

import be.kdg.cluedobackend.model.users.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column
    @Enumerated
    @ElementCollection(targetClass = ReportReason.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ReportReason> reportReasons;

    @OneToOne
    private User reported;

    @Column
    private LocalDateTime timeStamp;

    @OneToOne
    private User reportedBy;

    public Report(User reportedBy, User reported, List<ReportReason> reportReasons, LocalDateTime timeStamp) {
        this.reportedBy = reportedBy;
        this.reported = reported;
        this.reportReasons = reportReasons;
        this.timeStamp = timeStamp;
    }
}
