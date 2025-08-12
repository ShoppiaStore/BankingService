package com.example.demo.Repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
     Optional<User> findByEmail(String email);
     User findUserByEmail(String email);

    Optional<Object> findByName(String name);
}
