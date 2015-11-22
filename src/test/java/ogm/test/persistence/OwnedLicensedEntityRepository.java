package ogm.test.persistence;

import ogm.test.domain.OwnedLicensedEntity;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedLicensedEntityRepository extends GraphRepository<OwnedLicensedEntity> {
}
