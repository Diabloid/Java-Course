public class Task {
    private String name;
    private String assignee;
    private int priority;
    private double estimatedHours;

    // Сигнатура методу попереджає, що конструктор може викинути ці помилки
    public Task(String name, String assignee, int priority, double estimatedHours) 
           throws InvalidPriorityException, InvalidTimeException {
        
        // Валідація пріоритету (Рівень 2)
        if (priority < 1 || priority > 3) {
            throw new InvalidPriorityException("Пріоритет має бути від 1 до 3", priority);
        }
        
        // Валідація часу
        if (estimatedHours <= 0) {
            throw new InvalidTimeException("Оцінений час має бути більшим за 0", estimatedHours);
        }

        this.name = name;
        this.assignee = assignee;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
    }

    // Геттери (щоб читати значення з інших класів)
    public int getPriority() {
        return priority;
    }
    
    public double getEstimatedHours() {
        return estimatedHours;
    }
    
    @Override
    public String toString() {
        return String.format("Завдання: '%s' | Виконавець: %s | Пріоритет: %d | Час: %.1f год", 
                             name, assignee, priority, estimatedHours);
    }

    // Перевизначення equals() для пошуку цілого об'єкта (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        // Якщо посилання вказують на один об'єкт у пам'яті
        if (this == obj) return true;
        
        // Якщо передано null або об'єкт іншого класу
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Приведення типу до Task
        Task other = (Task) obj;
        
        // Вважаємо завдання однаковими, якщо збігаються назва, виконавець і пріоритет.
        // (Години можуть коригуватися, але суть завдання від цього не змінюється).
        return this.priority == other.priority &&
               this.name.equals(other.name) &&
               this.assignee.equals(other.assignee);
    }
}