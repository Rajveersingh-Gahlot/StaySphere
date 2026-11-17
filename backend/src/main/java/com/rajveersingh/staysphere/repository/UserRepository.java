package com.rajveersingh.staysphere.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rajveersingh.staysphere.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
