// Спостерігач: система екстрених сповіщень для термінових завдань (Рівень 2 - Concrete Observer 2)
public class TaskAlertNotifier implements TaskObserver {

    @Override
    public void onTaskUpdated(Task task, String changeType, String details) {
        // Сповіщення надсилається лише для термінових завдань (пріоритет 1)
        if (task.getPriority() == 1) {
            System.out.printf("   🚨 [ALERT NOTIFIER] УВАГА! Змінено термінове завдання '%s' (Виконавець: %s)! Подія: %s (%s)\n",
                    task.getName(), task.getAssignee(), changeType, details);
        }
    }
}
