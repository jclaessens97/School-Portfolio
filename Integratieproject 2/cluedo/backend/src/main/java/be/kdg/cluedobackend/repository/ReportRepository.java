package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReportedBy_UserId(UUID userId);
    List<Report> findAllByReported_UserId(UUID userId);

    @Query(value = "SELECT count(*) count, user_id userId, user_name username FROM report r JOIN app_user u on(r.reported_user_id = u.user_id)  GROUP BY reported_user_id ORDER BY count(*) DESC LIMIT ?2  OFFSET ?1", nativeQuery = true)
    List<ReportDetailProjection> getReportDetail(int offset, int limit);

    @Query(value = "SELECT coalesce(count(*), 0) FROM report r GROUP BY reported_user_id", nativeQuery = true)
    int getReportedUserCount();
}
