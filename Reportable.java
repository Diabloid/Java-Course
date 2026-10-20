// Інтерфейс для генерації звітів (Рівень 2)
public interface Reportable {

    String generateSummary();

    // Default-метод для виведення звіту
    default void printReport() {
        System.out.println(generateSummary());
    }
}
