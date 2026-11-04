import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// JDBC-реалізація сервісу для сутності Project (ЛР 10: Рівень 1-3)
public class JdbcProjectService extends AbstractJdbcCrudService<Project> {

    @Override
    protected String getTableName() {
        return "projects";
    }

    @Override
    protected Project mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String clientName = rs.getString("client_name");
        double budget = rs.getBigDecimal("budget").doubleValue();
        return new Project(id, name, clientName, budget);
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO projects (id, name, client_name, budget) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Project entity) throws SQLException {
        ps.setLong(1, entity.getId());
        ps.setString(2, entity.getName());
        ps.setString(3, entity.getClientName());
        ps.setBigDecimal(4, BigDecimal.valueOf(entity.getBudget()));
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE projects SET name = ?, client_name = ?, budget = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Project entity) throws SQLException {
        ps.setString(1, entity.getName());
        ps.setString(2, entity.getClientName());
        ps.setBigDecimal(3, BigDecimal.valueOf(entity.getBudget()));
        ps.setLong(4, entity.getId());
    }
}
