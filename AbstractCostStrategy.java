// Абстрактний клас зі спільною логікою (Рівень 2)
public abstract class AbstractCostStrategy implements CostCalculationStrategy {

    protected void validateRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Годинна ставка має бути більшою за 0");
        }
    }

    // Розрахунок базової вартості
    protected double getBaseCost(Task task, double hourlyRate) {
        validateRate(hourlyRate);
        return task.getEstimatedHours() * hourlyRate;
    }
}
