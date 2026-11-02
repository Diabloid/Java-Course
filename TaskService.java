import java.util.List;

// Специфічний інтерфейс для сутності Task (ЛР 7-9: Рівень 2)
// Винесено окремо, оскільки вторинне поле (estimatedHours) специфічне лише для завдань
public interface TaskService extends CRUDService<Task> {
    
    // Сортування: спочатку за назвою (name), а при збігу — за оціненим часом (estimatedHours)
    List<Task> findAllSortedByNameAndByEstimatedHours();
}
