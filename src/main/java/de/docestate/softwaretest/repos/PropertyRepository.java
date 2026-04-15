package de.docestate.softwaretest.repos;

import de.docestate.softwaretest.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
