// Фабрика створення стратегій розрахунку вартості (Рівень 1 - Factory)
public class CostStrategyFactory {

    public static CostCalculationStrategy createStrategy(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип стратегії не може бути null");
        }
        switch (type.trim().toLowerCase()) {
            case "priority":
            case "1":
                return new PriorityCostStrategy();
            case "fixed":
            case "2":
                return new FixedCostStrategy();
            case "volume":
            case "discount":
            case "3":
                return new VolumeDiscountCostStrategy();
            default:
                throw new IllegalArgumentException("Невідомий тип стратегії розрахунку: " + type);
        }
    }
}
