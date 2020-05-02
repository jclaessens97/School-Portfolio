package be.kdg.cluedobackend.integration;

import be.kdg.cluedobackend.dto.report.NewReportDto;
import be.kdg.cluedobackend.dto.report.ReportDto;
import be.kdg.cluedobackend.helpers.EnumUtils;
import be.kdg.cluedobackend.helpers.MockSecurityContext;
import be.kdg.cluedobackend.model.report.Report;
import be.kdg.cluedobackend.model.report.ReportReason;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.ReportRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
public class ReportApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @Test
    public void getReportReasons() throws Exception {
        MockSecurityContext.mockNormalUser(UUID.randomUUID());

        MvcResult result = mockMvc.perform(
                get("/api/report/reasons")
                    .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        List<ReportReason> returnedReportReasons = Arrays.asList(objectMapper.readValue(response, ReportReason[].class));
        List<ReportReason> realReportReasons = EnumUtils.getEnumValues(ReportReason.class);

        Assert.assertEquals(realReportReasons, returnedReportReasons);
    }

    @Test
    public void getAllReports() throws Exception {
        UUID uuid = UUID.randomUUID();

        MockSecurityContext.mockNormalUser(uuid);

        User u1 = new User(uuid, "TestU1", List.of(Role.USER));
        User u2 = new User(UUID.randomUUID(), "TestU2", List.of(Role.USER));
        Report r1 = new Report(u1, u2, List.of(ReportReason.OFFENSIVE_LANGUAGE), LocalDateTime.now());
        Report r2 = new Report(u2, u1, List.of(ReportReason.CHEATING), LocalDateTime.now());

        userRepository.saveAll(List.of(u1, u2));
        reportRepository.saveAll(List.of(r1, r2));

        MvcResult result = mockMvc.perform(
                get("/api/report/")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        List<ReportDto> returnedReports = Arrays.asList(objectMapper.readValue(response, ReportDto[].class));

        Assert.assertEquals(2, returnedReports.size());
        Assert.assertEquals(u1, returnedReports.get(0).getReportedBy());
        Assert.assertEquals(u2, returnedReports.get(0).getReported());
        Assert.assertEquals(u2, returnedReports.get(1).getReportedBy());
        Assert.assertEquals(u1, returnedReports.get(1).getReported());
        Assert.assertEquals(List.of(ReportReason.OFFENSIVE_LANGUAGE), returnedReports.get(0).getReportReasons());
        Assert.assertEquals(List.of(ReportReason.CHEATING), returnedReports.get(1).getReportReasons());
    }

    @Test
    public void getAllReportsByCurrentUser() throws Exception {
        UUID uuid = UUID.randomUUID();

        MockSecurityContext.mockNormalUser(uuid);

        User u1 = new User(uuid, "TestU1", List.of(Role.USER));
        User u2 = new User(UUID.randomUUID(), "TestU2", List.of(Role.USER));
        Report r1 = new Report(u1, u2, List.of(ReportReason.OFFENSIVE_LANGUAGE), LocalDateTime.now());
        Report r2 = new Report(u2, u1, List.of(ReportReason.CHEATING), LocalDateTime.now());

        userRepository.saveAll(List.of(u1, u2));
        reportRepository.saveAll(List.of(r1, r2));

        MvcResult result = mockMvc.perform(
                get("/api/report/myreports")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        List<ReportDto> returnedReports = Arrays.asList(objectMapper.readValue(response, ReportDto[].class));

        Assert.assertEquals(1, returnedReports.size());
        Assert.assertEquals(u1, returnedReports.get(0).getReportedBy());
        Assert.assertEquals(u2, returnedReports.get(0).getReported());
        Assert.assertEquals(List.of(ReportReason.OFFENSIVE_LANGUAGE), returnedReports.get(0).getReportReasons());
    }

    @Test
    public void getAllReportsForUserId() throws Exception {
        UUID uuid = UUID.randomUUID();

        MockSecurityContext.mockNormalUser(uuid);

        User u1 = new User(uuid, "TestU1", List.of(Role.USER));
        User u2 = new User(UUID.randomUUID(), "TestU2", List.of(Role.USER));
        Report r1 = new Report(u1, u2, List.of(ReportReason.OFFENSIVE_LANGUAGE), LocalDateTime.now());
        Report r2 = new Report(u2, u1, List.of(ReportReason.CHEATING), LocalDateTime.now());

        userRepository.saveAll(List.of(u1, u2));
        reportRepository.saveAll(List.of(r1, r2));

        MvcResult result = mockMvc.perform(
                get("/api/report/for")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("userId", u2.getUserId().toString())
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        List<ReportDto> returnedReports = Arrays.asList(objectMapper.readValue(response, ReportDto[].class));

        Assert.assertEquals(1, returnedReports.size());
        Assert.assertEquals(u1, returnedReports.get(0).getReportedBy());
        Assert.assertEquals(u2, returnedReports.get(0).getReported());
        Assert.assertEquals(List.of(ReportReason.OFFENSIVE_LANGUAGE), returnedReports.get(0).getReportReasons());
    }

    @Test
    public void createReport() throws Exception {
        UUID uuid = UUID.randomUUID();

        MockSecurityContext.mockNormalUser(uuid);

        User u1 = new User(uuid, "TestU1", List.of(Role.USER));
        User u2 = new User(UUID.randomUUID(), "TestU2", List.of(Role.USER));
        Player p = new Player();
        p.setUser(u2);

        userRepository.saveAll(List.of(u1, u2));

        when(playerRepository.findByCluedo_CluedoIdAndPlayerId(anyInt(), anyInt()))
            .thenReturn(p);

        NewReportDto newReportDto = new NewReportDto(0, 0, List.of(ReportReason.OFFENSIVE_LANGUAGE, ReportReason.CHEATING));

        MvcResult result = mockMvc.perform(
            post("/api/report/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReportDto))
        )
        .andExpect(status().isCreated())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        ReportDto reportDto = objectMapper.readValue(response, ReportDto.class);
        Assert.assertEquals(u1, reportDto.getReportedBy());
        Assert.assertEquals(u2, reportDto.getReported());
        Assert.assertEquals(ReportReason.OFFENSIVE_LANGUAGE, reportDto.getReportReasons().get(0));
        Assert.assertEquals(ReportReason.CHEATING, reportDto.getReportReasons().get(1));
        Assert.assertNotNull(reportDto.getTimeStamp());
    }
}
