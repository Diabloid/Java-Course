/**
 * Стратегія розрахунку за фіксованою ставкою.
 * Розраховує вартість строго як години * ставка, незалежно від пріоритету завдання.
 * Використовує базову реалізацію default-методу getDetailedDescription без перевизначення.
 */
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
