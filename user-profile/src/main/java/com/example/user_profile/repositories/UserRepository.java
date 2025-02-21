package com.example.user_profile.repositories;

import com.example.user_profile.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
