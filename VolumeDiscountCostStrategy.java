// Прогресивна знижка для завдань > 5 год (Рівень 1)
public class VolumeDiscountCostStrategy extends AbstractCostStrategy {

    private static final double THRESHOLD_HOURS = 5.0;
    private static final double DISCOUNT_RATE = 0.85; // 15% знижки на години понад ліміт

    @Override
    public double calculateCost(Task task, double hourlyRate) {
        validateRate(hourlyRate);
        double hours = task.getEstimatedHours();
        if (hours <= THRESHOLD_HOURS) {
            return hours * hourlyRate;
        } else {
            double standardPart = THRESHOLD_HOURS * hourlyRate;
            double discountedPart = (hours - THRESHOLD_HOURS) * hourlyRate * DISCOUNT_RATE;
            return standardPart + discountedPart;
        }
    }

    @Override
    public String getStrategyName() {
        return "Volume Discount Pricing (>5h: -15%)";
    }
}
