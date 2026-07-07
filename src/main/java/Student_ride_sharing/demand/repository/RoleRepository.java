package Student_ride_sharing.demand.repository;

import Student_ride_sharing.demand.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles,Long> {

    Roles findByName(String name);

}

