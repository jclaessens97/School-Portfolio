package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.helpers.EnumUtils;
import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.cards.types.WeaponType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.notebook.NotationSymbol;
import be.kdg.cluedobackend.model.notebook.NoteBook;
import be.kdg.cluedobackend.model.notebook.NoteLine;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.NoteBookRepository;
import be.kdg.cluedobackend.services.LobbyService;
import be.kdg.cluedobackend.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteBookRepository noteBookRepository;
    private final CluedoRepository cluedoRepository;
    private LobbyService lobbyService;

    @Autowired
    public NoteServiceImpl(
            NoteBookRepository noteBookRepository,
            CluedoRepository cluedoRepository
    ) {
        this.noteBookRepository = noteBookRepository;
        this.cluedoRepository = cluedoRepository;
    }

    @Override
    public void setLobbyService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @Override
    public NoteBook initializeNoteBook(int playerCount){
        Map<CardType, List<NoteLine>> noteLines = new HashMap<>();

        noteLines.put(CardType.CHARACTER, new ArrayList<>());
        EnumUtils.getEnumValues(CharacterType.class)
            .forEach((characterType -> {
                noteLines.get(CardType.CHARACTER).add(createNoteLine(
                    characterType.name(),
                    playerCount
                ));
            }));

        noteLines.put(CardType.WEAPON, new ArrayList<>());
        EnumUtils.getEnumValues(WeaponType.class)
                .forEach((weaponType -> {
                    noteLines.get(CardType.WEAPON).add(createNoteLine(
                        weaponType.name(),
                        playerCount
                    ));
                }));

        noteLines.put(CardType.ROOM, new ArrayList<>());
        EnumUtils.getEnumValues(RoomType.class)
                .forEach((roomType -> {
                    noteLines.get(CardType.ROOM).add(createNoteLine(
                            roomType.name(),
                            playerCount
                    ));
                }));

        return new NoteBook(noteLines);
    }

    //#region CRUD
    @Override
    public NoteBook getNoteBookByUserId(UUID userId, int cluedoId) throws CluedoException {
        Cluedo cluedo = lobbyService.getLobbyById(cluedoId);
        Player player = lobbyService.getPlayerInLobby(cluedo, userId);
        return player.getNoteBook();
    }

    @Override
    public NoteBook createNotebook(NoteBook noteBook) {
        return this.noteBookRepository.save(noteBook);
    }

    @Override
    public void updateNotebookColumn(UUID userId, int notebookId, CardType cardType, int line, int column, NotationSymbol symbol) throws CluedoException {
        NoteBook noteBook = getNotebookById(notebookId);
        checkAuthorization(noteBook, userId);

        switch (cardType) {
            case CHARACTER:
                noteBook.getCharacters().get(line).getColumns().set(column, symbol);
                break;
            case WEAPON:
                noteBook.getWeapons().get(line).getColumns().set(column, symbol);
                break;
            case ROOM:
                noteBook.getRooms().get(line).getColumns().set(column, symbol);
                break;
        }
        noteBookRepository.save(noteBook);
    }

    @Override
    public void updateNoteBookLine(UUID userId, int notebookId, CardType cardType, int line, boolean crossed) throws CluedoException {
        NoteBook noteBook = getNotebookById(notebookId);
        checkAuthorization(noteBook, userId);

        switch (cardType) {
            case CHARACTER:
                noteBook.getCharacters().get(line).setCrossed(crossed);
                break;
            case WEAPON:
                noteBook.getWeapons().get(line).setCrossed(crossed);
                break;
            case ROOM:
                noteBook.getRooms().get(line).setCrossed(crossed);
                break;
        }

        noteBookRepository.save(noteBook);
    }
    //#endregion

    //#region Helpers
    private void checkAuthorization(NoteBook noteBook, UUID userId) throws CluedoException {
        if (!noteBook.getPlayer().getUserId().equals(userId)) {
            throw new CluedoException(CluedoExceptionType.PLAYER_USER_NOT_LINKED);
        }
    }

    private NoteLine createNoteLine(String cardValue, int playerCount) {
        NoteLine noteLine = new NoteLine();
        noteLine.setCrossed(false);
        noteLine.setCard(cardValue);

        List<NotationSymbol> notationSymbols = new ArrayList<>();
        for (int i = 0; i < playerCount - 1; i++) {
            notationSymbols.add(NotationSymbol.EMPTY);
        }
        noteLine.setColumns(notationSymbols);

        return noteLine;
    }

    private NoteBook getNotebookById(int notebookId) throws CluedoException {
        return noteBookRepository
            .findById(notebookId)
            .orElseThrow(() ->
                new CluedoException(
                    CluedoExceptionType.NOTEBOOK_NOT_FOUND,
                    String.format("No notebook found with id %d", notebookId))
            );
    }
    //#endregion
}
