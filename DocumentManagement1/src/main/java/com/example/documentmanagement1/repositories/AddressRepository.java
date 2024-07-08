package com.example.documentmanagement1.repositories;

import com.example.documentmanagement1.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
