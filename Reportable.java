/**
 * Інтерфейс для сутностей, здатних формувати форматовані аналітичні звіти.
 * Використовується для демонстрації множинної реалізації інтерфейсів (Рівень 2).
 */
public interface Reportable {

    /**
     * Генерує короткий звіт про сутність.
     *
     * @return текстовий звіт
     */
    String generateSummary();

    /**
     * Default-метод для виведення звіту в консоль.
     */
    default void printReport() {
        System.out.println(generateSummary());
    }
}
