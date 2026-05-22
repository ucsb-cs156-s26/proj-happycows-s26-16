package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.entities.CommonsFeature;
import edu.ucsb.cs156.happiercows.enums.CommonsFeatures;
import edu.ucsb.cs156.happiercows.repositories.CommonsFeatureRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Commons Features")
@RequestMapping("/api/commonsfeatures")
@RestController
public class CommonsFeaturesPostController extends ApiController {

  @Autowired
  CommonsFeatureRepository commonsFeatureRepository;

  @Operation(summary = "Update commons feature settings")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("")
  public ResponseEntity<List<CommonsFeature>> postCommonsFeatures(
      @RequestBody Map<String, Object> requestBody) {

    Object commonsIdObject = requestBody.get("commonsId");

    if (commonsIdObject == null) {
      return ResponseEntity.badRequest().build();
    }

    long commonsId = Long.parseLong(commonsIdObject.toString());
    List<CommonsFeature> savedFeatures = new ArrayList<>();

    for (CommonsFeatures feature : CommonsFeatures.values()) {
      String featureName = feature.name();

      if (requestBody.containsKey(featureName)) {
        boolean enabled = Boolean.parseBoolean(requestBody.get(featureName).toString());

        CommonsFeature commonsFeature =
            commonsFeatureRepository
                .findByCommonsIdAndFeature(commonsId, featureName)
                .orElse(
                    CommonsFeature.builder()
                        .commonsId(commonsId)
                        .feature(featureName)
                        .build());

        commonsFeature.setEnabled(enabled);
        savedFeatures.add(commonsFeatureRepository.save(commonsFeature));
      }
    }

    return ResponseEntity.ok(savedFeatures);
  }
}