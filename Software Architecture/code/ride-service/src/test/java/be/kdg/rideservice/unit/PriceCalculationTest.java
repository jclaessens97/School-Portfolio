package be.kdg.rideservice.unit;

import be.kdg.rideservice.domain.model.pricing.calculator.PricingCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PriceCalculationTest {
    @Autowired
    private PricingCalculator calculator;

    @Test
    public void testPriceCalculation() {
        double price = calculator.calculatePrice(60, 15, 1000);
        Assert.assertEquals(141.0, price, 0.01);
    }
}
