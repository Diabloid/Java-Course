public class Task {
    // Поля різних типів (Рівень 1)
    private String name;
    private String assignee;
    private int priority;
    private double estimatedHours;

    // Конструктор (Рівень 1)
    public Task(String name, String assignee, int priority, double estimatedHours) {
        this.name = name;
        this.assignee = assignee;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
    }

    public int getPriority() {
        return priority;
    }
    
    public double getEstimatedHours() {
        return estimatedHours;
    }

    // Перевизначення toString() (Рівень 1)
    @Override
    public String toString() {
        return String.format("Завдання: '%s' | Виконавець: %s | Пріоритет: %d | Час: %.1f год", 
                             name, assignee, priority, estimatedHours);
    }

    // Перевизначення equals() для пошуку цілого об'єкта (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Task other = (Task) obj;
        
        return this.priority == other.priority &&
               this.name.equals(other.name) &&
               this.assignee.equals(other.assignee);
    }
}