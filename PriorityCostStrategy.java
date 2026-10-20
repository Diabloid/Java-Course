// Розрахунок вартості з урахуванням терміновості (ЛР 1, Рівень 1)
public class PriorityCostStrategy extends AbstractCostStrategy {

    @Override
    public double calculateCost(Task task, double hourlyRate) {
        double baseCost = getBaseCost(task, hourlyRate);
        switch (task.getPriority()) {
            case 1:
                return baseCost * 1.20; // 20% націнки
            case 2:
                return baseCost;
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

    // Перевизначення default-методу (Рівень 2)
    @Override
    public String getDetailedDescription(Task task, double hourlyRate) {
        return String.format("[%s] Завдання '%s' (%s): $%.2f (враховано коефіцієнт терміновості)",
                getStrategyName(), task.getName(), task.getPriorityLabel(), calculateCost(task, hourlyRate));
    }
}
