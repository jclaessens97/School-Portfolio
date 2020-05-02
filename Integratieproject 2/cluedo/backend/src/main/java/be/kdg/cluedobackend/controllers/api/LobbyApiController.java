package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.LobbyDetailsDto;
import be.kdg.cluedobackend.dto.LobbyDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.helpers.RequestUtils;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.services.LobbyService;
import be.kdg.cluedobackend.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobby")
public class LobbyApiController {
    private final LobbyService lobbyService;
    private final MessageService messageService;

    @Autowired
    public LobbyApiController(LobbyService lobbyService, MessageService messageService) {
        this.lobbyService = lobbyService;
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createLobby(@RequestParam CharacterType characterType) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        int lobbyId = lobbyService.createLobby(userId, characterType);
        return ResponseEntity.ok(lobbyId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<LobbyDto>> getLobbies(){
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        List<Cluedo> openGames = lobbyService.getAllLobbies();
        List <LobbyDto> lobbies = new ArrayList<>();
        for (Cluedo cluedo :
                openGames) {
            lobbies.add(new LobbyDto(cluedo, userId));
        }
        return ResponseEntity.ok(lobbies);
    }

    @GetMapping("/list/new")
    public ResponseEntity<List<LobbyDto>> getLobbiesNotJoined(){
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        List<Cluedo> openGames = lobbyService.getAllLobbiesNotJoined(userId);
        List <LobbyDto> lobbies = new ArrayList<>();
        for (Cluedo cluedo :
                openGames) {
            lobbies.add(new LobbyDto(cluedo, userId));
        }
        return ResponseEntity.ok(lobbies);
    }

    @GetMapping("/list/joined")
    public ResponseEntity<List<LobbyDto>> getLobbiesJoined(){
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        List<Cluedo> openGames = lobbyService.getAllLobbiesJoined(userId);
        List <LobbyDto> lobbies = new ArrayList<>();
        for (Cluedo cluedo :
                openGames) {
            lobbies.add(new LobbyDto(cluedo, userId));
        }
        return ResponseEntity.ok(lobbies);
    }

    @GetMapping("/games")
    public ResponseEntity<List<LobbyDto>> getOpenGames(){
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        List<Cluedo> openGames = lobbyService.getOpenGames(userId);
        List <LobbyDto> lobbies = new ArrayList<>();
        for (Cluedo cluedo :
                openGames) {
            lobbies.add(new LobbyDto(cluedo, userId));
        }
        return ResponseEntity.ok(lobbies);
    }

    @GetMapping("/free-characters")
    public ResponseEntity<List<CharacterType>> getFreeCharacters(@RequestParam int cluedoId) throws CluedoException {
        List<CharacterType> characterTypes = lobbyService.getFreeCharacters(cluedoId);
        return ResponseEntity.ok(characterTypes);
    }

    @PostMapping("/join")
    public ResponseEntity<Boolean> joinLobby(@RequestParam int cluedoId,
                                             @RequestParam CharacterType characterType) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        lobbyService.joinLobby(cluedoId, userId, characterType);
        messageService.sendSystemMessage(cluedoId,userId, "joined");
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/details")
    public ResponseEntity<LobbyDetailsDto> getLobbyDetails(@RequestParam int cluedoId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        Cluedo cluedo = lobbyService.getLobbyById(cluedoId);
        int playerId = lobbyService.getPlayerInLobby(cluedo, userId).getPlayerId();
        return ResponseEntity.ok(new LobbyDetailsDto(cluedo, playerId));
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Boolean> leaveLobby(@RequestParam int cluedoId,
                                              @RequestParam int playerId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        lobbyService.leaveLobby(cluedoId, playerId, userId, false);
        messageService.sendSystemMessage(cluedoId,userId, "left");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/kick")
    public ResponseEntity<Boolean> kickFromLobby(@RequestParam int cluedoId,
                                                 @RequestParam int playerId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        lobbyService.leaveLobby(cluedoId, playerId, userId, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/start")
    public ResponseEntity<Boolean> startGame(@RequestParam int cluedoId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());

        final Cluedo cluedo = lobbyService.startGame(cluedoId, userId);
        return ResponseEntity.ok(cluedo != null);
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateSettings(@RequestParam int cluedoId,
                                                  @RequestParam String lobbyName,
                                                  @RequestParam int turnDuration,
                                                  @RequestParam int maxPlayers) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        lobbyService.changeGameSettings(cluedoId, userId, lobbyName, turnDuration, maxPlayers);
        return ResponseEntity.ok().build();
    }
}
