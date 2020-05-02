package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.notebook.NotebookDto;
import be.kdg.cluedobackend.dto.notebook.UpdateNoteColumnDto;
import be.kdg.cluedobackend.dto.notebook.UpdateNoteLineDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.helpers.RequestUtils;
import be.kdg.cluedobackend.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NoteApiController {
    private final NoteService noteService;

    @Autowired
    public NoteApiController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PutMapping("/update/column")
    ResponseEntity<Boolean> updateColumn(@RequestBody UpdateNoteColumnDto updateNoteColumnDto) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        noteService.updateNotebookColumn(
                userId,
                updateNoteColumnDto.getNotebookId(),
                updateNoteColumnDto.getCardType(),
                updateNoteColumnDto.getLine(),
                updateNoteColumnDto.getColumn(),
                updateNoteColumnDto.getNotationSymbol()
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/line")
    ResponseEntity updateLine(@RequestBody UpdateNoteLineDto updateNoteLineDto) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        noteService.updateNoteBookLine(
                userId,
                updateNoteLineDto.getNotebookId(),
                updateNoteLineDto.getCardType(),
                updateNoteLineDto.getLine(),
                updateNoteLineDto.isCrossed()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{cluedoId}")
    ResponseEntity<NotebookDto> getNoteBook(@PathVariable int cluedoId) throws CluedoException {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        NotebookDto notebookDto = new NotebookDto(noteService.getNoteBookByUserId(userId, cluedoId));
        return new ResponseEntity<NotebookDto>(notebookDto, HttpStatus.OK);
    }
}
