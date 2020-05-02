package be.kdg.cluedobackend.services;

public interface DiceService {
    /**
     * Returns random value of one dice.
     * @return
     */
    int rollOneDice();

    /**
     * Returns 2 random values of two dices each.
     * @return
     */
    int[] rollTwoDice();
}
