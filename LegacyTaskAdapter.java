// Адаптер: адаптує LegacyExternalTask до доменної моделі Task (ЛР 5: Adapter, ЛР 6: final sealed, ЛР 7-9: BaseEntity)
public final class LegacyTaskAdapter extends Task {
    private final LegacyExternalTask legacyTask;

    public LegacyTaskAdapter(LegacyExternalTask legacyTask) 
           throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), legacyTask);
    }

    public LegacyTaskAdapter(long id, LegacyExternalTask legacyTask) 
           throws InvalidPriorityException, InvalidTimeException {
        super(
            id,
            legacyTask != null ? legacyTask.getTitle() : "Без назви",
            legacyTask != null ? legacyTask.getDeveloperEmail() : "Не призначено",
            mapUrgencyToPriority(legacyTask != null ? legacyTask.getUrgencyLevel() : "NORMAL"),
            legacyTask != null ? legacyTask.getDurationInMinutes() / 60.0 : 1.0,
            new PriorityCostStrategy()
        );
        this.legacyTask = legacyTask;
    }

    @Override
    public String getTaskType() {
        return "External Legacy Task";
    }

    @Override
    public String getExecutionDetails() {
        return "Адаптоване зовнішнє завдання зі старої системи (LegacyExternalTask)";
    }

    private static int mapUrgencyToPriority(String urgency) {
        if (urgency == null) return 2;
        switch (urgency.trim().toUpperCase()) {
            case "CRITICAL":
            case "HIGH":
                return 1;
            case "NORMAL":
            case "MEDIUM":
                return 2;
            case "LOW":
                return 3;
            default:
                return 2;
        }
    }

    public LegacyExternalTask getLegacyTask() {
        return legacyTask;
    }
}
