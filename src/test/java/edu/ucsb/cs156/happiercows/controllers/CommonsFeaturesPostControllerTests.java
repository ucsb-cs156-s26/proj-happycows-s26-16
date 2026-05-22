package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.CommonsFeature;
import edu.ucsb.cs156.happiercows.repositories.CommonsFeatureRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommonsFeaturesPostController.class)
public class CommonsFeaturesPostControllerTests extends ControllerTestCase {

  @MockBean
  CommonsFeatureRepository commonsFeatureRepository;

  @MockBean
  UserRepository userRepository;

  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void postCommonsFeatures_creates_new_feature_setting() throws Exception {
    CommonsFeature savedFeature =
        CommonsFeature.builder()
            .id(1L)
            .commonsId(7L)
            .feature("FARMERS_CAN_SEE_LEADERBOARD")
            .enabled(false)
            .build();

    when(commonsFeatureRepository.findByCommonsIdAndFeature(
            7L, "FARMERS_CAN_SEE_LEADERBOARD"))
        .thenReturn(Optional.empty());

    when(commonsFeatureRepository.save(any(CommonsFeature.class))).thenReturn(savedFeature);

    String requestBody =
        """
        {
          "commonsId": 7,
          "FARMERS_CAN_SEE_LEADERBOARD": false
        }
        """;

    MvcResult response =
        mockMvc
            .perform(
                post("/api/commonsfeatures")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    String expectedJson = mapper.writeValueAsString(java.util.List.of(savedFeature));
    String responseString = response.getResponse().getContentAsString();

    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void postCommonsFeatures_updates_existing_feature_setting() throws Exception {
    CommonsFeature existingFeature =
        CommonsFeature.builder()
            .id(1L)
            .commonsId(7L)
            .feature("FARMERS_CAN_SEE_LEADERBOARD")
            .enabled(false)
            .build();

    CommonsFeature updatedFeature =
        CommonsFeature.builder()
            .id(1L)
            .commonsId(7L)
            .feature("FARMERS_CAN_SEE_LEADERBOARD")
            .enabled(true)
            .build();

    when(commonsFeatureRepository.findByCommonsIdAndFeature(
            7L, "FARMERS_CAN_SEE_LEADERBOARD"))
        .thenReturn(Optional.of(existingFeature));

    when(commonsFeatureRepository.save(any(CommonsFeature.class))).thenReturn(updatedFeature);

    String requestBody =
        """
        {
          "commonsId": 7,
          "FARMERS_CAN_SEE_LEADERBOARD": true
        }
        """;

    MvcResult response =
        mockMvc
            .perform(
                post("/api/commonsfeatures")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    String expectedJson = mapper.writeValueAsString(java.util.List.of(updatedFeature));
    String responseString = response.getResponse().getContentAsString();

    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void postCommonsFeatures_returns_bad_request_when_commonsId_missing() throws Exception {
    String requestBody =
        """
        {
          "FARMERS_CAN_SEE_LEADERBOARD": false
        }
        """;

    mockMvc
        .perform(
            post("/api/commonsfeatures")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void postCommonsFeatures_forbidden_for_regular_user() throws Exception {
    String requestBody =
        """
        {
          "commonsId": 7,
          "FARMERS_CAN_SEE_LEADERBOARD": false
        }
        """;

    mockMvc
        .perform(
            post("/api/commonsfeatures")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isForbidden());
  }
}