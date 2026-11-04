import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// JDBC-реалізація сервісу завдань (ЛР 10: Рівень 1, 2, 3)
// Спадкує шаблонні методи від AbstractJdbcCrudService та реалізує інтерфейс TaskService
public class JdbcTaskService extends AbstractJdbcCrudService<Task> implements TaskService {

    @Override
    protected String getTableName() {
        return "tasks";
    }

    // Перетворення рядка ResultSet в поліморфний об'єкт Task (Рівень 2)
    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String taskType = rs.getString("task_type");
        String assignee = rs.getString("assignee");
        int priority = rs.getInt("priority");
        double hours = rs.getBigDecimal("estimated_hours").doubleValue();
        String status = rs.getString("status");
        String strategyName = rs.getString("cost_strategy");

        CostCalculationStrategy strategy = resolveStrategy(strategyName);

        Task task;
        try {
            switch (taskType) {
                case "Bug Fix":
                    String repo = rs.getString("repository_url");
                    String severity = rs.getString("severity");
                    boolean hotfix = rs.getBoolean("is_hotfix");
                    task = new BugFixTask(id, name, assignee, priority, hours, repo, severity, hotfix, strategy);
                    break;
                case "Documentation":
                    String docFormat = rs.getString("doc_format");
                    String audience = rs.getString("target_audience");
                    task = new DocumentationTask(id, name, assignee, priority, hours, docFormat, audience, strategy);
                    break;
                case "External Legacy Task":
                    LegacyExternalTask legacy = new LegacyExternalTask(name, assignee, (int)(hours * 60), priority == 1 ? "CRITICAL" : "NORMAL");
                    task = new LegacyTaskAdapter(id, legacy);
                    break;
                case "New Feature":
                default:
                    String module = rs.getString("module_name");
                    int sp = rs.getInt("story_points");
                    String fRepo = rs.getString("repository_url");
                    task = new FeatureTask(id, name, assignee, priority, hours, fRepo, module, sp, strategy);
                    break;
            }
        } catch (Exception e) {
            throw new SQLException("Помилка створення об'єкта завдання з БД: " + e.getMessage(), e);
        }

        task.setStatus(status);
        return task;
    }

    private CostCalculationStrategy resolveStrategy(String strategyName) {
        if (strategyName == null) return new PriorityCostStrategy();
        if (strategyName.contains("Fixed")) return CostStrategyFactory.createStrategy("fixed");
        if (strategyName.contains("Volume")) return CostStrategyFactory.createStrategy("volume");
        return CostStrategyFactory.createStrategy("priority");
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO tasks (id, project_id, name, task_type, assignee, priority, estimated_hours, " +
               "status, cost_strategy, repository_url, severity, is_hotfix, module_name, story_points, doc_format, target_audience) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Task entity) throws SQLException {
        populateParameters(ps, entity, false);
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE tasks SET project_id = ?, name = ?, task_type = ?, assignee = ?, priority = ?, " +
               "estimated_hours = ?, status = ?, cost_strategy = ?, repository_url = ?, severity = ?, " +
               "is_hotfix = ?, module_name = ?, story_points = ?, doc_format = ?, target_audience = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Task entity) throws SQLException {
        populateParameters(ps, entity, true);
    }

    private void populateParameters(PreparedStatement ps, Task entity, boolean isUpdate) throws SQLException {
        int idx = 1;
        if (!isUpdate) {
            ps.setLong(idx++, entity.getId());
        }

        // project_id (за замовчуванням 1L або null, якщо не задано)
        ps.setLong(idx++, 1L);
        ps.setString(idx++, entity.getName());
        ps.setString(idx++, entity.getTaskType());
        ps.setString(idx++, entity.getAssignee());
        ps.setInt(idx++, entity.getPriority());
        ps.setBigDecimal(idx++, BigDecimal.valueOf(entity.getEstimatedHours()));
        ps.setString(idx++, entity.getStatus());
        ps.setString(idx++, entity.getCostStrategy().getStrategyName());

        // Специфічні поля підкласів (pattern matching)
        if (entity instanceof BugFixTask bug) {
            ps.setString(idx++, bug.getRepositoryUrl());
            ps.setString(idx++, bug.getSeverity());
            ps.setBoolean(idx++, bug.isHotfix());
            ps.setNull(idx++, java.sql.Types.VARCHAR); // module_name
            ps.setInt(idx++, 0); // story_points
            ps.setNull(idx++, java.sql.Types.VARCHAR); // doc_format
            ps.setNull(idx++, java.sql.Types.VARCHAR); // target_audience
        } else if (entity instanceof FeatureTask feat) {
            ps.setString(idx++, feat.getRepositoryUrl());
            ps.setNull(idx++, java.sql.Types.VARCHAR); // severity
            ps.setBoolean(idx++, false);
            ps.setString(idx++, feat.getModuleName());
            ps.setInt(idx++, feat.getStoryPoints());
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
        } else if (entity instanceof DocumentationTask doc) {
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setBoolean(idx++, false);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setInt(idx++, 0);
            ps.setString(idx++, doc.getDocFormat());
            ps.setString(idx++, doc.getTargetAudience());
        } else {
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setBoolean(idx++, false);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setInt(idx++, 0);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
            ps.setNull(idx++, java.sql.Types.VARCHAR);
        }

        if (isUpdate) {
            ps.setLong(idx, entity.getId());
        }
    }

    // Рівень 2: Вторинне сортування на стороні СУБД через SQL ORDER BY
    @Override
    public List<Task> findAllSortedByNameAndByEstimatedHours() {
        String sql = "SELECT * FROM tasks ORDER BY name ASC, estimated_hours ASC";
        List<Task> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка виконання сортування ORDER BY у БД: " + e.getMessage(), e);
        }
        return list;
    }

    // Рівень 2: Транзакція над кількома таблицями з підтримкою rollback
    public void createProjectWithTasksTransactional(Project project, List<Task> taskList, boolean simulateError) throws SQLException {
        String insertProjectSql = "INSERT INTO projects (id, name, client_name, budget) VALUES (?, ?, ?, ?)";
        String insertTaskSql = getInsertSql();

        try (Connection conn = dbConnection.getConnection()) {
            // 1. Вимикаємо автоматичний коміт для запуску ручної транзакції
            conn.setAutoCommit(false);
            try {
                // Вставка проєкту
                try (PreparedStatement psProj = conn.prepareStatement(insertProjectSql)) {
                    psProj.setLong(1, project.getId());
                    psProj.setString(2, project.getName());
                    psProj.setString(3, project.getClientName());
                    psProj.setBigDecimal(4, BigDecimal.valueOf(project.getBudget()));
                    psProj.executeUpdate();
                }

                // Вставка завдань
                try (PreparedStatement psTask = conn.prepareStatement(insertTaskSql)) {
                    for (Task t : taskList) {
                        setInsertParameters(psTask, t);
                        psTask.executeUpdate();
                    }
                }

                // Симуляція помилки під час транзакції для демонстрації rollback
                if (simulateError) {
                    throw new SQLException("СИМУЛЯЦІЯ ЗБОЮ ТРАНЗАКЦІЇ: Збій зв'язку або порушення бізнес-правила!");
                }

                // 2. Успішний коміт транзакції
                conn.commit();
                System.out.println("✅ [Транзакція]: Успішно зафіксовано (COMMIT) створення проєкту та " + taskList.size() + " завдань.");
            } catch (SQLException e) {
                // 3. Відкат транзакції при виникненні будь-якої помилки
                conn.rollback();
                System.out.println("⚠️ [Транзакція]: Виконано відкат (ROLLBACK)! Жодні дані не записані в БД. Помилка: " + e.getMessage());
                throw e;
            } finally {
                // Відновлюємо автокоміт
                conn.setAutoCommit(true);
            }
        }
    }

    // Рівень 3: Пакетна вставка (Batch insert) через addBatch / executeBatch
    public void saveBatch(List<Task> tasks) {
        String sql = getInsertSql();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Task t : tasks) {
                setInsertParameters(ps, t);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пакетної вставки завдань: " + e.getMessage(), e);
        }
    }

    // Рівень 3: Посторінкове читання (Pagination) через LIMIT/OFFSET
    public List<Task> findPage(int page, int size) {
        if (page < 1 || size <= 0) {
            throw new IllegalArgumentException("Номер сторінки має бути >= 1, а розмір > 0");
        }
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM tasks ORDER BY id ASC LIMIT ? OFFSET ?";
        List<Task> pageItems = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pageItems.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка посторінкового зчитування: " + e.getMessage(), e);
        }
        return pageItems;
    }

    // Рівень 2: Демонстрація небезпеки Statement (SQL Injection) vs безпеки PreparedStatement
    public int vulnerableSearchCount(String userInput) {
        // "Поганий" метод із конкатенацією рядків
        String badSql = "SELECT COUNT(*) FROM tasks WHERE name = '" + userInput + "'";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(badSql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("   [SQL Error]: " + e.getMessage());
        }
        return 0;
    }

    public int safeSearchCount(String userInput) {
        // "Хороший" метод із параметризацією PreparedStatement
        String safeSql = "SELECT COUNT(*) FROM tasks WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(safeSql)) {
            ps.setString(1, userInput);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("   [SQL Error]: " + e.getMessage());
        }
        return 0;
    }
}
