import java.util.Scanner;
import java.util.Locale;

public class TaskTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("=== Трекер Завдань (CLI Версія) ===");

        // 1. Введення даних (String)
        System.out.print("Введіть назву завдання: ");
        String taskName = scanner.nextLine();

        System.out.print("Введіть виконавця (наприклад, Ваня): ");
        String assignee = scanner.nextLine();

        // 2. Введення даних (int)
        System.out.print("Введіть пріоритет (1 - Високий, 2 - Середній, 3 - Низький): ");
        int priority = scanner.nextInt();

        // 3. Введення даних (double)
        System.out.print("Введіть оцінений час виконання (в годинах, наприклад 2.5): ");
        double estimatedHours = scanner.nextDouble();

        System.out.print("Введіть оціночну вартість однієї години (в $): ");
        double hourlyRate = scanner.nextDouble();

        // 4. Змістовне обчислення та оператори розгалуження (if...else)
        double baseCost = estimatedHours * hourlyRate;
        double finalCost;
        String priorityLabel;

        // Якщо завдання має найвищий пріоритет (1), додаємо 20% "націнки" за терміновість
        if (priority == 1) {
            finalCost = baseCost * 1.20;
            priorityLabel = "Високий (Терміново)";
        } else if (priority == 2) {
            finalCost = baseCost;
            priorityLabel = "Середній (Стандарт)";
        } else {
            finalCost = baseCost * 0.90; 
            priorityLabel = "Низький (Не поспішаючи)";
        }

        System.out.println("\n==================================");
        System.out.println("      ДЕТАЛІ ЗАВДАННЯ");
        System.out.println("==================================");
        System.out.printf("Назва завдання : %s\n", taskName);
        System.out.printf("Виконавець     : %s\n", assignee);
        System.out.printf("Пріоритет      : %s\n", priorityLabel);
        System.out.printf("Оцінка часу    : %.2f год.\n", estimatedHours);
        System.out.printf("Базова вартість: $%.2f\n", baseCost);
        System.out.printf("Фінальна сума  : $%.2f (з урахуванням пріоритету)\n", finalCost);
        System.out.println("==================================");

        scanner.close();
    }
}