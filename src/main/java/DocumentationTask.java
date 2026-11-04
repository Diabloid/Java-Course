import java.util.Objects;

// 2-й рівень ієрархії: завдання з написання документації (ЛР 6: final, ЛР 7-9: CRUD support)
public final class DocumentationTask extends Task {

    private String docFormat;
    private String targetAudience;

    // Конструктор без явного ID (зворотна сумісність)
    public DocumentationTask(String name, String assignee, int priority, double estimatedHours, 
                             String docFormat, String targetAudience)
            throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), name, assignee, priority, estimatedHours, docFormat, targetAudience, new PriorityCostStrategy());
    }

    // Конструктор з явним ID (ЛР 7-9)
    public DocumentationTask(long id, String name, String assignee, int priority, double estimatedHours, 
                             String docFormat, String targetAudience)
            throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, docFormat, targetAudience, new PriorityCostStrategy());
    }

    // Повний конструктор
    public DocumentationTask(long id, String name, String assignee, int priority, double estimatedHours, 
                             String docFormat, String targetAudience, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(id, name, assignee, priority, estimatedHours, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor DocumentationTask] 2. Ініціалізація DocumentationTask: format='" + docFormat + "', audience='" + targetAudience + "'");
        }

        this.docFormat = (docFormat == null || docFormat.isBlank()) ? "Markdown" : docFormat;
        this.targetAudience = (targetAudience == null || targetAudience.isBlank()) ? "Developers" : targetAudience;
    }

    @Override
    public String getTaskType() {
        return "Documentation";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Створення технічної документації у форматі %s для аудиторії '%s'", docFormat, targetAudience);
    }

    public String getDocFormat() {
        return docFormat;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Формат: %s | Цільова аудиторія: %s", docFormat, targetAudience);
    }
}
