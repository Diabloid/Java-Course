import java.util.Objects;

// Абстрактний базовий клас сутностей системи (ЛР 7-9: Рівень 1-2)
public abstract class BaseEntity {
    private final long id;
    private String name;

    public BaseEntity(long id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID сутності має бути більшим за 0 (отримано: " + id + ")");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Назва сутності не може бути порожньою або null");
        }
        this.id = id;
        this.name = name.trim();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Назва сутності не може бути порожньою або null");
        }
        this.name = name.trim();
    }

    // Перевизначення equals() та hashCode() за полем id (Рівень 2)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] '%s'", id, name);
    }
}
