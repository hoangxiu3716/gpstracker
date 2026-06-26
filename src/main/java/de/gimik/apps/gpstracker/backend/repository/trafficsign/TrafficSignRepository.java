package de.gimik.apps.gpstracker.backend.repository.trafficsign;

import org.springframework.data.jpa.repository.JpaRepository;
import de.gimik.apps.gpstracker.backend.model.TrafficSign;

public interface TrafficSignRepository extends JpaRepository<TrafficSign, Long> {
}
