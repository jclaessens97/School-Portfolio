package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportDetail;
import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.User;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    /**
     * Get all possible report reasons
     * @return
     */
    List<ReportReason> getAllReportReasons();

    /**
     * Get all reports from everyone
     * @return
     */
    List<Report> getAllReports();

    /**
     * Get all reports performed by a specific user
     * @param userId
     * @return
     */
    List<Report> getAllReportsByUserId(UUID userId);

    /**
     * Get all reports for a specific user
     * @param userId
     * @return
     */
    List<Report> getAllReportsForUserId(UUID userId);

    /**
     * Creates report for a user
     * @param reporter
     * @param reported
     * @param reportReasons
     */
    Report reportUser(User reporter, User reported, List<ReportReason> reportReasons);

    List<ReportDetail> getMostReportedUsers(int page, int pageSize);
    int getReportedUsersCount();
}
