import java.util.Objects;

// Проміжний рівень ієрархії: завдання розробки ПЗ (ЛР 6: Рівень 1-3, sealed hierarchy)
public abstract sealed class DevelopmentTask extends Task permits BugFixTask, FeatureTask {
    
    private String repositoryUrl;

    // Перевантажений конструктор з делегуванням через this(...) (Рівень 2)
    public DevelopmentTask(String name, String assignee, int priority, double estimatedHours, String repositoryUrl)
            throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, repositoryUrl, new PriorityCostStrategy());
    }

    // Повний конструктор
    public DevelopmentTask(String name, String assignee, int priority, double estimatedHours, 
                           String repositoryUrl, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(name, assignee, priority, estimatedHours, costStrategy);
        
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

    // Перевизначення toString() із використанням super.toString() (Рівень 1)
    @Override
    public String toString() {
        return super.toString() + " | Репозиторій: " + repositoryUrl;
    }

    // Перевизначення equals() та hashCode() з викликом super.equals() (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        DevelopmentTask other = (DevelopmentTask) obj;
        return Objects.equals(this.repositoryUrl, other.repositoryUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), repositoryUrl);
    }
}
