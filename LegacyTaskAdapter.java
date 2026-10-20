// Адаптер: адаптує LegacyExternalTask до доменної моделі Task (Рівень 3 - Adapter)
public class LegacyTaskAdapter extends Task {
    private final LegacyExternalTask legacyTask;

    public LegacyTaskAdapter(LegacyExternalTask legacyTask) 
           throws InvalidPriorityException, InvalidTimeException {
        super(
            legacyTask != null ? legacyTask.getTitle() : "Без назви",
            legacyTask != null ? legacyTask.getDeveloperEmail() : "Не призначено",
            mapUrgencyToPriority(legacyTask != null ? legacyTask.getUrgencyLevel() : "NORMAL"),
            legacyTask != null ? legacyTask.getDurationInMinutes() / 60.0 : 1.0
        );
        this.legacyTask = legacyTask;
    }

    // Трансляція текстового рівня критичності в цілочисельний пріоритет (1-3)
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
