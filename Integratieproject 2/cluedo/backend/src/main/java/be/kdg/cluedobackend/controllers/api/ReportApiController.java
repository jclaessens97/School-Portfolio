package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.report.NewReportDto;
import be.kdg.cluedobackend.dto.report.ReportDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.helpers.RequestUtils;
import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportDetail;
import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.services.PlayerService;
import be.kdg.cluedobackend.services.ReportService;
import be.kdg.cluedobackend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/report")
public class ReportApiController {
    private final ReportService reportService;
    private final UserService userService;
    private final PlayerService playerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportApiController(
        ReportService reportService,
        UserService userService,
        PlayerService playerService,
        ObjectMapper objectMapper
    ) {
        this.reportService = reportService;
        this.userService = userService;
        this.playerService = playerService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/reasons")
    public ResponseEntity<List<ReportReason>> getAllReportReasons() {
        List<ReportReason> reportReasons = reportService.getAllReportReasons();
        return new ResponseEntity<>(reportReasons, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return generateReportDtoArrayResponse(reports);
    }

    @GetMapping("/myreports")
    public ResponseEntity<List<ReportDto>> getAllReportsByCurrentUser() {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        List<Report> reports = reportService.getAllReportsByUserId(userId);
        return generateReportDtoArrayResponse(reports);
    }

    @GetMapping("/for")
    public ResponseEntity<List<ReportDto>> getAllReportsforUserId(@RequestParam UUID userId) {
        List<Report> reports = reportService.getAllReportsForUserId(userId);
        return generateReportDtoArrayResponse(reports);
    }

    @PostMapping()
    public ResponseEntity<ReportDto> createReport(@RequestBody NewReportDto newReportDto) throws CluedoException {
        UUID currentUserId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        User reportedBy = userService.getUserById(currentUserId);

        User reported = playerService.getPlayerByCluedoIdAndPlayerId(
            newReportDto.getCluedoId(), newReportDto.getPlayerId()
        ).getUser();

        Report createdReport = reportService.reportUser(
            reportedBy,
            reported,
            newReportDto.getReportReasons()
        );

        ReportDto createdReportDto = objectMapper.convertValue(createdReport, ReportDto.class);
        return new ResponseEntity<>(createdReportDto, HttpStatus.CREATED);
    }

    @GetMapping("/top")
    public ResponseEntity<List<ReportDetail>> getMostReported(@RequestParam int page, @RequestParam int pageSize) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        User requestUser = userService.getUserById(userId);
        if (!requestUser.getRoles().contains(Role.ADMIN)) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(reportService.getMostReportedUsers(page, pageSize));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getreportedCount() throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        User requestUser = userService.getUserById(userId);
        if (!requestUser.getRoles().contains(Role.ADMIN)) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(reportService.getReportedUsersCount());
    }

    //#region Helpers
    private ResponseEntity<List<ReportDto>> generateReportDtoArrayResponse(List<Report> reports) {
        List<ReportDto> reportDtos = new ArrayList<>(reports.size());

        reports.forEach((r) -> {
            reportDtos.add(
                new ReportDto(
                    r.getReportId(),
                    r.getReportedBy(),
                    r.getReported(),
                    r.getReportReasons(),
                    r.getTimeStamp()
                )
            );
        });

        if (reportDtos.size() > 0) {
            return new ResponseEntity<>(reportDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(reportDtos, HttpStatus.NO_CONTENT);
        }
    }
    //#endregion
}
