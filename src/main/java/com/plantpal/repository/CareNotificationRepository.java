package com.plantpal.repository;
import com.plantpal.domain.CareNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CareNotificationRepository extends JpaRepository<CareNotification,Long> {
 List<CareNotification> findTop30ByOrderByCreatedAtDesc();
 void deleteByPlantId(Long plantId);
}
