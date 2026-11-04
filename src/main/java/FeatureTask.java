import java.util.Objects;

// 3-й рівень ієрархії: розробка нової функціональності (ЛР 6: final, ЛР 7-9: CRUD support)
public final class FeatureTask extends DevelopmentTask {

    private String moduleName;
    private int storyPoints;

    // Конструктори без явного ID (зворотна сумісність)
    public FeatureTask(String name, String assignee, int priority, double estimatedHours, 
                       String repositoryUrl, String moduleName, int storyPoints)
            throws InvalidPriorityException, InvalidTimeException {
        this(Task.getNextAutoId(), name, assignee, priority, estimatedHours, repositoryUrl, moduleName, storyPoints, new PriorityCostStrategy());
    }

    // Конструктори з явним ID (ЛР 7-9)
    public FeatureTask(long id, String name, String assignee, int priority, double estimatedHours, 
                       String repositoryUrl, String moduleName, int storyPoints)
            throws InvalidPriorityException, InvalidTimeException {
        this(id, name, assignee, priority, estimatedHours, repositoryUrl, moduleName, storyPoints, new PriorityCostStrategy());
    }

    // Повний конструктор
    public FeatureTask(long id, String name, String assignee, int priority, double estimatedHours, 
                       String repositoryUrl, String moduleName, int storyPoints, CostCalculationStrategy costStrategy)
            throws InvalidPriorityException, InvalidTimeException {
        super(id, name, assignee, priority, estimatedHours, repositoryUrl, costStrategy);

        if (enableConstructorLogging) {
            System.out.println("   [Constructor FeatureTask] 3. Ініціалізація FeatureTask: module='" + moduleName + "', SP=" + storyPoints);
        }

        this.moduleName = (moduleName == null || moduleName.isBlank()) ? "General" : moduleName;
        this.storyPoints = Math.max(1, storyPoints);
    }

    @Override
    public String getTaskType() {
        return "New Feature";
    }

    @Override
    public String getExecutionDetails() {
        return String.format("Розробка нового функціоналу для модуля '%s' (Оцінка: %d Story Points)", moduleName, storyPoints);
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = Math.max(1, storyPoints);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Модуль: %s | SP: %d", moduleName, storyPoints);
    }
}
