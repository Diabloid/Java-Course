// Друга доменна сутність проєкту для перевірки універсальності InMemoryCrudService (ЛР 7-9: Рівень 1)
public class Project extends BaseEntity {
    private String clientName;
    private double budget;

    public Project(long id, String name, String clientName, double budget) {
        super(id, name);
        if (budget < 0) {
            throw new IllegalArgumentException("Бюджет проєкту не може бути від'ємним");
        }
        this.clientName = (clientName == null || clientName.isBlank()) ? "Внутрішній клієнт" : clientName.trim();
        this.budget = budget;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        if (budget < 0) {
            throw new IllegalArgumentException("Бюджет проєкту не може бути від'ємним");
        }
        this.budget = budget;
    }

    @Override
    public String toString() {
        return String.format("[Проєкт ID: %d] '%s' | Клієнт: %s | Бюджет: $%.2f", 
                getId(), getName(), clientName, budget);
    }
}
