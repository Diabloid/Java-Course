/**
 * Інтерфейс стратегії розрахунку вартості виконання завдання (Паттерн Strategy).
 * Визначає контракт поведінки ціноутворення, яка може варіюватися для різних типів контрактів чи завдань.
 */
public interface CostCalculationStrategy {

    /**
     * Розраховує підсумкову вартість завдання на основі заданої погодинної ставки.
     *
     * @param task       об'єкт завдання
     * @param hourlyRate базова вартість однієї години роботи
     * @return розрахована вартість
     */
    double calculateCost(Task task, double hourlyRate);

    /**
     * Повертає назву стратегії для логування та звітів.
     *
     * @return коротка назва стратегії
     */
    String getStrategyName();

    /**
     * Default-метод (Рівень 2): повертає стандартизований опис розрахунку.
     * Може бути перевизначений конкретною стратегією за потреби.
     *
     * @param task       об'єкт завдання
     * @param hourlyRate базова вартість години
     * @return форматований текстовий опис
     */
    default String getDetailedDescription(Task task, double hourlyRate) {
        return String.format("[%s] Підсумкова вартість для '%s': $%.2f (базова ставка $%.2f/год)",
                getStrategyName(), task.getName(), calculateCost(task, hourlyRate), hourlyRate);
    }
}
