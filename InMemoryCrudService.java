import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

// Універсальна реалізація CRUDService у пам'яті (ЛР 7-9: Рівень 1-3)
// Працює з будь-якою сутністю T extends BaseEntity без жодних змін коду
public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {

    // Сховище LinkedHashMap: швидкість O(1) за id + збереження детермінованого порядку вставки
    protected final Map<Long, T> storage = new LinkedHashMap<>();

    @Override
    public T findById(long id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Сутність для збереження не може бути null");
        }
        // Map.put додає нову сутність або оновлює наявну за ключем id
        storage.put(entity.getId(), entity);
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не може бути null");
        }
        // Створюємо НОВУ колекцію, щоб сортування не змінювало стан та порядок сховища (Рівень 2)
        List<T> result = new ArrayList<>(storage.values());
        result.sort(comparator);
        return result;
    }

    // Додаткові допоміжні методи для зручності
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public int count() {
        return storage.size();
    }
}
