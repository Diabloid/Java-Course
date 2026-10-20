import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

// Доменна сутність завдання (Суб'єкт для паттерна Observer, Рівень 2)
public class Task implements Reportable, Comparable<Task> {
    private String name;
    private String assignee;
    private int priority;
    private double estimatedHours;
    private String status = "NEW";

    // Паттерн Strategy (ЛР 4)
    private CostCalculationStrategy costStrategy;

    // Паттерн Observer: список підписників (Рівень 2)
    private final List<TaskObserver> observers = new ArrayList<>();

    // Базовий конструктор (ЛР 2-3)
    public Task(String name, String assignee, int priority, double estimatedHours) 
           throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, new PriorityCostStrategy());
    }

    // Розширений конструктор зі стратегією (ЛР 4)
    public Task(String name, String assignee, int priority, double estimatedHours, CostCalculationStrategy costStrategy)
           throws InvalidPriorityException, InvalidTimeException {
        
        // Валідація пріоритету (ЛР 3)
        if (priority < 1 || priority > 3) {
            throw new InvalidPriorityException("Пріоритет має бути від 1 до 3", priority);
        }
        
        // Валідація часу (ЛР 3)
        if (estimatedHours <= 0) {
            throw new InvalidTimeException("Оцінений час має бути більшим за 0", estimatedHours);
        }

        if (costStrategy == null) {
            throw new IllegalArgumentException("Стратегія розрахунку вартості не може бути null");
        }

        this.name = name;
        this.assignee = assignee;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
        this.costStrategy = costStrategy;
    }

    // Геттери
    public String getName() {
        return name;
    }

    public String getAssignee() {
        return assignee;
    }

    public int getPriority() {
        return priority;
    }
    
    public double getEstimatedHours() {
        return estimatedHours;
    }

    public String getStatus() {
        return status;
    }

    // Управління спостерігачами (Паттерн Observer - Рівень 2)
    public void attach(TaskObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(TaskObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String changeType, String details) {
        for (TaskObserver observer : observers) {
            observer.onTaskUpdated(this, changeType, details);
        }
    }

    // Зміна статусу завдання зі сповіщенням підписників (Рівень 2)
    public void setStatus(String status) {
        this.status = status;
        notifyObservers("STATUS_CHANGED", "Новий статус: " + status);
    }

    // Зміна пріоритету зі сповіщенням підписників (Рівень 2)
    public void setPriority(int priority) throws InvalidPriorityException {
        if (priority < 1 || priority > 3) {
            throw new InvalidPriorityException("Пріоритет має бути від 1 до 3", priority);
        }
        this.priority = priority;
        notifyObservers("PRIORITY_CHANGED", "Новий пріоритет: " + priority + " (" + getPriorityLabel() + ")");
    }

    public CostCalculationStrategy getCostStrategy() {
        return costStrategy;
    }

    // Зміна стратегії в runtime зі сповіщенням підписників (ЛР 4 та ЛР 5)
    public void setCostStrategy(CostCalculationStrategy costStrategy) {
        if (costStrategy == null) {
            throw new IllegalArgumentException("Стратегія розрахунку вартості не може бути null");
        }
        this.costStrategy = costStrategy;
        notifyObservers("STRATEGY_CHANGED", "Встановлено стратегію: " + costStrategy.getStrategyName());
    }

    // Делегування розрахунку вартості стратегії (ЛР 4)
    public double calculateCost(double hourlyRate) {
        return this.costStrategy.calculateCost(this, hourlyRate);
    }

    // Текстовий опис пріоритету (з ЛР 1)
    public String getPriorityLabel() {
        switch (priority) {
            case 1:
                return "Високий (Терміново)";
            case 2:
                return "Середній (Стандарт)";
            case 3:
                return "Низький (Не поспішаючи)";
            default:
                return "Невідомий";
        }
    }

    public double calculateBaseCost(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Годинна ставка має бути більшою за 0");
        }
        return estimatedHours * hourlyRate;
    }

    public double calculateFinalCost(double hourlyRate) {
        return calculateCost(hourlyRate);
    }

    // Реалізація Reportable (ЛР 4)
    @Override
    public String generateSummary() {
        return String.format("[ЗВІТ ЗАВДАННЯ] '%s' | Статус: %s | Виконавець: %s | Час: %.1f год | Пріоритет: %s | Стратегія: %s",
                name, status, assignee, estimatedHours, getPriorityLabel(), costStrategy.getStrategyName());
    }

    // Реалізація Comparable<Task> за часом (ЛР 4)
    @Override
    public int compareTo(Task other) {
        if (other == null) {
            return 1;
        }
        return Double.compare(this.estimatedHours, other.estimatedHours);
    }

    // Перевизначення toString()
    @Override
    public String toString() {
        return String.format("Завдання: '%s' | Статус: %s | Виконавець: %s | Пріоритет: %d (%s) | Час: %.1f год | Стратегія: %s", 
                             name, status, assignee, priority, getPriorityLabel(), estimatedHours, costStrategy.getStrategyName());
    }

    // Перевизначення equals() (ЛР 2-3)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Task other = (Task) obj;
        
        return this.priority == other.priority &&
               Objects.equals(this.name, other.name) &&
               Objects.equals(this.assignee, other.assignee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, assignee, priority);
    }
}