import java.util.Objects;

// 3-й рівень ієрархії: виправлення дефекту (ЛР 6: Рівень 1-3, термінальний final клас)
public final class BugFixTask extends DevelopmentTask {

    private String severity;
    private boolean isHotfix;

    // Перевантажений конструктор 1: базовий дефект
    public BugFixTask(String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity)
            throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, repositoryUrl, severity, false);
    }

    // Перевантажений конструктор 2: з прапорцем hotfix
    public BugFixTask(String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity, boolean isHotfix)
            throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, repositoryUrl, severity, isHotfix, new PriorityCostStrategy());
    }

    // Повний конструктор
    public BugFixTask(String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity, boolean isHotfix, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(name, assignee, priority, estimatedHours, repositoryUrl, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor BugFixTask] 3. Ініціалізація BugFixTask: severity='" + severity + "', hotfix=" + isHotfix);
        }

        this.severity = (severity == null || severity.isBlank()) ? "NORMAL" : severity;
        this.isHotfix = isHotfix;
    }

    // Реалізація абстрактних методів (Рівень 2)
    @Override
    public String getTaskType() {
        return "Bug Fix";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Виправлення дефекту [%s] (Hotfix: %s)", severity, isHotfix ? "ТАК" : "НІ");
    }

    // Специфічні методи підкласу (для тестування downcasting та pattern matching)
    public String getSeverity() {
        return severity;
    }

    public boolean isHotfix() {
        return isHotfix;
    }

    public void markAsHotfix() {
        this.isHotfix = true;
        notifyObservers("HOTFIX_APPLIED", "Баг переведено в статус термінового хотфіксу!");
    }

    // Перевизначення toString() із викликом super.toString() (Рівень 1)
    @Override
    public String toString() {
        return super.toString() + String.format(" | Критичність: %s | Hotfix: %s", severity, isHotfix ? "ТАК" : "НІ");
    }

    // Перевизначення equals() та hashCode() з викликом super.equals() (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        BugFixTask other = (BugFixTask) obj;
        return this.isHotfix == other.isHotfix &&
               Objects.equals(this.severity, other.severity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), severity, isHotfix);
    }
}
