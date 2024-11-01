package fi.dlaitine.task.board.repository;

import fi.dlaitine.task.board.entity.TaskEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, Integer> {

    List<TaskEntity> findAll();

    Optional<TaskEntity> findByBoardIdAndId(UUID boardId, Integer id);

    List<TaskEntity> findByStatusEqualsAndIndexBetween(TaskEntity.Status status, int start, int end);

    List<TaskEntity> findByStatusEqualsAndIndexGreaterThan(TaskEntity.Status status, int index);

    List<TaskEntity> findByStatusEqualsAndIndexGreaterThanEqual(TaskEntity.Status status, int index);

    @Query(value="SELECT COALESCE(MAX(`index`), -1) FROM task WHERE status = ?1", nativeQuery=true)
    Integer findMaxIndexForStatusGroup(TaskEntity.Status status);
}
