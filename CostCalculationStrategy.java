// Інтерфейс стратегії розрахунку вартості (Рівень 1)
public interface CostCalculationStrategy {

    double calculateCost(Task task, double hourlyRate);

    String getStrategyName();

    // Default-метод (Рівень 2)
    default String getDetailedDescription(Task task, double hourlyRate) {
        return String.format("[%s] Підсумкова вартість для '%s': $%.2f (базова ставка $%.2f/год)",
                getStrategyName(), task.getName(), calculateCost(task, hourlyRate), hourlyRate);
    }
}
