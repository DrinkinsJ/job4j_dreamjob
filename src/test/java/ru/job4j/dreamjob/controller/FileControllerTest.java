package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    private MockMultipartFile multipartFile;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        multipartFile = new MockMultipartFile("test.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenFileFine() throws IOException {
        var fileDto = new FileDto(multipartFile.getOriginalFilename(), multipartFile.getBytes());
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));
        var actualFile = fileController.getById(1);
        assertThat(actualFile.getBody()).isEqualTo(fileDto.getContent());
    }

    @Test
    public void whenFileEmpty() {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());
        var actualFile = fileController.getById(1);
        assertThat(actualFile.getBody()).isNull();
    }
}