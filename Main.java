import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("=== Трекер Завдань (Комплексний проєкт: ЛР 1-9) ===");

        try {
            System.out.print("Скільки завдань ви хочете створити? ");
            int count = scanner.nextInt();
            scanner.nextLine();

            Task[] tasks = new Task[count];

            for (int i = 0; i < count; i++) {
                System.out.println("\n--- Введення завдання #" + (i + 1) + " ---");
                try {
                    System.out.print("Тип (1 - Нова фіча, 2 - Багфікс, 3 - Документація) [1]: ");
                    String typeInput = scanner.nextLine().trim();
                    int typeChoice = 1;
                    if (!typeInput.isEmpty()) {
                        try {
                            typeChoice = Integer.parseInt(typeInput);
                        } catch (NumberFormatException ignored) {}
                    }

                    System.out.print("Назва: ");
                    String name = scanner.nextLine().trim();
                    
                    System.out.print("Виконавець: ");
                    String assignee = scanner.nextLine().trim();
                    
                    System.out.print("Пріоритет (1 - Високий, 2 - Середній, 3 - Низький): ");
                    int priority = scanner.nextInt();
                    
                    System.out.print("Оцінений час (год): ");
                    double hours = scanner.nextDouble();
                    scanner.nextLine(); // Очищення буфера після введення чисел

                    tasks[i] = createTaskByType(typeChoice, name, assignee, priority, hours);
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

            // --- ПОШУК ОБ'ЄКТА ЗА ДОПОМОГОЮ EQUALS (ЛР 2) ---
            System.out.println("\n=== Демонстрація пошуку за equals (ЛР 2) ===");
            try {
                Task searchTarget = new FeatureTask(999L, "Тестове завдання", "Іван", 1, 4.0, "https://github.com/vanya/Java-Course", "General", 3);
                System.out.println("Шукаємо зразок: " + searchTarget);
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

            Task demoTask = (tasks.length > 0 && tasks[0] != null)
                    ? tasks[0]
                    : new FeatureTask("Розробка REST API для аутентифікації", "Архітектор", 1, 6.0, "https://github.com/vanya/Java-Course", "Auth", 5);

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

            demoTask.setCostStrategy(CostStrategyFactory.createStrategy("fixed"));
            System.out.printf("Завданню призначено стратегію з фабрики: %s (вартість: $%.2f)\n", 
                    demoTask.getCostStrategy().getStrategyName(), demoTask.calculateCost(defaultHourlyRate));

            // --- Рівень 2: Паттерн Observer ---
            System.out.println("\n--- Рівень 2: Паттерн Observer (Підписка та сповіщення) ---");
            TaskObserver auditLogger = new TaskAuditLogger();
            TaskObserver alertNotifier = new TaskAlertNotifier();

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
            LegacyExternalTask externalTask = new LegacyExternalTask(
                    "Оновлення безпеки платіжного шлюзу",
                    "sec-team@fintech.io",
                    150,
                    "CRITICAL"
            );

            Task adaptedTask = new LegacyTaskAdapter(externalTask);
            System.out.println("Об'єкт після адаптації (LegacyTaskAdapter як Task):");
            System.out.println("   " + adaptedTask);

            adaptedTask.attach(auditLogger);
            adaptedTask.attach(alertNotifier);
            adaptedTask.setStatus("RESOLVED");

            // =========================================================================
            // --- ДЕМОНСТРАЦІЯ ЛР 6: ІЄРАРХІЯ КЛАСІВ ТА НАСЛІДУВАННЯ ---
            // =========================================================================
            System.out.println("\n=======================================================");
            System.out.println("   ДЕМОНСТРАЦІЯ ФУНКЦІОНАЛУ ЛАБОРАТОРНОЇ РОБОТИ №6");
            System.out.println("=======================================================");

            // --- Рівень 1: Базовий рівень ---
            System.out.println("\n--- Рівень 1 (Базовий): Поліморфізм ієрархії підкласів Task ---");
            Task[] polyTasks = new Task[] {
                new FeatureTask("Аутентифікація через OAuth2", "Олександр", 1, 12.0, "https://github.com/company/auth", "Security", 8),
                new BugFixTask("Витік пам'яті в WebSocket", "Марія", 1, 4.0, "https://github.com/company/gateway", "CRITICAL", true),
                new DocumentationTask("API Swagger Specification v2", "Іван", 2, 6.0, "OpenAPI/Markdown", "Frontend Developers"),
                new LegacyTaskAdapter(new LegacyExternalTask("Міграція старої БД", "db-admin@legacy.org", 180, "HIGH"))
            };

            for (Task pt : polyTasks) {
                System.out.println("• " + pt);
                System.out.println("  -> Тип: " + pt.getTaskType() + " | Деталі: " + pt.getExecutionDetails());
            }

            // --- Рівень 2: Ланцюжок конструкторів ---
            System.out.println("\n--- Рівень 2 (Середній): Трирівнева ієрархія та ланцюжок конструкторів ---");
            Task.enableConstructorLogging = true;
            BugFixTask demoBug = new BugFixTask(
                "NPE при обробці порожнього запиту", 
                "Дмитро", 
                2, 
                3.0, 
                "https://github.com/company/backend", 
                "MAJOR"
            );
            Task.enableConstructorLogging = false;

            Task baseRef = demoBug;
            if (baseRef instanceof BugFixTask bug) {
                System.out.println("✅ Об'єкт узгоджено як BugFixTask через pattern matching instanceof:");
                bug.markAsHotfix();
            }

            // --- Рівень 3: Sealed switch ---
            System.out.println("\n--- Рівень 3 (Високий): Sealed Hierarchy + Pattern Matching у switch (Java 21+) ---");
            for (Task task : polyTasks) {
                String description = switch (task) {
                    case BugFixTask b -> String.format("🐛 [Дефект] Рівень: %s | Хотфікс: %s", b.getSeverity(), b.isHotfix() ? "ТАК" : "НІ");
                    case FeatureTask f -> String.format("✨ [Нова фіча] Модуль: %s | Оцінка: %d SP", f.getModuleName(), f.getStoryPoints());
                    case DocumentationTask d -> String.format("📚 [Документація] Формат: %s | Для кого: %s", d.getDocFormat(), d.getTargetAudience());
                    case LegacyTaskAdapter l -> String.format("🔌 [Legacy Адаптер] Зовнішнє завдання: %s", l.getLegacyTask().getTitle());
                };
                System.out.println("   " + description);
            }

            // =========================================================================
            // --- ДЕМОНСТРАЦІЯ ЛР 7-9: ТИПІЗОВАНИЙ CRUD-СЕРВІС, КОЛЕКЦІЇ, СОРТУВАННЯ ---
            // =========================================================================
            System.out.println("\n=======================================================");
            System.out.println("   ДЕМОНСТРАЦІЯ ФУНКЦІОНАЛУ ЛАБОРАТОРНОЇ РОБОТИ №7-9");
            System.out.println("=======================================================");

            // --- Рівень 1 (Базовий): Універсальний CRUDService<T> для сутності Project ---
            System.out.println("\n--- Рівень 1 (Базовий): Універсальний InMemoryCrudService для сутності Project ---");
            CRUDService<Project> projectService = new InMemoryCrudService<>();

            System.out.println("1) Додавання нових сутностей (save):");
            Project p1 = new Project(1L, "Портал відділення коледжу", "Воронов", 12000.0);
            Project p2 = new Project(2L, "AI Game Master (Диплом)", "Ваня", 35000.0);
            projectService.save(p1);
            projectService.save(p2);
            System.out.println("   Успішно збережено: " + p1);
            System.out.println("   Успішно збережено: " + p2);

            System.out.println("\n2) Оновлення наявної сутності за тим самим ID (id=1, оновлено назву та бюджет):");
            Project p1Updated = new Project(1L, "Портал відділення коледжу v2.0", "Воронов", 16000.0);
            projectService.save(p1Updated);
            System.out.println("   Сутність після оновлення: " + projectService.findById(1L));
            System.out.println("   Кількість сутностей у сховищі не змінилася: " + ((InMemoryCrudService<Project>) projectService).count());

            System.out.println("\n3) Видалення (успішне та неуспішне):");
            boolean deletedSuccess = projectService.delete(2L);
            boolean deletedFail = projectService.delete(999L);
            System.out.println("   Видалення існуючого ID=2: " + deletedSuccess + " (очікувано: true)");
            System.out.println("   Видалення неіснуючого ID=999: " + deletedFail + " (очікувано: false)");

            System.out.println("\n4) Пошук неіснуючого ID з обробкою EntityNotFoundException:");
            try {
                projectService.findById(999L);
            } catch (EntityNotFoundException e) {
                System.out.println("   ✅ Перехоплено виняток: " + e.getMessage() + " [ID=" + e.getId() + "]");
            }

            // --- Рівень 2 (Середній): Специфічний TaskService, вторинне сортування та збереження сховища ---
            System.out.println("\n--- Рівень 2 (Середній): TaskService, первинне та вторинне сортування ---");
            TaskService taskService = new TaskServiceImpl();

            // Створюємо щонайменше 3 записи, два з яких мають однакове ім'я (для перевірки вторинного сортування)
            Task t1 = new FeatureTask(101L, "Рефакторинг архітектури", "Ваня", 1, 6.0, "https://repo/core", "Core", 5);
            Task t2 = new BugFixTask(102L, "Рефакторинг архітектури", "Петро", 2, 2.5, "https://repo/core", "MAJOR");
            Task t3 = new DocumentationTask(103L, "Аналіз вимог", "Олена", 3, 4.0, "Markdown", "Devs");

            taskService.save(t1);
            taskService.save(t2);
            taskService.save(t3);

            System.out.println("Початковий стан сховища (порядок додавання у LinkedHashMap):");
            for (Task t : ((InMemoryCrudService<Task>) taskService).findAll()) {
                System.out.printf("   • ID=%d | '%s' | Час: %.1f год | %s\n", t.getId(), t.getName(), t.getEstimatedHours(), t.getTaskType());
            }

            System.out.println("\n1) Сортування лише за назвою (findAllSortedByName):");
            List<Task> sortedByName = taskService.findAllSortedByName();
            for (Task t : sortedByName) {
                System.out.printf("   -> ID=%d | '%s' | Час: %.1f год\n", t.getId(), t.getName(), t.getEstimatedHours());
            }

            System.out.println("\n2) Вторинне сортування (findAllSortedByNameAndByEstimatedHours):");
            System.out.println("   (Спочатку за назвою, а при збігу назви — за зростанням оціненого часу)");
            List<Task> sortedByNameAndHours = taskService.findAllSortedByNameAndByEstimatedHours();
            for (Task t : sortedByNameAndHours) {
                System.out.printf("   -> ID=%d | '%s' | Час: %.1f год\n", t.getId(), t.getName(), t.getEstimatedHours());
            }

            System.out.println("\n3) Перевірка незмінності сховища (immutability of storage):");
            System.out.println("   Сховище зберегло свій первісний порядок після сортувань:");
            for (Task t : ((InMemoryCrudService<Task>) taskService).findAll()) {
                System.out.printf("   • ID=%d | '%s'\n", t.getId(), t.getName());
            }

            // --- Рівень 3 (Високий): Узагальнене сортування (PECS) та Wildcard printAll ---
            System.out.println("\n--- Рівень 3 (Високий): Comparator<? super T> та Wildcard Collection<? extends BaseEntity> ---");

            System.out.println("1) Узагальнене сортування findAllSorted за пріоритетом (1 -> 3):");
            List<Task> sortedByPriority = taskService.findAllSorted(Comparator.comparingInt(Task::getPriority));
            for (Task t : sortedByPriority) {
                System.out.printf("   [Пріоритет %d] ID=%d | '%s'\n", t.getPriority(), t.getId(), t.getName());
            }

            System.out.println("\n2) Виклик утилітного методу printAll(Collection<? extends BaseEntity>):");
            System.out.println("   Друк колекції Task (коваріантність через wildcard ? extends BaseEntity):");
            printAll(taskService.findAllSortedByName());

            System.out.println("\n   Друк колекції Project через той самий printAll:");
            printAll(((InMemoryCrudService<Project>) projectService).findAll());

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

    // Утилітний метод друку сутностей з використанням wildcard <? extends BaseEntity> (ЛР 7-9: Рівень 3)
    public static void printAll(Collection<? extends BaseEntity> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("   [Колекція порожня]");
            return;
        }
        for (BaseEntity item : items) {
            System.out.println("   📌 " + item);
        }
    }

    // Поліморфне створення завдань за типом (ЛР 6)
    public static Task createTaskByType(int typeChoice, String name, String assignee, int priority, double hours) throws TaskDomainException {
        try {
            switch (typeChoice) {
                case 2:
                    return new BugFixTask(name, assignee, priority, hours, "https://github.com/vanya/Java-Course", "MAJOR");
                case 3:
                    return new DocumentationTask(name, assignee, priority, hours, "Markdown", "Developers");
                case 1:
                default:
                    return new FeatureTask(name, assignee, priority, hours, "https://github.com/vanya/Java-Course", "Core", 3);
            }
        } catch (TaskDomainException e) {
            System.out.println("[LOG SERVER]: Спроба створення некоректного завдання '" + name + "'");
            throw e; 
        }
    }
} // Кінець класу Main
