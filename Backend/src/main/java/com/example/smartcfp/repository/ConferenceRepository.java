
package com.example.smartcfp.repository;

import com.example.smartcfp.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConferenceRepository
        extends JpaRepository<Conference, Long> {

    Optional<Conference> findByWebsite(String website);

//    List<Conference>
//    findByShortTitleContainingIgnoreCaseOrLongTitleContainingIgnoreCase(
//            String shortTitle,
//            String longTitle);

    List<Conference>
    findByShortTitleContainingIgnoreCase(
            String Keyword);

    List<Conference> findByLocationContainingIgnoreCase(
            String location);
}


