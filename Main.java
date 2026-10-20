import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("=== Трекер Завдань (Комплексний проєкт: ЛР 1-5) ===");

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
            // --- ДЕМОНСТРАЦІЯ ЛР 4 (Інтерфейси, Default-методи, Strategy) ---
            // =========================================================================
            System.out.println("\n=== ДЕМОНСТРАЦІЯ ЛАБОРАТОРНОЇ РОБОТИ №4 ===");

            Task demoTask = (tasks.length > 0)
                    ? Arrays.stream(tasks).filter(t -> t != null).max((t1, t2) -> Double.compare(t1.getEstimatedHours(), t2.getEstimatedHours())).orElse(new Task("Демонстраційна фіча", "Архітектор", 1, 6.0))
                    : new Task("Демонстраційна фіча", "Архітектор", 1, 6.0);

            // Поліморфізм інтерфейсу CostCalculationStrategy (створено через Factory з ЛР 5)
            System.out.println("\n--- Рівень 1 (ЛР 4): Поліморфізм інтерфейсу CostCalculationStrategy ---");
            CostCalculationStrategy[] strategies = new CostCalculationStrategy[] {
                CostStrategyFactory.createStrategy("priority"),
                CostStrategyFactory.createStrategy("fixed"),
                CostStrategyFactory.createStrategy("volume")
            };

            System.out.println("Розрахунок вартості для завдання: " + demoTask.getName() + 
                               " (" + demoTask.getEstimatedHours() + " год, пріоритет " + demoTask.getPriority() + ")");
            for (CostCalculationStrategy strategy : strategies) {
                double cost = strategy.calculateCost(demoTask, defaultHourlyRate);
                System.out.printf("   [%s] -> $%.2f\n", strategy.getStrategyName(), cost);
            }

            // Default-методи та множинна реалізація (Reportable, Comparable)
            System.out.println("\n--- Рівень 2 (ЛР 4): Default-методи та множинна реалізація ---");
            System.out.println("1) Виклик default-методу getDetailedDescription():");
            for (CostCalculationStrategy strategy : strategies) {
                System.out.println("   " + strategy.getDetailedDescription(demoTask, defaultHourlyRate));
            }

            System.out.println("\n2) Множинна реалізація: Інтерфейс Reportable:");
            Reportable[] reportables = tasks;
            for (Reportable r : reportables) {
                if (r != null) {
                    r.printReport();
                }
            }

            System.out.println("\n3) Множинна реалізація: Інтерфейс Comparable<Task>:");
            Task[] nonNullTasks = Arrays.stream(tasks)
                                        .filter(t -> t != null)
                                        .toArray(Task[]::new);
            Arrays.sort(nonNullTasks);
            System.out.println("Масив після сортування через Arrays.sort():");
            for (Task t : nonNullTasks) {
                System.out.printf("   * %.1f год — %s (%s)\n", t.getEstimatedHours(), t.getName(), t.getAssignee());
            }

            // =========================================================================
            // --- ДЕМОНСТРАЦІЯ ЛР 5: ПАТТЕРНИ ПРОЄКТУВАННЯ (Factory, Observer, Adapter) ---
            // =========================================================================
            System.out.println("\n=======================================================");
            System.out.println("   ДЕМОНСТРАЦІЯ ФУНКЦІОНАЛУ ЛАБОРАТОРНОЇ РОБОТИ №5");
            System.out.println("=======================================================");

            // --- Рівень 1: Паттерн Factory ---
            System.out.println("\n--- Рівень 1: Паттерн Factory (CostStrategyFactory) ---");
            System.out.println("Створення стратегій через фабрику без прямих викликів new:");
            CostCalculationStrategy factoryStrategy1 = CostStrategyFactory.createStrategy("fixed");
            CostCalculationStrategy factoryStrategy2 = CostStrategyFactory.createStrategy("volume");
            System.out.println("✅ Фабрика успішно створила: " + factoryStrategy1.getStrategyName());
            System.out.println("✅ Фабрика успішно створила: " + factoryStrategy2.getStrategyName());

            try {
                System.out.print("Тест фабрики на невідомий тип ('unknown'): ");
                CostStrategyFactory.createStrategy("unknown");
            } catch (IllegalArgumentException e) {
                System.out.println("Перехоплено очікувану помилку: " + e.getMessage());
            }

            // Динамічна зміна стратегії через фабрику (поєднання Factory + Strategy)
            demoTask.setCostStrategy(CostStrategyFactory.createStrategy("fixed"));
            System.out.printf("Завданню призначено стратегію з фабрики: %s (вартість: $%.2f)\n", 
                    demoTask.getCostStrategy().getStrategyName(), demoTask.calculateCost(defaultHourlyRate));

            // --- Рівень 2: Паттерн Observer ---
            System.out.println("\n--- Рівень 2: Паттерн Observer (Підписка та сповіщення) ---");
            TaskObserver auditLogger = new TaskAuditLogger();
            TaskObserver alertNotifier = new TaskAlertNotifier();

            System.out.println("Підписуємо спостерігачів (TaskAuditLogger та TaskAlertNotifier) на demoTask...");
            demoTask.attach(auditLogger);
            demoTask.attach(alertNotifier);

            System.out.println("\n1) Зміна статусу завдання на 'IN_PROGRESS':");
            demoTask.setStatus("IN_PROGRESS");

            System.out.println("\n2) Зміна стратегії ціноутворення через фабрику:");
            demoTask.setCostStrategy(CostStrategyFactory.createStrategy("volume"));

            System.out.println("\n3) Зміна статусу завдання на 'COMPLETED':");
            demoTask.setStatus("COMPLETED");

            // --- Рівень 3: Паттерн Adapter ---
            System.out.println("\n--- Рівень 3: Паттерн Adapter (Інтеграція стороннього Legacy API) ---");
            // Моделюємо сторонній об'єкт із несумісним API (хвилини замість годин, "CRITICAL" замість int 1)
            LegacyExternalTask externalTask = new LegacyExternalTask(
                    "Оновлення безпеки платіжного шлюзу",
                    "sec-team@fintech.io",
                    150, // 150 хвилин = 2.5 години
                    "CRITICAL" // CRITICAL = пріоритет 1
            );
            System.out.println("Оригінальний зовнішній об'єкт (LegacyExternalTask):");
            System.out.printf("   Title: '%s' | Dev: %s | Duration: %d хв | Urgency: %s\n",
                    externalTask.getTitle(), externalTask.getDeveloperEmail(), 
                    externalTask.getDurationInMinutes(), externalTask.getUrgencyLevel());

            // Адаптуємо сторонній об'єкт до нашої моделі Task
            Task adaptedTask = new LegacyTaskAdapter(externalTask);
            System.out.println("\nОб'єкт після адаптації (LegacyTaskAdapter як Task):");
            System.out.println("   " + adaptedTask);
            System.out.printf("   -> Оцінений час у годинах: %.1f год.\n", adaptedTask.getEstimatedHours());
            System.out.printf("   -> Адаптований пріоритет: %d (%s)\n", adaptedTask.getPriority(), adaptedTask.getPriorityLabel());
            System.out.printf("   -> Розрахунок вартості (ставка $%.2f): $%.2f\n", 
                    defaultHourlyRate, adaptedTask.calculateCost(defaultHourlyRate));

            // Перевіряємо інтеграцію адаптованого завдання з паттерном Observer
            System.out.println("\nТестуємо спостереження за адаптованим завданням:");
            adaptedTask.attach(auditLogger);
            adaptedTask.attach(alertNotifier);
            adaptedTask.setStatus("RESOLVED");

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
