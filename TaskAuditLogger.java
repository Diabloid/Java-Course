// Спостерігач: система аудиту та логування подій (Рівень 2 - Concrete Observer 1)
public class TaskAuditLogger implements TaskObserver {

    @Override
    public void onTaskUpdated(Task task, String changeType, String details) {
        System.out.printf("   [AUDIT LOG] Завдання '%s' | Подія: %s | Інфо: %s\n",
                task.getName(), changeType, details);
    }
}
