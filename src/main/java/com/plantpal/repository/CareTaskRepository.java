package com.plantpal.repository;
import com.plantpal.domain.CareTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CareTaskRepository extends JpaRepository<CareTask, Long> {
    List<CareTask> findByPlantIdOrderByDueDateAsc(Long plantId);
    List<CareTask> findAllByOrderByDueDateAsc();
    void deleteByPlantId(Long plantId);
}
