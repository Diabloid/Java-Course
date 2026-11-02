import java.util.Objects;

// Проміжний рівень ієрархії: завдання розробки ПЗ (ЛР 6: sealed, ЛР 7-9: CRUD support)
public abstract sealed class DevelopmentTask extends Task permits BugFixTask, FeatureTask {
    
    private String repositoryUrl;

    // Конструктор з автогенерацією ID
    public DevelopmentTask(String name, String assignee, int priority, double estimatedHours, String repositoryUrl)
            throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), name, assignee, priority, estimatedHours, repositoryUrl, new PriorityCostStrategy());
    }

    // Конструктор з явним ID (ЛР 7-9)
    public DevelopmentTask(long id, String name, String assignee, int priority, double estimatedHours, String repositoryUrl)
            throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, repositoryUrl, new PriorityCostStrategy());
    }

    // Повний конструктор
    public DevelopmentTask(long id, String name, String assignee, int priority, double estimatedHours, 
                           String repositoryUrl, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(id, name, assignee, priority, estimatedHours, costStrategy);
        
        if (enableConstructorLogging) {
            System.out.println("   [Constructor DevelopmentTask] 2. Ініціалізація DevelopmentTask: repo='" + repositoryUrl + "'");
        }

        this.repositoryUrl = (repositoryUrl == null || repositoryUrl.isBlank()) 
                             ? "https://github.com/vanya/repo-default" 
                             : repositoryUrl;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    @Override
    public String toString() {
        return super.toString() + " | Репозиторій: " + repositoryUrl;
    }
}
