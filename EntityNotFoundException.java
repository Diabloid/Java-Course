// Виняток відсутності сутності в сховищі (ЛР 7-9: Рівень 1)
// Спадкує RuntimeException (неперевірений), що відповідає сучасному дизайну Java/Spring API
public class EntityNotFoundException extends RuntimeException {
    private final long id;

    public EntityNotFoundException(long id) {
        super("Сутність із ID=" + id + " не знайдена у сховищі.");
        this.id = id;
    }

    public EntityNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
