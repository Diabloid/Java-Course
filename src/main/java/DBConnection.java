import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// Потокобезпечний Singleton для керування підключенням до БД через пул з'єднань HikariCP (ЛР 10: Рівень 1 & 3)
public class DBConnection {

    private static volatile DBConnection instance;
    private final HikariDataSource dataSource;

    private DBConnection() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("Файл application.properties не знайдено в classpath!");
            }
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Помилка завантаження application.properties: " + e.getMessage(), e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password", ""));
        config.setDriverClassName(props.getProperty("db.driver", "org.h2.Driver"));

        // Налаштування пулу з'єднань
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikaricp.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikaricp.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("hikaricp.idleTimeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikaricp.connectionTimeout", "10000")));
        config.setPoolName(props.getProperty("hikaricp.poolName", "TaskTrackerPool"));

        this.dataSource = new HikariDataSource(config);
    }

    // Double-checked locking для гарантії єдиного потокобезпечного екземпляра
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    // Отримання з'єднання з пулу
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    // Автоматичне ідемпотентне виконання init.sql при старті (Рівень 2)
    public void executeInitScript() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("init.sql")) {
            if (in == null) {
                throw new IllegalStateException("Скрипт init.sql не знайдено в classpath!");
            }

            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Пропускаємо коментарі SQL
                    String trimmed = line.trim();
                    if (!trimmed.startsWith("--") && !trimmed.isEmpty()) {
                        sqlBuilder.append(line).append("\n");
                    }
                }
            }

            // Розбиваємо на окремі SQL-вирази за крапкою з комою
            String[] statements = sqlBuilder.toString().split(";");
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                    }
                }
            }
            System.out.println("✅ [DBConnection]: DDL-скрипт init.sql успішно виконано (схему БД ініціалізовано).");
        } catch (Exception e) {
            throw new RuntimeException("Помилка виконання init.sql: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute("SHUTDOWN");
            } catch (Exception ignored) {}
            dataSource.close();
        }
    }
}
