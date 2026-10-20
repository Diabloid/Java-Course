import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("=== Трекер Завдань (Комплексний проєкт: ЛР 1-4) ===");

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
                    System.out.printf("   -> Базова вартість: $%.2f | Фінальна вартість (%s): $%.2f\n",
                            t.calculateBaseCost(defaultHourlyRate), t.getCostStrategy().getStrategyName(), t.calculateCost(defaultHourlyRate));
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
                    totalCost += t.calculateCost(defaultHourlyRate);
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

            System.out.println("\n=== Масив завдань ПІСЛЯ сортування Bubble Sort (ЛР 2) ===");
            for (Task t : tasks) {
                if (t != null) System.out.println(t);
            }

            // --- ТЕСТУВАННЯ ЛІНІЙНОГО ПОШУКУ (ЛР 2 & ЛР 3) ---
            System.out.println("\n=== Тестування лінійного пошуку (ЛР 2-3) ===");
            System.out.println("Шукаємо завдання з назвою 'Тест', виконавцем 'Ваня' та пріоритетом 1...");
            
            try {
                Task searchTarget = new Task("Тест", "Ваня", 1, 1.0);
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

            // =========================================================================
            // --- ДЕМОНСТРАЦІЯ ВИМОГ ЛАБОРАТОРНОЇ РОБОТИ №4 ---
            // =========================================================================
            System.out.println("\n=======================================================");
            System.out.println("   ДЕМОНСТРАЦІЯ ФУНКЦІОНАЛУ ЛАБОРАТОРНОЇ РОБОТИ №4");
            System.out.println("=======================================================");

            // Для наочності беремо завдання з найбільшою кількістю годин (або створюємо демонстраційне)
            Task demoTask = (tasks.length > 0)
                    ? Arrays.stream(tasks).filter(t -> t != null).max((t1, t2) -> Double.compare(t1.getEstimatedHours(), t2.getEstimatedHours())).orElse(new Task("Демонстраційна фіча", "Архітектор", 1, 6.0))
                    : new Task("Демонстраційна фіча", "Архітектор", 1, 6.0);

            // --- РІВЕНЬ 1. Базовий: Поліморфізм через інтерфейси ---
            System.out.println("\n--- Рівень 1: Поліморфне використання інтерфейсу CostCalculationStrategy ---");
            CostCalculationStrategy[] strategies = new CostCalculationStrategy[] {
                new PriorityCostStrategy(),
                new FixedCostStrategy(),
                new VolumeDiscountCostStrategy()
            };

            System.out.println("Розрахунок вартості для завдання: " + demoTask.getName() + 
                               " (" + demoTask.getEstimatedHours() + " год, пріоритет " + demoTask.getPriority() + ")");
            for (CostCalculationStrategy strategy : strategies) {
                double cost = strategy.calculateCost(demoTask, defaultHourlyRate);
                System.out.printf("   [%s] -> $%.2f\n", strategy.getStrategyName(), cost);
            }

            // --- РІВЕНЬ 2. Середній: Default-методи та множинна реалізація інтерфейсів ---
            System.out.println("\n--- Рівень 2: Default-методи та множинна реалізація інтерфейсів ---");
            System.out.println("1) Виклик default-методу getDetailedDescription():");
            for (CostCalculationStrategy strategy : strategies) {
                // FixedCostStrategy використовує успадкований default-метод,
                // а PriorityCostStrategy перевизначає його.
                System.out.println("   " + strategy.getDetailedDescription(demoTask, defaultHourlyRate));
            }

            System.out.println("\n2) Множинна реалізація: Інтерфейс Reportable:");
            Reportable[] reportables = tasks;
            for (Reportable r : reportables) {
                if (r != null) {
                    r.printReport(); // виклик default-методу з Reportable
                }
            }

            System.out.println("\n3) Множинна реалізація: Інтерфейс Comparable<Task> (природне сортування):");
            // Фільтруємо null перед Arrays.sort
            Task[] nonNullTasks = Arrays.stream(tasks)
                                        .filter(t -> t != null)
                                        .toArray(Task[]::new);
            Arrays.sort(nonNullTasks); // використовує compareTo із Task
            System.out.println("Масив після сортування через Arrays.sort() (Comparable<Task>):");
            for (Task t : nonNullTasks) {
                System.out.printf("   * %.1f год — %s (%s)\n", t.getEstimatedHours(), t.getName(), t.getAssignee());
            }

            // --- РІВЕНЬ 3. Високий: Паттерн Strategy & зміна стратегії в Runtime ---
            System.out.println("\n--- Рівень 3: Паттерн Strategy (зміна поведінки об'єкта в Runtime) ---");
            System.out.println("Початкова стратегія завдання: " + demoTask.getCostStrategy().getStrategyName());
            System.out.printf("   Початкова вартість: $%.2f\n", demoTask.calculateCost(defaultHourlyRate));

            System.out.println("\n-> Динамічно перемикаємо стратегію на FixedCostStrategy в runtime...");
            demoTask.setCostStrategy(new FixedCostStrategy());
            System.out.println("Нова активна стратегія: " + demoTask.getCostStrategy().getStrategyName());
            System.out.printf("   Оновлена вартість без створення нового об'єкта: $%.2f\n", demoTask.calculateCost(defaultHourlyRate));

            System.out.println("\n-> Динамічно перемикаємо стратегію на VolumeDiscountCostStrategy в runtime...");
            demoTask.setCostStrategy(new VolumeDiscountCostStrategy());
            System.out.println("Нова активна стратегія: " + demoTask.getCostStrategy().getStrategyName());
            System.out.printf("   Оновлена вартість зі знижкою на тривалість: $%.2f\n", demoTask.calculateCost(defaultHourlyRate));

            System.out.println("\n=======================================================");

        } catch (NegativeArraySizeException e) {
            System.out.println("❌ Розмір масиву не може бути від'ємним!");
        } catch (InputMismatchException e) {
            System.out.println("❌ Помилка: замість кількості завдань введено некоректні дані.");
        } catch (TaskDomainException e) {
            System.out.println("❌ Доменна помилка: " + e.getMessage());
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
