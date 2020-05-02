package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.StatisticDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.helpers.RequestUtils;
import be.kdg.cluedobackend.model.users.FriendType;
import be.kdg.cluedobackend.model.users.GameStatistics;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserApiController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> addFriend(@RequestParam String friendUserName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        userService.addFriend(userId, friendUserName);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/confirm")
    public ResponseEntity<Boolean> confirmFriend(@RequestParam String friendUserName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        userService.updateFriend(userId, friendUserName, FriendType.CONFIRMED);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/block")
    public ResponseEntity<Boolean> blockFriend(@RequestParam String friendUserName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        userService.updateFriend(userId, friendUserName, FriendType.BLOCKED);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<User>> getFriends() {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        return ResponseEntity.ok(userService.getFriends(userId, FriendType.CONFIRMED));
    }

    @GetMapping("/available_friends")
    public ResponseEntity<List<String>> getAvailableFriends(@RequestParam int cluedoId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        return ResponseEntity.ok(userService.getAvailableFriends(userId, cluedoId));
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<User>> getBlocked() {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        return ResponseEntity.ok(userService.getFriends(userId, FriendType.CONFIRMED));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPending() {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        return ResponseEntity.ok(userService.getPendings(userId));
    }

    @DeleteMapping("delete_pending")
    public ResponseEntity<Boolean> deletePending(@RequestParam String friendUserName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        return ResponseEntity.ok(userService.updateFriend(userId,friendUserName, FriendType.DELETE_PENDING));
    }

    @DeleteMapping("/delete_friend")
    public ResponseEntity<Boolean> deleteFriend(@RequestParam String friendUserName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        userService.deleteFriend(userId, friendUserName);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/statistics/id")
    public ResponseEntity<StatisticDto> getStatistics(@RequestParam int cluedoId,
                                                      @RequestParam int playerId) throws CluedoException {
        GameStatistics gameStatistics = userService.getStatistics(cluedoId, playerId);
        StatisticDto statisticDto = this.objectMapper.convertValue(gameStatistics, StatisticDto.class);
        return ResponseEntity.ok(statisticDto);
    }

    @GetMapping("/statistics/username")
    public ResponseEntity<StatisticDto> getStatistics(@RequestParam String userName) throws CluedoException {
        GameStatistics gameStatistics = userService.getStatistics(userName);
        StatisticDto statisticDto = this.objectMapper.convertValue(gameStatistics, StatisticDto.class);
        return ResponseEntity.ok(statisticDto);
    }

    @PostMapping("/invite")
    public ResponseEntity<Boolean> invite(@RequestParam int cluedoId, @RequestParam String userName) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        userService.invite(userId, cluedoId, userName);
        return ResponseEntity.ok(true);
    }
}
