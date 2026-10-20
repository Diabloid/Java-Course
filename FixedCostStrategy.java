// Розрахунок за фіксованою ставкою (Рівень 1)
public class FixedCostStrategy extends AbstractCostStrategy {

    @Override
    public double calculateCost(Task task, double hourlyRate) {
        return getBaseCost(task, hourlyRate);
    }

    @Override
    public String getStrategyName() {
        return "Fixed Rate Pricing";
    }
}
