import java.util.Objects;

// 3-й рівень ієрархії: розробка нової функціональності (ЛР 6: Рівень 1-3, термінальний final клас)
public final class FeatureTask extends DevelopmentTask {

    private String moduleName;
    private int storyPoints;

    // Перевантажений конструктор з делегуванням (Рівень 2)
    public FeatureTask(String name, String assignee, int priority, double estimatedHours, 
                       String repositoryUrl, String moduleName, int storyPoints)
            throws InvalidPriorityException, InvalidTimeException {
        this(name, assignee, priority, estimatedHours, repositoryUrl, moduleName, storyPoints, new PriorityCostStrategy());
    }

    // Повний конструктор
    public FeatureTask(String name, String assignee, int priority, double estimatedHours, 
                       String repositoryUrl, String moduleName, int storyPoints, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(name, assignee, priority, estimatedHours, repositoryUrl, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor FeatureTask] 3. Ініціалізація FeatureTask: module='" + moduleName + "', SP=" + storyPoints);
        }

        this.moduleName = (moduleName == null || moduleName.isBlank()) ? "General" : moduleName;
        this.storyPoints = Math.max(1, storyPoints);
    }

    // Реалізація абстрактних методів (Рівень 2)
    @Override
    public String getTaskType() {
        return "New Feature";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Розробка нового функціоналу для модуля '%s' (Оцінка: %d Story Points)", moduleName, storyPoints);
    }

    // Специфічні методи
    public String getModuleName() {
        return moduleName;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = Math.max(1, storyPoints);
    }

    // Перевизначення toString() із викликом super.toString() (Рівень 1)
    @Override
    public String toString() {
        return super.toString() + String.format(" | Модуль: %s | SP: %d", moduleName, storyPoints);
    }

    // Перевизначення equals() та hashCode() (Рівень 3)
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        FeatureTask other = (FeatureTask) obj;
        return this.storyPoints == other.storyPoints &&
               Objects.equals(this.moduleName, other.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), moduleName, storyPoints);
    }
}
