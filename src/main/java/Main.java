import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("=== Трекер Завдань (Комплексний проєкт: ЛР 1-10) ===");

        // Автоматична ініціалізація схеми реляційної БД через init.sql (ЛР 10: Рівень 1-2)
        try {
            DBConnection.getInstance().executeInitScript();
        } catch (Exception e) {
            System.out.println("⚠️ Помилка ініціалізації БД: " + e.getMessage());
        }

        int count = 0;
        try {
            System.out.print("Скільки завдань ви хочете створити? ");
            if (scanner.hasNextInt()) {
                count = scanner.nextInt();
                scanner.nextLine();
            } else if (scanner.hasNext()) {
                scanner.nextLine();
            }
        } catch (Exception ignored) {
            count = 0;
        }

        Task[] tasks = new Task[count];

        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Введення завдання #" + (i + 1) + " ---");
            try {
                System.out.print("Тип (1 - Нова фіча, 2 - Багфікс, 3 - Документація) [1]: ");
                String typeInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";
                int typeChoice = 1;
                if (!typeInput.isEmpty()) {
                    try {
                        typeChoice = Integer.parseInt(typeInput);
                    } catch (NumberFormatException ignored) {}
                }

                System.out.print("Назва: ");
                String name = scanner.hasNextLine() ? scanner.nextLine().trim() : "Task " + (i + 1);
                
                System.out.print("Виконавець: ");
                String assignee = scanner.hasNextLine() ? scanner.nextLine().trim() : "Developer";
                
                System.out.print("Пріоритет (1 - Високий, 2 - Середній, 3 - Низький): ");
                int priority = scanner.hasNextInt() ? scanner.nextInt() : 2;
                
                System.out.print("Оцінений час (год): ");
                double hours = scanner.hasNextDouble() ? scanner.nextDouble() : 2.0;
                if (scanner.hasNextLine()) scanner.nextLine(); // Очищення буфера

                tasks[i] = createTaskByType(typeChoice, name, assignee, priority, hours);
                System.out.println("✅ Успішно додано: " + tasks[i]);

            } catch (InputMismatchException e) {
                System.out.println("❌ Помилка вводу: очікувалося число, а ви ввели текст.");
                if (scanner.hasNextLine()) scanner.nextLine();
                i--;
            } catch (InvalidPriorityException e) {
                System.out.println("❌ Помилка пріоритету: " + e.getMessage());
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

        CostCalculationStrategy[] strategies = new CostCalculationStrategy[] {
            CostStrategyFactory.createStrategy("priority"),
            CostStrategyFactory.createStrategy("fixed"),
            CostStrategyFactory.createStrategy("volume")
        };

        for (CostCalculationStrategy strategy : strategies) {
            double cost = strategy.calculateCost(demoTask, defaultHourlyRate);
            System.out.printf("   [%s] -> $%.2f\n", strategy.getStrategyName(), cost);
        }

        // =========================================================================
        // --- ДЕМОНСТРАЦІЯ ЛР 5: ПАТТЕРНИ ПРОЄКТУВАННЯ (Factory, Observer, Adapter) ---
        // =========================================================================
        System.out.println("\n=== ДЕМОНСТРАЦІЯ ЛАБОРАТОРНОЇ РОБОТИ №5 ===");
        TaskObserver auditLogger = new TaskAuditLogger();
        demoTask.attach(auditLogger);
        demoTask.setStatus("IN_PROGRESS");

        // =========================================================================
        // --- ДЕМОНСТРАЦІЯ ЛР 6: ІЄРАРХІЯ КЛАСІВ ТА НАСЛІДУВАННЯ ---
        // =========================================================================
        System.out.println("\n=== ДЕМОНСТРАЦІЯ ЛАБОРАТОРНОЇ РОБОТИ №6 ===");
        Task[] polyTasks = new Task[] {
            new FeatureTask("Аутентифікація через OAuth2", "Олександр", 1, 12.0, "https://github.com/company/auth", "Security", 8),
            new BugFixTask("Витік пам'яті в WebSocket", "Марія", 1, 4.0, "https://github.com/company/gateway", "CRITICAL", true),
            new DocumentationTask("API Swagger Specification v2", "Іван", 2, 6.0, "OpenAPI/Markdown", "Frontend Developers")
        };
        for (Task pt : polyTasks) {
            System.out.println("• " + pt + " | Тип: " + pt.getTaskType());
        }

        // =========================================================================
        // --- ДЕМОНСТРАЦІЯ ЛР 7-9: ТИПІЗОВАНИЙ CRUD-СЕРВІС, КОЛЕКЦІЇ, СОРТУВАННЯ ---
        // =========================================================================
        System.out.println("\n=== ДЕМОНСТРАЦІЯ ЛАБОРАТОРНОЇ РОБОТИ №7-9 (Сховище в пам'яті) ===");
        TaskService inMemoryTaskService = new TaskServiceImpl();
        Task memT1 = new FeatureTask(101L, "Рефакторинг архітектури", "Ваня", 1, 6.0, "https://repo/core", "Core", 5);
        Task memT2 = new BugFixTask(102L, "Рефакторинг архітектури", "Петро", 2, 2.5, "https://repo/core", "MAJOR");
        inMemoryTaskService.save(memT1);
        inMemoryTaskService.save(memT2);

        System.out.println("Вторинне сортування в InMemoryCrudService (назва + час):");
        List<Task> memSorted = inMemoryTaskService.findAllSortedByNameAndByEstimatedHours();
        for (Task t : memSorted) {
            System.out.printf("   -> ID=%d | '%s' | Час: %.1f год\n", t.getId(), t.getName(), t.getEstimatedHours());
        }

        // =========================================================================
        // --- ДЕМОНСТРАЦІЯ ЛР 10: MAVEN, JDBC, РЕЛЯЦІЙНА БД, ПУЛ HIKARICP ---
        // =========================================================================
        System.out.println("\n=======================================================");
        System.out.println("   ДЕМОНСТРАЦІЯ ФУНКЦІОНАЛУ ЛАБОРАТОРНОЇ РОБОТИ №10");
        System.out.println("=======================================================");

        // --- Рівень 1 (Базовий): JDBC CRUD для Projects та Tasks ---
        System.out.println("\n--- Рівень 1 (Базовий): Робота з БД через JDBC (Connection, PreparedStatement) ---");
        JdbcProjectService jdbcProjectService = new JdbcProjectService();
        JdbcTaskService jdbcTaskService = new JdbcTaskService();

        // 1. Створюємо проєкт
        Project project1 = new Project(1L, "Web-портал коледжу", "Відділення ІТ", 20000.0);
        jdbcProjectService.save(project1);
        System.out.println("1) Збережено проєкт у таблицю 'projects': " + jdbcProjectService.findById(1L));

        // 2. Створюємо завдання через JDBC
        Task dbTask1 = new FeatureTask(201L, "Розробка розкладу пар", "Олексій", 1, 8.0, "https://github.com/it/site", "Schedule", 5);
        Task dbTask2 = new BugFixTask(202L, "Розробка розкладу пар", "Дмитро", 2, 3.5, "https://github.com/it/site", "MAJOR");
        Task dbTask3 = new DocumentationTask(203L, "Інструкція викладача", "Ваня", 3, 4.0, "Markdown", "Teachers");

        jdbcTaskService.save(dbTask1);
        jdbcTaskService.save(dbTask2);
        jdbcTaskService.save(dbTask3);
        System.out.println("2) Успішно додано 3 завдання в таблицю 'tasks' через PreparedStatement (INSERT).");

        // 3. Оновлення завдання за тим самим ID (save як UPDATE)
        Task dbTask1Updated = new FeatureTask(201L, "Розробка розкладу пар (оновлено)", "Олексій", 1, 10.0, "https://github.com/it/site", "Schedule", 8);
        jdbcTaskService.save(dbTask1Updated);
        System.out.println("3) Оновлено завдання ID=201 (save як UPDATE): " + jdbcTaskService.findById(201L));

        // 4. Видалення та перевірка EntityNotFoundException
        boolean deleted = jdbcTaskService.delete(203L);
        System.out.println("4) Видалено завдання ID=203: " + deleted + " (очікувано: true)");
        try {
            jdbcTaskService.findById(203L);
        } catch (EntityNotFoundException e) {
            System.out.println("   ✅ Перехоплено очікуваний виняток: " + e.getMessage());
        }

        // --- Рівень 2 (Середній): Foreign Key, SQL ORDER BY, Транзакції, SQL Injection ---
        System.out.println("\n--- Рівень 2 (Середній): Вторинне сортування в SQL, Транзакції, Безпека ---");

        // 1. Вторинне сортування на стороні БД через ORDER BY
        System.out.println("1) Вторинне сортування через SQL ORDER BY (name ASC, estimated_hours ASC):");
        List<Task> sqlSortedTasks = jdbcTaskService.findAllSortedByNameAndByEstimatedHours();
        for (Task t : sqlSortedTasks) {
            System.out.printf("   -> ID=%d | '%s' | Час: %.1f год | %s\n", t.getId(), t.getName(), t.getEstimatedHours(), t.getTaskType());
        }

        // 2. Демонстрація транзакцій (ACID) над кількома таблицями (projects + tasks)
        System.out.println("\n2) Демонстрація транзакції (setAutoCommit, commit, rollback):");
        Project transProject = new Project(2L, "AI Game Master RPG", "Ваня (Диплом)", 45000.0);
        List<Task> transTasks = List.of(
            new FeatureTask(301L, "Генератор діалогів LLM", "Ваня", 1, 14.0, "https://github.com/nova/ai", "AI", 10),
            new BugFixTask(302L, "Синхронізація інвентарю", "Ваня", 2, 4.0, "https://github.com/nova/ai", "CRITICAL")
        );

        // А. Успішна транзакція
        try {
            jdbcTaskService.createProjectWithTasksTransactional(transProject, transTasks, false);
        } catch (SQLException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        // Б. Транзакція зі збоєм та відкатом (ROLLBACK)
        Project failedProject = new Project(3L, "Помилковий проєкт", "Тестер", 1000.0);
        List<Task> failedTasks = List.of(
            new FeatureTask(401L, "Тестова фіча", "Тестер", 1, 2.0, "https://repo", "Test", 1)
        );
        try {
            System.out.println("Спроба транзакції з ініційованою помилкою:");
            jdbcTaskService.createProjectWithTasksTransactional(failedProject, failedTasks, true);
        } catch (SQLException ignored) {
            // Очікуваний відкат
        }
        System.out.println("Перевіряємо, чи зберігся проєкт ID=3 після rollback: " + jdbcProjectService.existsById(3L) + " (очікувано: false)");

        // 3. Демонстрація SQL-ін'єкції (Statement vs PreparedStatement)
        System.out.println("\n3) Демонстрація безпеки (SQL Injection):");
        String maliciousInput = "' OR '1'='1";
        System.out.println("Зловмисний рядок пошуку: " + maliciousInput);
        int vulnerableCount = jdbcTaskService.vulnerableSearchCount(maliciousInput);
        int safeCount = jdbcTaskService.safeSearchCount(maliciousInput);
        System.out.println("   ❌ Небезпечний Statement (вразливий): знайдено " + vulnerableCount + " записів (злам спрацював!)");
        System.out.println("   ✅ Безпечний PreparedStatement: знайдено " + safeCount + " записів (ввід знешкоджено параметризацією).");

        // --- Рівень 3 (Високий): Template Method, Пакетна вставка (Batch), Пагінація ---
        System.out.println("\n--- Рівень 3 (Високий): Пакетна вставка (Batch), Пагінація, Пул HikariCP ---");

        // 1. Вимірювання продуктивності: поодинокі INSERT vs addBatch / executeBatch
        int batchSize = 100;
        List<Task> batchList = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            batchList.add(new FeatureTask(1000L + i, "Batch Feature #" + i, "Автомат", 2, 1.0, "https://repo", "Batch", 1));
        }

        System.out.println("1) Порівняння продуктивності вставки " + batchSize + " записів:");
        long startSingle = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            Task t = batchList.get(i);
            jdbcTaskService.save(t);
        }
        long endSingle = System.currentTimeMillis();
        long singleTime = (endSingle - startSingle) * (batchSize / 20); // екстраполяція на 100

        // Видаляємо для чистого пакетного тесту
        for (int i = 0; i < 20; i++) {
            jdbcTaskService.delete(batchList.get(i).getId());
        }

        long startBatch = System.currentTimeMillis();
        jdbcTaskService.saveBatch(batchList);
        long endBatch = System.currentTimeMillis();
        long batchTime = (endBatch - startBatch);

        System.out.printf("   • Орієнтовний час 100 поодиноких INSERT: ~%d мс\n", singleTime);
        System.out.printf("   • Час пакетної вставки (addBatch/executeBatch): %d мс\n", batchTime);
        System.out.println("   -> Пакетна вставка значно прискорює запис завдяки мінімізації мережевих звернень!");

        // 2. Посторінкове зчитування (Pagination) через LIMIT/OFFSET
        System.out.println("\n2) Демонстрація посторінкового читання (LIMIT 5 OFFSET ...):");
        List<Task> page1 = jdbcTaskService.findPage(1, 5);
        List<Task> page2 = jdbcTaskService.findPage(2, 5);
        System.out.println("   [Сторінка 1 (записи 1-5)]:");
        for (Task t : page1) System.out.println("      * ID=" + t.getId() + " | " + t.getName());
        System.out.println("   [Сторінка 2 (записи 6-10)]:");
        for (Task t : page2) System.out.println("      * ID=" + t.getId() + " | " + t.getName());

        // 3. Друк сутностей через поліморфний printAll
        System.out.println("\n3) Друк результатів через утилітний метод printAll(Collection<? extends BaseEntity>):");
        printAll(jdbcProjectService.findAll());

        System.out.println("\n=======================================================");
        System.out.println("[Система]: Роботу програми завершено.");

        scanner.close();
        try {
            DBConnection.getInstance().close();
        } catch (Exception ignored) {}
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
}
