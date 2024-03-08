package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mctoluene.productinformationmanagement.model.Property;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findByName(String name);
}
