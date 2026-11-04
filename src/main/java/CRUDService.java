import java.util.List;
import java.util.Comparator;

// Узагальнений інтерфейс CRUD-сервісу з обмеженням T extends BaseEntity (ЛР 7-9: Рівень 1-3)
public interface CRUDService<T extends BaseEntity> {
    
    // Пошук сутності за id (кидає EntityNotFoundException, якщо не знайдено)
    T findById(long id);

    // Додавання нової або оновлення наявної сутності (за entity.getId())
    void save(T entity);

    // Видалення за id (true - успішно видалено, false - сутності не було)
    boolean delete(long id);

    // Рівень 3: узагальнений метод сортування за довільним компаратором (PECS: Comparator<? super T>)
    List<T> findAllSorted(Comparator<? super T> comparator);

    // Рівень 3: default-метод сортування за іменем сутності через базовий findAllSorted
    default List<T> findAllSortedByName() {
        return findAllSorted(Comparator.comparing(BaseEntity::getName));
    }
}
