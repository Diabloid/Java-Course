import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("=== Трекер Завдань (Комплексний проєкт: ЛР 1-3) ===");

        try {
            System.out.print("Скільки завдань ви хочете створити? ");
            int count = scanner.nextInt();
            scanner.nextLine();

            Task[] tasks = new Task[count];

            for (int i = 0; i < count; i++) {
                System.out.println("\n--- Введення завдання #" + (i + 1) + " ---");
                try {
                    System.out.print("Назва: ");
                    String name = scanner.nextLine().trim();
                    
                    System.out.print("Виконавець: ");
                    String assignee = scanner.nextLine().trim();
                    
                    System.out.print("Пріоритет (1 - Високий, 2 - Середній, 3 - Низький): ");
                    int priority = scanner.nextInt();
                    
                    System.out.print("Оцінений час (год): ");
                    double hours = scanner.nextDouble();
                    scanner.nextLine(); // Очищення буфера після введення чисел

                    tasks[i] = createTaskWithLogging(name, assignee, priority, hours);
                    System.out.println("✅ Успішно додано: " + tasks[i]);

                } catch (InputMismatchException e) {
                    System.out.println("❌ Помилка вводу: очікувалося число, а ви ввели текст.");
                    scanner.nextLine(); // Очищення некоректного вводу
                    i--;
                } catch (java.util.NoSuchElementException e) {
                    System.out.println("⚠️ Вхідний потік завершено (EOF).");
                    break;
                } catch (InvalidPriorityException e) {
                    System.out.println("❌ Помилка пріоритету: " + e.getMessage() + ". Введено некоректне значення: " + e.getWrongPriority());
                    i--;
                } catch (TaskDomainException e) {
                    System.out.println("❌ Доменна помилка завдання: " + e.getMessage());
                    i--;
                } catch (Exception e) {
                    System.out.println("❌ Невідома критична помилка: " + e.getMessage());
                    i--;
                } finally {
                    System.out.println("[Спроба збереження завдання #" + (i + 1) + " завершена]");
                }
            }

            // --- ВИВЕДЕННЯ ДО СОРТУВАННЯ ТА РОЗРАХУНОК ВАРТОСТІ (ЛР 1 & ЛР 2) ---
            System.out.println("\n=== Введений масив завдань (до сортування) ===");
            double defaultHourlyRate = 25.0; // Базова ставка за годину з ЛР 1 ($25.00/год)
            for (Task t : tasks) {
                if (t != null) {
                    System.out.println(t);
                    System.out.printf("   -> Базова вартість: $%.2f | Фінальна вартість (з коефіцієнтом терміновості): $%.2f\n",
                            t.calculateBaseCost(defaultHourlyRate), t.calculateFinalCost(defaultHourlyRate));
                }
            }

            // --- ПІДБИТТЯ ПІДСУМКІВ ТА СТАТИСТИКА (ЛР 1 & ЛР 2) ---
            int highPriorityCount = 0;
            double totalHours = 0;
            double totalCost = 0;
            for (Task t : tasks) {
                if (t != null) {
                    if (t.getPriority() == 1) {
                        highPriorityCount++;
                    }
                    totalHours += t.getEstimatedHours();
                    totalCost += t.calculateFinalCost(defaultHourlyRate);
                }
            }
            System.out.println("\nКількість термінових завдань (пріоритет 1): " + highPriorityCount);
            System.out.printf("Сумарний оцінений час: %.1f год.\n", totalHours);
            System.out.printf("Орієнтовна вартість виконання всіх завдань: $%.2f (ставка $%.2f/год)\n", totalCost, defaultHourlyRate);

            // --- СОРТУВАННЯ МЕТОДОМ БУЛЬБАШКИ ЗА ЧАСОМ (ЛР 2) ---
            for (int i = 0; i < tasks.length - 1; i++) {
                for (int j = 0; j < tasks.length - 1 - i; j++) {
                    if (tasks[j] != null && tasks[j + 1] != null && 
                        tasks[j].getEstimatedHours() > tasks[j + 1].getEstimatedHours()) {
                        Task temp = tasks[j];
                        tasks[j] = tasks[j + 1];
                        tasks[j + 1] = temp;
                    }
                }
            }

            System.out.println("\n=== Масив завдань ПІСЛЯ сортування (за часом) ===");
            for (Task t : tasks) {
                if (t != null) System.out.println(t);
            }

            // --- ТЕСТУВАННЯ ЛІНІЙНОГО ПОШУКУ (ЛР 2 & ЛР 3) ---
            System.out.println("\n=== Тестування лінійного пошуку ===");
            System.out.println("Шукаємо завдання з назвою 'Тест', виконавцем 'Ваня' та пріоритетом 1...");
            
            // Тепер створення Task вимагає try-catch через перевірку валідності даних
            try {
                Task searchTarget = new Task("Тест", "Ваня", 1, 1.0); // час має бути > 0, інакше виняток
                boolean found = false;
                
                for (int i = 0; i < tasks.length; i++) {
                    if (tasks[i] != null && tasks[i].equals(searchTarget)) {
                        System.out.println("✅ Об'єкт знайдено на індексі " + i + ":\n" + tasks[i]);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    System.out.println("❌ Об'єкт не знайдено.");
                }
            } catch (TaskDomainException e) {
                System.out.println("Помилка при створенні об'єкта пошуку: " + e.getMessage());
            }

        } catch (NegativeArraySizeException e) {
            System.out.println("❌ Розмір масиву не може бути від'ємним!");
        } catch (InputMismatchException e) {
            System.out.println("❌ Помилка: замість кількості завдань введено некоректні дані.");
        } finally {
            System.out.println("\n[Система]: Закриття потоку введення. Роботу завершено.");
            scanner.close();
        }
    } // Кінець методу main

    // Метод createTaskWithLogging з ЛР 3 із проміжним логуванням
    public static Task createTaskWithLogging(String name, String assignee, int priority, double hours) throws TaskDomainException {
        try {
            return new Task(name, assignee, priority, hours);
        } catch (TaskDomainException e) {
            System.out.println("[LOG SERVER]: Спроба створення некоректного завдання '" + name + "'");
            throw e; 
        }
    }
} // Кінець класу Main
