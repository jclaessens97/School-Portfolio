package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.services.DiceService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class DiceServiceImpl implements DiceService {
    public int rollOneDice() {
        return roll();
    }

    public int[] rollTwoDice() {
        return new int[]{roll(),roll()};
    }

    private int roll() {
        return ThreadLocalRandom.current().nextInt(1, 7);
    }
}
