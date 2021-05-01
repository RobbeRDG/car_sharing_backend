package be.kul.rideservice.repository;

import be.kul.rideservice.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
}
