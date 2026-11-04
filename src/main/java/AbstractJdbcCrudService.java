import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Абстрактний базовий JDBC-сервіс за патерном Template Method (ЛР 10: Рівень 1 & 3)
// Надає каркас операцій CRUD, делегуючи специфіку таблиць і маппінгу абстрактним методам
public abstract class AbstractJdbcCrudService<T extends BaseEntity> implements CRUDService<T> {

    protected final DBConnection dbConnection;

    public AbstractJdbcCrudService() {
        this.dbConnection = DBConnection.getInstance();
    }

    // Абстрактні методи шаблону (Template Method hooks)
    protected abstract String getTableName();
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract String getInsertSql();
    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String getUpdateSql();
    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;

    // Перевірка існування запису за ID
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка перевірки існування ID=" + id + " в " + getTableName(), e);
        }
    }

    // 1. Пошук за ID (кидає EntityNotFoundException, якщо не знайдено)
    @Override
    public T findById(long id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку за ID=" + id + " в " + getTableName(), e);
        }
        throw new EntityNotFoundException(id);
    }

    // 2. Збереження: додавання або оновлення за id (Upsert)
    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Сутність не може бути null");
        }

        boolean exists = existsById(entity.getId());
        String sql = exists ? getUpdateSql() : getInsertSql();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (exists) {
                setUpdateParameters(ps, entity);
            } else {
                setInsertParameters(ps, entity);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження сутності ID=" + entity.getId() + " в " + getTableName(), e);
        }
    }

    // 3. Видалення за ID (повертає true, якщо видалено, false якщо не було)
    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення ID=" + id + " в " + getTableName(), e);
        }
    }

    // 4. Отримання всіх сутностей
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка завантаження всіх записів з " + getTableName(), e);
        }
        return list;
    }

    // 5. Узагальнене сортування (Рівень 3)
    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не може бути null");
        }
        List<T> list = findAll();
        list.sort(comparator);
        return list;
    }
}
