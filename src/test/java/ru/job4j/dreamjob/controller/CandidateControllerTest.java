package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateController candidateController;

    private CandidateService candidateService;

    private CityService cityService;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestCandidatesListPageThenGetPageWithCandidates() {
        Candidate candidate1 = new Candidate(1, "testName1", "testDesc1", now(), 1, 2);
        Candidate candidate2 = new Candidate(2, "testName2", "testDesc2", now(), 1, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");
        assertThat(view).isEqualTo("candidates/list");
        assertThat(expectedCandidates).isEqualTo(actualCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Kazan");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");
        assertThat(view).isEqualTo("candidates/create");
        assertThat(expectedCities).isEqualTo(actualCities);
    }

    @Test
    public void whenPostCandidatesWithFileThenSameDataRedirectToCandidatesPage() throws IOException {
        Candidate candidate1 = new Candidate(1, "testName1", "testDesc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate1);
        var model = new ConcurrentModel();
        var view = candidateController.create(candidate1, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate1);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);
        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenGetCandidateThenReturnCandidateAndRedirectToCandidatePage() {
        Candidate candidate1 = new Candidate(1, "testName1", "testDesc1", now(), 1, 2);
        when(candidateService.findById(anyInt())).thenReturn(Optional.of(candidate1));
        var model = new ConcurrentModel();
        var view = candidateController.getById(model, candidate1.getId());
        var actualVacancy = model.getAttribute("candidate");
        assertThat(actualVacancy).isEqualTo(candidate1);
        assertThat(view).isEqualTo("candidates/one");
    }

    @Test
    public void whenUpdateCandidateWithFileThenCandidateUpdatedAndRedirectToCandidatesPage() throws IOException {
        Candidate candidate1 = new Candidate(1, "testName1", "testDesc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoCaptor.capture())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.update(candidate1, testFile, model);
        var updateCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoCaptor.getValue();
        assertThat(candidate1).isEqualTo(updateCandidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteCandidateThenRedirectToCandidatesPage() {
        when(candidateService.deleteById(anyInt())).thenReturn(true);
        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 123);
        var deletedCandidate = model.getAttribute("candidate");
        assertThat(deletedCandidate).isNull();
        assertThat(view).isEqualTo("redirect:/candidates");
    }
}