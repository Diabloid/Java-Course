// Інтерфейс спостерігача за змінами завдання (Рівень 2 - Observer)
public interface TaskObserver {
    void onTaskUpdated(Task task, String changeType, String details);
}
