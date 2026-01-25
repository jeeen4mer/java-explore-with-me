package ru.practicum.ewm_service.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @EntityGraph(attributePaths = "events")
    Page<Compilation> findAllByPinned(boolean pinned, Pageable pageable);

    @EntityGraph(attributePaths = "events")
    Page<Compilation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "events")
    Optional<Compilation> findById(Long id);
}