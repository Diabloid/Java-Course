/**
 * Стратегія розрахунку вартості на основі пріоритетності (успадкована з ЛР 1).
 * Пріоритет 1 (Високий): +20% за терміновість.
 * Пріоритет 2 (Середній): базова вартість.
 * Пріоритет 3 (Низький): 10% знижки.
 */
public class PriorityCostStrategy extends AbstractCostStrategy {

    @Override
    public double calculateCost(Task task, double hourlyRate) {
        double baseCost = getBaseCost(task, hourlyRate);
        switch (task.getPriority()) {
            case 1:
                return baseCost * 1.20; // 20% націнки за терміновість
            case 2:
                return baseCost;        // Базовий тариф
            case 3:
                return baseCost * 0.90; // 10% знижки
            default:
                return baseCost;
        }
    }

    @Override
    public String getStrategyName() {
        return "Priority-Based Pricing";
    }

    /**
     * Перевизначення default-методу інтерфейсу (Рівень 2):
     * додаємо інформацію про коефіцієнт пріоритету.
     */
    @Override
    public String getDetailedDescription(Task task, double hourlyRate) {
        return String.format("[%s] Завдання '%s' (%s): $%.2f (враховано коефіцієнт терміновості)",
                getStrategyName(), task.getName(), task.getPriorityLabel(), calculateCost(task, hourlyRate));
    }
}
