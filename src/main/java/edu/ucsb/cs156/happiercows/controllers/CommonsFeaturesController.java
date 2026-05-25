package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.enums.CommonsFeatures;
import edu.ucsb.cs156.happiercows.errors.EntityNotFoundException;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Commons Features")
@RequestMapping("/api/commonsfeatures")
@RestController
public class CommonsFeaturesController extends ApiController {

  @Autowired CommonsRepository commonsRepository;

  @Operation(summary = "List all commons features")
  @GetMapping("")
  public ResponseEntity<List<String>> getCommonsFeatures() {
    return ResponseEntity.ok(
        Arrays.stream(CommonsFeatures.values()).map(Enum::name).collect(Collectors.toList()));
  }

  @Operation(summary = "Update commons feature settings")
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("")
  public ResponseEntity<Commons> postCommonsFeatures(
      @Parameter(description = "The id of the commons") @RequestParam Long commonsId,
      @Parameter(description = "Whether farmers can see the leaderboard")
          @RequestParam(name = "FARMERS_CAN_SEE_LEADERBOARD")
          Boolean farmersCanSeeLeaderboard) {

    Commons commons =
        commonsRepository
            .findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));

    commons.setShowLeaderboard(farmersCanSeeLeaderboard);

    Commons savedCommons = commonsRepository.save(commons);

    return ResponseEntity.ok(savedCommons);
  }
}