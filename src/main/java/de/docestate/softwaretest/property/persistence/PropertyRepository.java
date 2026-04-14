package de.docestate.softwaretest.property.persistence;

import de.docestate.softwaretest.property.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
