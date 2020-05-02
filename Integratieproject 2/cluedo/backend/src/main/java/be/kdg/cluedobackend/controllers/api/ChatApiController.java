package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.MessageDto;
import be.kdg.cluedobackend.model.chat.Message;
import be.kdg.cluedobackend.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {
    private final MessageService messageService;

    @Autowired
    public ChatApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Boolean> sendMessage(@RequestParam Integer cluedoId,@RequestParam String msg, @RequestParam Integer playerId) {
        messageService.sendMessage(cluedoId,msg,playerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cluedoId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Integer cluedoId){
        List<Message> messageList = messageService.getMessages(cluedoId);
        List<MessageDto> dtoList = new ArrayList<>();
        for (Message msg : messageList){
            dtoList.add(new MessageDto(msg));
        }
        return ResponseEntity.ok(dtoList);
    }
}
