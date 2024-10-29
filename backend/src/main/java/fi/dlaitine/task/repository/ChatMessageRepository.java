package fi.dlaitine.task.repository;

import fi.dlaitine.task.entity.ChatMessageEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findAll();
}
