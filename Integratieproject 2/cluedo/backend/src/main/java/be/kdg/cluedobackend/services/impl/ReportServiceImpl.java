package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.helpers.EnumUtils;
import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportDetail;
import be.kdg.cluedobackend.model.report.ReportDetailProjection;
import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.ReportRepository;
import be.kdg.cluedobackend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<ReportReason> getAllReportReasons() {
        return EnumUtils.getEnumValues(ReportReason.class);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> getAllReportsByUserId(UUID userId) {
        return reportRepository.findAllByReportedBy_UserId(userId);
    }

    @Override
    public List<Report> getAllReportsForUserId(UUID userId) {
        return reportRepository.findAllByReported_UserId(userId);
    }

    @Override
    public Report reportUser(User reporter, User reported, List<ReportReason> reportReasons) {
        Report report = new Report(
            reporter,
            reported,
            reportReasons,
            LocalDateTime.now()
        );

        return reportRepository.save(report);
    }

    @Override
    public List<ReportDetail> getMostReportedUsers(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        int limit = offset + pageSize;

        List<ReportDetail> reportDetails = new ArrayList<>();
        List<ReportDetailProjection> projections = reportRepository.getReportDetail(offset, limit);
        for (ReportDetailProjection pr :
                projections) {
            reportDetails.add(new ReportDetail(convertByteArrToUUID(pr.getUserId()), pr.getUserName(), pr.getCount()));
        }
        return reportDetails;
    }

    @Override
    public int getReportedUsersCount() {
        Integer count = reportRepository.getReportedUserCount();
        if (count == null) return 0;
        return count;
    }

    // Helper
    UUID convertByteArrToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }


}
