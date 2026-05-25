package edu.ucsb.cs156.happiercows.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.enums.CommonsFeatures;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = CommonsFeaturesController.class)
public class CommonsFeaturesControllerTests extends ControllerTestCase {

  @MockBean CommonsRepository commonsRepository;

  @MockBean UserRepository userRepository;

  @Autowired ObjectMapper mapper;

  @WithMockUser(roles = {"USER"})
  @Test
  public void userCanListAllCommonsFeatures() throws Exception {
    List<String> expectedFeatures =
        Arrays.stream(CommonsFeatures.values()).map(Enum::name).collect(Collectors.toList());

    MvcResult response =
        mockMvc.perform(get("/api/commonsfeatures")).andExpect(status().isOk()).andReturn();

    String responseString = response.getResponse().getContentAsString();
    String expectedResponseString = mapper.writeValueAsString(expectedFeatures);

    assertEquals(expectedResponseString, responseString);
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  public void adminCanSetFarmersCanSeeLeaderboardToTrue() throws Exception {
    Long commonsId = 1L;

    Commons commons =
        Commons.builder()
            .id(commonsId)
            .name("Test Commons")
            .showLeaderboard(false)
            .build();

    Commons savedCommons =
        Commons.builder()
            .id(commonsId)
            .name("Test Commons")
            .showLeaderboard(true)
            .build();

    when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));
    when(commonsRepository.save(any(Commons.class))).thenReturn(savedCommons);

    MvcResult response =
        mockMvc
            .perform(
                post(
                        "/api/commonsfeatures?commonsId={commonsId}&FARMERS_CAN_SEE_LEADERBOARD={farmersCanSeeLeaderboard}",
                        commonsId,
                        true)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    verify(commonsRepository, atLeastOnce()).findById(commonsId);
    verify(commonsRepository, atLeastOnce()).save(any(Commons.class));

    String responseString = response.getResponse().getContentAsString();
    String expectedResponseString = mapper.writeValueAsString(savedCommons);

    assertEquals(expectedResponseString, responseString);
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  public void adminCanSetFarmersCanSeeLeaderboardToFalse() throws Exception {
    Long commonsId = 1L;

    Commons commons =
        Commons.builder()
            .id(commonsId)
            .name("Test Commons")
            .showLeaderboard(true)
            .build();

    Commons savedCommons =
        Commons.builder()
            .id(commonsId)
            .name("Test Commons")
            .showLeaderboard(false)
            .build();

    when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));
    when(commonsRepository.save(any(Commons.class))).thenReturn(savedCommons);

    MvcResult response =
        mockMvc
            .perform(
                post(
                        "/api/commonsfeatures?commonsId={commonsId}&FARMERS_CAN_SEE_LEADERBOARD={farmersCanSeeLeaderboard}",
                        commonsId,
                        false)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    verify(commonsRepository, atLeastOnce()).findById(commonsId);
    verify(commonsRepository, atLeastOnce()).save(any(Commons.class));

    String responseString = response.getResponse().getContentAsString();
    String expectedResponseString = mapper.writeValueAsString(savedCommons);

    assertEquals(expectedResponseString, responseString);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void ordinaryUserCannotUpdateCommonsFeatures() throws Exception {
    Long commonsId = 1L;

    mockMvc
        .perform(
            post(
                    "/api/commonsfeatures?commonsId={commonsId}&FARMERS_CAN_SEE_LEADERBOARD={farmersCanSeeLeaderboard}",
                    commonsId,
                    true)
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(commonsRepository, times(0)).findById(any(Long.class));
    verify(commonsRepository, times(0)).save(any(Commons.class));
  }

  @WithMockUser(roles = {"ADMIN"})
  @Test
  public void adminCannotUpdateCommonsFeaturesForMissingCommons() throws Exception {
    Long commonsId = 999L;

    when(commonsRepository.findById(commonsId)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            post(
                    "/api/commonsfeatures?commonsId={commonsId}&FARMERS_CAN_SEE_LEADERBOARD={farmersCanSeeLeaderboard}",
                    commonsId,
                    true)
                .with(csrf()))
        .andExpect(status().isNotFound());

    verify(commonsRepository, atLeastOnce()).findById(commonsId);
    verify(commonsRepository, times(0)).save(any(Commons.class));
  }
}