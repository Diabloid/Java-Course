// Зовнішній або застарілий клас із несумісним API (Рівень 3 - Adaptee)
public class LegacyExternalTask {
    private String title;
    private String developerEmail;
    private int durationInMinutes;
    private String urgencyLevel; // "CRITICAL", "NORMAL", "LOW"

    public LegacyExternalTask(String title, String developerEmail, int durationInMinutes, String urgencyLevel) {
        this.title = title;
        this.developerEmail = developerEmail;
        this.durationInMinutes = durationInMinutes;
        this.urgencyLevel = urgencyLevel;
    }

    public String getTitle() {
        return title;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }
}
