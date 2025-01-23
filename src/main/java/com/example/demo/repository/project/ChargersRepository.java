package com.example.demo.repository.project;

import com.example.demo.entity.project.Charger;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChargersRepository extends JpaRepository<Charger, Long> {

    List<Charger> findByUsers_IdOrderByControl_IdDesc(Long id);

    Optional<Charger> findByOpenedAndUsers_Id(Boolean isOpened, Long userId);

    Optional<Charger> findByControl_IdAndUsers_Id(Long controlId, Long userId);

    List<Charger> findByControl_IdOrderById(Long controlId);
    List<Charger> findByControl_IdAndStageOrderById(Long controlId, Integer stageId);
    List<Charger> findByUsers_Id(Long userId);


    @Transactional
    @Modifying
    @Query("DELETE FROM Charger c WHERE c.control.id = :controlId")
    void removeByControlId(@Param("controlId") Long controlId);
}
