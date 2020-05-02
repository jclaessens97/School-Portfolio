package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.DiceDto;
import be.kdg.cluedobackend.helpers.RequestUtils;
import be.kdg.cluedobackend.services.DiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dice")
public class DiceApiController {
    private final DiceService diceService;

    @Autowired
    public DiceApiController(DiceService diceService) {
        this.diceService = diceService;
    }

    @GetMapping("one")
    public ResponseEntity<DiceDto> rollSingleDice() {
        UUID userId = RequestUtils.getUserIdFromAuth(SecurityContextHolder.getContext());
        int roll = diceService.rollOneDice();
        return ResponseEntity.ok(new DiceDto(roll));
    }

    @GetMapping("two")
    public ResponseEntity<DiceDto> rollTwoDice() {
        int[] rolls = diceService.rollTwoDice();
        return ResponseEntity.ok(new DiceDto(rolls));
    }

}
