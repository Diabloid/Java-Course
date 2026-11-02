import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

// Базовий абстрактний запечатаний клас завдання (ЛР 6: sealed hierarchy, ЛР 7-9: extends BaseEntity)
public abstract sealed class Task extends BaseEntity implements Reportable, Comparable<Task> 
       permits DevelopmentTask, DocumentationTask, LegacyTaskAdapter {
    
    // Автоінкремент для зворотної сумісності (якщо id не передано явно)
    private static long idCounter = 1;
    public static synchronized long getNextAutoId() {
        return idCounter++;
    }

    // Статичний прапорець для демонстрації ланцюжка викликів конструкторів (ЛР 6)
    public static boolean enableConstructorLogging = false;

    private String assignee;
    private int priority;
    private double estimatedHours;
    private String status = "NEW";

    // Паттерн Strategy (композиція: Task "має" стратегію, а не "є" нею)
    private CostCalculationStrategy costStrategy;

    // Паттерн Observer: список підписників
    private final List<TaskObserver> observers = new ArrayList<>();

    // Перевантажені конструктори з автогенерацією ID (для зворотної сумісності з ЛР 1-6)
    public Task(String name, String assignee, int priority, double estimatedHours) 
           throws InvalidPriorityException, InvalidTimeException {
        this(getNextAutoId(), name, assignee, priority, estimatedHours, new PriorityCostStrategy());
    }

    public Task(String name, String assignee, int priority, double estimatedHours, CostCalculationStrategy costStrategy)
           throws InvalidPriorityException, InvalidTimeException {
        this(getNextAutoId(), name, assignee, priority, estimatedHours, costStrategy);
    }

    // Конструктор із явним ID (ЛР 7-9)
    public Task(long id, String name, String assignee, int priority, double estimatedHours) 
           throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, new PriorityCostStrategy());
    }

    // Базовий конструктор із повною ініціалізацією
    public Task(long id, String name, String assignee, int priority, double estimatedHours, CostCalculationStrategy costStrategy)
           throws InvalidPriorityException, InvalidTimeException {
        
        super(id, name); // Ініціалізація BaseEntity (id, name)

        if (enableConstructorLogging) {
            System.out.println("   [Constructor Task] 1. Ініціалізація Task: ID=" + id + ", name='" + name + "'");
        }

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

        this.assignee = assignee;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
        this.costStrategy = costStrategy;
    }

    // Абстрактні методи (ЛР 6)
    public abstract String getTaskType();
    public abstract String getExecutionDetails();

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

    // Управління спостерігачами (Паттерн Observer)
    public final void attach(TaskObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public final void detach(TaskObserver observer) {
        observers.remove(observer);
    }

    protected final void notifyObservers(String changeType, String details) {
        for (TaskObserver observer : observers) {
            observer.onTaskUpdated(this, changeType, details);
        }
    }

    // Зміна статусу завдання зі сповіщенням підписників
    public void setStatus(String status) {
        this.status = status;
        notifyObservers("STATUS_CHANGED", "Новий статус: " + status);
    }

    // Зміна пріоритету зі сповіщенням підписників
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

    // Зміна стратегії в runtime
    public void setCostStrategy(CostCalculationStrategy costStrategy) {
        if (costStrategy == null) {
            throw new IllegalArgumentException("Стратегія розрахунку вартості не може бути null");
        }
        this.costStrategy = costStrategy;
        notifyObservers("STRATEGY_CHANGED", "Встановлено стратегію: " + costStrategy.getStrategyName());
    }

    // final метод: підкласи не повинні порушувати делегування розрахунку вартості
    public final double calculateCost(double hourlyRate) {
        return this.costStrategy.calculateCost(this, hourlyRate);
    }

    // Текстовий опис пріоритету
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
        return String.format("[ID: %d | %s] '%s' | Статус: %s | Виконавець: %s | Час: %.1f год | Пріоритет: %s | Деталі: %s",
                getId(), getTaskType().toUpperCase(), getName(), status, assignee, estimatedHours, getPriorityLabel(), getExecutionDetails());
    }

    // Реалізація Comparable<Task> за назвою (природний порядок сортування за алфавітом)
    @Override
    public int compareTo(Task other) {
        if (other == null) return 1;
        // Порівнюємо за назвою, що узгоджується з findAllSortedByName()
        int nameCompare = this.getName().compareTo(other.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        return Long.compare(this.getId(), other.getId());
    }

    // Перевизначення toString()
    @Override
    public String toString() {
        return String.format("[ID: %d | %s] '%s' | Статус: %s | Виконавець: %s | Пріоритет: %d (%s) | Час: %.1f год | Стратегія: %s", 
                             getId(), getTaskType(), getName(), status, assignee, priority, getPriorityLabel(), estimatedHours, costStrategy.getStrategyName());
    }
}
