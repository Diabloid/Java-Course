/**
 * Абстрактний клас, що частково реалізує інтерфейс CostCalculationStrategy (Рівень 2).
 * Інкапсулює спільну для більшості стратегій логіку валідації та розрахунку базової вартості.
 */
public abstract class AbstractCostStrategy implements CostCalculationStrategy {

    /**
     * Загальна валідація ставки. У разі некоректного значення викидає IllegalArgumentException.
     *
     * @param hourlyRate вартість години
     */
    protected void validateRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Годинна ставка має бути строго більшою за 0");
        }
    }

    /**
     * Спільний метод розрахунку базової вартості (години * ставка).
     *
     * @param task       завдання
     * @param hourlyRate годинна ставка
     * @return базова вартість без коефіцієнтів та знижок
     */
    protected double getBaseCost(Task task, double hourlyRate) {
        validateRate(hourlyRate);
        return task.getEstimatedHours() * hourlyRate;
    }
}
