import java.util.Objects;

// 3-й рівень ієрархії: виправлення дефекту (ЛР 6: final, ЛР 7-9: CRUD support)
public final class BugFixTask extends DevelopmentTask {

    private String severity;
    private boolean isHotfix;

    // Конструктори без явного ID (зворотна сумісність)
    public BugFixTask(String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity)
            throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), name, assignee, priority, estimatedHours, repositoryUrl, severity, false);
    }

    public BugFixTask(String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity, boolean isHotfix)
            throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), name, assignee, priority, estimatedHours, repositoryUrl, severity, isHotfix, new PriorityCostStrategy());
    }

    // Конструктори з явним ID (ЛР 7-9)
    public BugFixTask(long id, String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity)
            throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, repositoryUrl, severity, false);
    }

    public BugFixTask(long id, String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity, boolean isHotfix)
            throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, repositoryUrl, severity, isHotfix, new PriorityCostStrategy());
    }

    // Повний конструктор
    public BugFixTask(long id, String name, String assignee, int priority, double estimatedHours, 
                      String repositoryUrl, String severity, boolean isHotfix, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(id, name, assignee, priority, estimatedHours, repositoryUrl, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor BugFixTask] 3. Ініціалізація BugFixTask: severity='" + severity + "', hotfix=" + isHotfix);
        }

        this.severity = (severity == null || severity.isBlank()) ? "NORMAL" : severity;
        this.isHotfix = isHotfix;
    }

    @Override
    public String getTaskType() {
        return "Bug Fix";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Виправлення дефекту [%s] (Hotfix: %s)", severity, isHotfix ? "ТАК" : "НІ");
    }

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

    @Override
    public String toString() {
        return super.toString() + String.format(" | Критичність: %s | Hotfix: %s", severity, isHotfix ? "ТАК" : "НІ");
    }
}
