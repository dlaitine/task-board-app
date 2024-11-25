package fi.dlaitine.task.board.repository;

import fi.dlaitine.task.board.entity.BoardEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<BoardEntity, UUID> {

    List<BoardEntity> findByIsPrivateFalse();
}
