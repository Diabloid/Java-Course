import java.util.Scanner;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "cp1251").useLocale(Locale.US);

        System.out.println("=== Трекер Завдань (ЛР 2) ===");
        System.out.print("Скільки завдань ви хочете створити? ");
        int count = scanner.nextInt();
        scanner.nextLine();

        Task[] tasks = new Task[count];

        // Заповнення масиву через цикл for (Рівень 1)
        for (int i = 0; i < tasks.length; i++) {
            System.out.println("\n--- Введення завдання #" + (i + 1) + " ---");
            
            System.out.print("Назва: ");
            String name = scanner.nextLine();
            
            System.out.print("Виконавець: ");
            String assignee = scanner.nextLine();
            
            System.out.print("Пріоритет (1-Високий, 2-Середній, 3-Низький): ");
            int priority = scanner.nextInt();
            
            System.out.print("Оцінений час (год): ");
            double hours = scanner.nextDouble();
            scanner.nextLine();

            tasks[i] = new Task(name, assignee, priority, hours);
        }

        System.out.println("\n=== Введений масив завдань (до сортування) ===");
        // Виведення через for-each (Рівень 1)
        for (Task t : tasks) {
            System.out.println(t.toString());
        }

        // Підбиття підсумку (Рівень 1)
        int highPriorityCount = 0;
        for (Task t : tasks) {
            if (t.getPriority() == 1) {
                highPriorityCount++;
            }
        }
        System.out.println("\nКількість термінових завдань (пріоритет 1): " + highPriorityCount);

        // Метод бульбашки (Bubble Sort).
        for (int i = 0; i < tasks.length - 1; i++) {
            for (int j = 0; j < tasks.length - 1 - i; j++) {
                if (tasks[j].getEstimatedHours() > tasks[j + 1].getEstimatedHours()) {
                    Task temp = tasks[j];
                    tasks[j] = tasks[j + 1];
                    tasks[j + 1] = temp;
                }
            }
        }

        System.out.println("\n=== Масив завдань ПІСЛЯ сортування (за часом) ===");
        for (Task t : tasks) {
            System.out.println(t);
        }

        System.out.println("\n=== Тестування лінійного пошуку ===");
        System.out.println("Шукаємо завдання з назвою 'Тест', виконавцем 'Ваня' та пріоритетом 1...");
        
        Task searchTarget = new Task("Тест", "Ваня", 1, 0.0);
        boolean found = false;
        
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i].equals(searchTarget)) {
                System.out.println("✅ Об'єкт знайдено на індексі " + i + ":\n" + tasks[i]);
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("❌ Об'єкт не знайдено.");
        }

        scanner.close();
    }
}