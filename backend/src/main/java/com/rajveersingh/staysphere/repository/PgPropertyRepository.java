package com.rajveersingh.staysphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.rajveersingh.staysphere.entity.PgProperty;

import jakarta.persistence.LockModeType;

public interface PgPropertyRepository extends JpaRepository<PgProperty, UUID> {

    List<PgProperty> findByOwner_Id(UUID ownerId);

    Optional<PgProperty> findByIdAndOwner_Id(UUID id, UUID ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PgProperty> findOneByIdAndOwner_Id(UUID id, UUID ownerId);

}
