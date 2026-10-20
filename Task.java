import java.util.Objects;

public class Task {
    private String name;
    private String assignee;
    private int priority;
    private double estimatedHours;

    // Конструктор з валідацією параметрів (ЛР 2 та ЛР 3)
    public Task(String name, String assignee, int priority, double estimatedHours) 
           throws InvalidPriorityException, InvalidTimeException {
        
        // Валідація пріоритету (ЛР 3: 1 - Високий, 2 - Середній, 3 - Низький)
        if (priority < 1 || priority > 3) {
            throw new InvalidPriorityException("Пріоритет має бути від 1 до 3", priority);
        }
        
        // Валідація часу (ЛР 3: час виконання повинен бути строго більше нуля)
        if (estimatedHours <= 0) {
            throw new InvalidTimeException("Оцінений час має бути більшим за 0", estimatedHours);
        }

        this.name = name;
        this.assignee = assignee;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
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

    // Отримання мітки пріоритету (бізнес-логіка з ЛР 1)
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

    // Розрахунок вартості завдання з урахуванням коефіцієнта терміновості (бізнес-логіка з ЛР 1)
    public double calculateBaseCost(double hourlyRate) {
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Годинна ставка не може бути від'ємною");
        }
        return estimatedHours * hourlyRate;
    }

    public double calculateFinalCost(double hourlyRate) {
        double baseCost = calculateBaseCost(hourlyRate);
        switch (priority) {
            case 1:
                return baseCost * 1.20; // 20% націнки за терміновість
            case 2:
                return baseCost;        // Стандартна вартість
            case 3:
                return baseCost * 0.90; // 10% знижки за нетерміновість
            default:
                return baseCost;
        }
    }

    // Перевизначення toString() (ЛР 1-3)
    @Override
    public String toString() {
        return String.format("Завдання: '%s' | Виконавець: %s | Пріоритет: %d (%s) | Час: %.1f год", 
                             name, assignee, priority, getPriorityLabel(), estimatedHours);
    }

    // Перевизначення equals() для пошуку цілого об'єкта (ЛР 2 та ЛР 3)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Task other = (Task) obj;
        
        // Вважаємо завдання однаковими, якщо збігаються назва, виконавець і пріоритет.
        return this.priority == other.priority &&
               Objects.equals(this.name, other.name) &&
               Objects.equals(this.assignee, other.assignee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, assignee, priority);
    }
}