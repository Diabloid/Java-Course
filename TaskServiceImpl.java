import java.util.List;
import java.util.Comparator;

// Реалізація TaskService на базі узагальненого InMemoryCrudService (ЛР 7-9: Рівень 2)
public class TaskServiceImpl extends InMemoryCrudService<Task> implements TaskService {

    @Override
    public List<Task> findAllSortedByNameAndByEstimatedHours() {
        // Використовуємо Comparator з ланцюжком thenComparing (Рівень 2)
        // Метод повертає нову колекцію, не змінюючи сховище
        Comparator<Task> taskComparator = Comparator
                .comparing(Task::getName)
                .thenComparingDouble(Task::getEstimatedHours);
        
        return findAllSorted(taskComparator);
    }
}
