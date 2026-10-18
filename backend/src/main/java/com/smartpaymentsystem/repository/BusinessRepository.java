package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByIdAndOwners_Id(Long businessId, Long ownerId);
    boolean existsByOwners_IdAndName(Long ownerId, String name);
    List<Business> findByOwners_Id(Long ownerId);
    Optional<Business> findByIdAndOwners_Id(Long businessId, Long ownerId);

    @Query("select b from Business b join b.owners o where o.id = :ownerId")
    List<Business> findAllByOwnerId(@Param("ownerId") Long ownerId);
}
