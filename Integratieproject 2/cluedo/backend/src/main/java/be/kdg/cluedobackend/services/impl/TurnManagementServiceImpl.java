package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.controllers.messagehandlers.TurnMessageHandler;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.game.Turn;
import be.kdg.cluedobackend.repository.CluedoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Predicate;

@Service
public class TurnManagementServiceImpl {
    private final CluedoRepository cluedoRepository;
    private final TurnMessageHandler turnMessageHandler;
    private static final Logger logger = LoggerFactory.getLogger(TurnManagementServiceImpl.class);

    @Autowired
    public TurnManagementServiceImpl(CluedoRepository cluedoRepository, TurnMessageHandler turnMessageHandler) {
        this.cluedoRepository = cluedoRepository;
        this.turnMessageHandler = turnMessageHandler;
    }

    @Scheduled(fixedDelay = 1000)
    public void changeTurnOnLimit(){
        Predicate<Turn> turnLimit = turn ->
                turn.getStartTurn().plusMinutes(turn.getMaxTurnTime()).toEpochSecond(ZoneOffset.UTC) -
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) < 1;

        List<Cluedo> games = this.cluedoRepository.findAllByActiveIsTrue();
        games.stream().filter(game -> turnLimit.test(game.getCurrentTurn())).forEach(game -> {
            try {
                logger.info(String.format("%s's turn has ended on game %d, the game is switching to the next person.",
                                            game.getCurrentTurn().getPlayer().getUser().getUserName(),
                                            game.getCluedoId()));
                game.switchTurn();
                this.turnMessageHandler.sendMessage(game.getCluedoId(), "new", null);
                cluedoRepository.save(game);
            }
            catch (CluedoException e) { e.printStackTrace();}
        });
    }
}
