package de.szut.lf8_starter.hello;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloRepository extends JpaRepository<HelloEntity, Long> {

    List<HelloEntity> findByMessage(String message);
}
