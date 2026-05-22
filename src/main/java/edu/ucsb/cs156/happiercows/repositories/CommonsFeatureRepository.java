package edu.ucsb.cs156.happiercows.repositories;

import edu.ucsb.cs156.happiercows.entities.CommonsFeature;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonsFeatureRepository extends CrudRepository<CommonsFeature, Long> {
  Optional<CommonsFeature> findByCommonsIdAndFeature(long commonsId, String feature);
}