import java.util.Objects;

// 2-й рівень ієрархії: завдання з написання документації (ЛР 6: Рівень 1-3, термінальний final клас)
public final class DocumentationTask extends Task {

    private String docFormat;
    private String targetAudience;

    // Перевантажений конструктор з делегуванням через this(...) (Рівень 2)
    public DocumentationTask(String name, String assignee, int priority, double estimatedHours, 
                             String docFormat, String targetAudience)
            throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, docFormat, targetAudience, new PriorityCostStrategy());
    }

    // Повний конструктор
    public DocumentationTask(String name, String assignee, int priority, double estimatedHours, 
                             String docFormat, String targetAudience, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(name, assignee, priority, estimatedHours, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor DocumentationTask] 2. Ініціалізація DocumentationTask: format='" + docFormat + "', audience='" + targetAudience + "'");
        }

        this.docFormat = (docFormat == null || docFormat.isBlank()) ? "Markdown" : docFormat;
        this.targetAudience = (targetAudience == null || targetAudience.isBlank()) ? "Developers" : targetAudience;
    }

    // Реалізація абстрактних методів (Рівень 2)
    @Override
    public String getTaskType() {
        return "Documentation";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Створення технічної документації у форматі %s для аудиторії '%s'", docFormat, targetAudience);
    }

    // Специфічні методи
    public String getDocFormat() {
        return docFormat;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    // Перевизначення toString() із використанням super.toString() (Рівень 1)
    @Override
    public String toString() {
        return super.toString() + String.format(" | Формат: %s | Цільова аудиторія: %s", docFormat, targetAudience);
    }

    // Перевизначення equals() та hashCode() (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        DocumentationTask other = (DocumentationTask) obj;
        return Objects.equals(this.docFormat, other.docFormat) &&
               Objects.equals(this.targetAudience, other.targetAudience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), docFormat, targetAudience);
    }
}
