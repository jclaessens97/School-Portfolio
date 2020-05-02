package be.kdg.cluedobackend.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class DiceServiceTest {
    @Autowired
    private DiceService diceService;

    @Test
    public void rollOneDiceTest() {
        int roll = diceService.rollOneDice();
        Assert.assertTrue(roll >= 1 && roll <= 6);
    }

    @Test
    public void rollTwoDiceTest() {
        int[] roll = diceService.rollTwoDice();
        int sum = roll[0]+roll[1];
        Assert.assertTrue(sum >= 2 && sum <= 12);
    }

    @Test
    public void checkRandomnessDoubleDice() {
        boolean random = false;

        for (int i = 0; i < 50; i++) {
            int[] die = diceService.rollTwoDice();
            random = die[0] != die[1];
            if (random) break;
        }

        Assert.assertTrue("Out of 50 rolls you rolled 50 doubles", random);
    }
}
