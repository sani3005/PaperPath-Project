
package com.example.smartcfp.repository;

import com.example.smartcfp.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository
        extends JpaRepository<Conference, Long> {

    // Find a conference using its website URL.
    Optional<Conference> findByWebsite(String website);

    // Search by title, location, or source.
    @Query("""
        SELECT c FROM Conference c
        WHERE LOWER(COALESCE(c.shortTitle, ''))
              LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(c.longTitle, ''))
              LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(c.location, ''))
              LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(c.sourceName, ''))
              LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Conference> searchConferences(
            @Param("keyword") String keyword);
}
