package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportDetail;
import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.ReportRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReportServiceTest {
    @Autowired
    ReportService reportService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportRepository reportRepository;

    User reporter;

    User reported1;
    User reported2;
    User reported3;
    User reported4;

    List<Report> reports = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        reporter = new User(UUID.randomUUID(), "dirk", List.of(Role.USER));
        reported1 = new User(UUID.randomUUID(), "jan", List.of(Role.USER));
        reported2 = new User(UUID.randomUUID(), "peter", List.of(Role.USER));
        reported3 = new User(UUID.randomUUID(), "jos", List.of(Role.USER));
        reported4 = new User(UUID.randomUUID(), "jan", List.of(Role.USER));

        userRepository.save(reporter);
        userRepository.save(reported1);
        userRepository.save(reported2);
        userRepository.save(reported3);
        userRepository.save(reported4);
    }

    @Test
    public void testMostReportedPlayers() {
        this.reportSomeUsers();
        List<ReportDetail> details = reportService.getMostReportedUsers(1, 5);

        Assert.assertEquals(reported2.getUserId(), details.get(0).getUserId());
        Assert.assertEquals(reported1.getUserId(), details.get(1).getUserId());
        Assert.assertEquals(reported3.getUserId(), details.get(2).getUserId());

        Assert.assertEquals(4, (int)details.get(0).getCount());
        Assert.assertEquals(3, (int)details.get(1).getCount());
        Assert.assertEquals(2, (int)details.get(2).getCount());
        Assert.assertEquals(1, (int)details.get(3).getCount());
    }

    @After
    public void tearDown() throws Exception {
        reportRepository.deleteAll(reports);
        userRepository.deleteAll(List.of(reporter, reported1, reported2, reported3, reported4));
    }


    // Helper
    private void reportSomeUsers() {
        // Reports user1
        reports.add(reportService.reportUser(reporter,
                reported1,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported1,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported1,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));

        // Reports user2
        reports.add(reportService.reportUser(reporter,
                reported2,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported2,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported2,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported2,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));

        // Report user3
        reports.add(reportService.reportUser(reporter,
                reported3,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
        reports.add(reportService.reportUser(reporter,
                reported3,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));

        // Report user3
        reports.add(reportService.reportUser(reporter,
                reported4,
                List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.OFFENSIVE_USERNAME)));
    }


}
