package com.weareadaptive.auction.organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Integer> {

    @Query("SELECT o FROM Organisation o WHERE o.name = :organisationName")
    Optional<Organisation> findByName(String organisationName);

    @Query("SELECT EXISTS(SELECT o FROM Organisation o WHERE o.name = :organisationName)")
    boolean existsByName(String organisationName);
}
