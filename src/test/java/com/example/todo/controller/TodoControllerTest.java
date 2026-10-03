package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.service.TodoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoService todoService;

    @BeforeEach
    void resetState() {
        todoService.populateInitialData();
    }

    @Test
    void testGetAllTodos_ReturnsPopulatedList() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title", notNullValue()));
    }

    @Test
    void testGetTodoById_Success() throws Exception {
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testGetTodoById_NotFound() throws Exception {
        mockMvc.perform(get("/api/todos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("9999")));
    }

    @Test
    void testCreateTodo_Success() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest(
                "Write Documentation",
                "Create README file with API specification",
                Priority.HIGH,
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Write Documentation"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void testCreateTodo_ValidationFailure() throws Exception {
        CreateTodoRequest invalidRequest = new CreateTodoRequest("", "", Priority.LOW, null);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.title").exists());
    }

    @Test
    void testUpdateTodo_Success() throws Exception {
        UpdateTodoRequest updateRequest = new UpdateTodoRequest(
                "Updated Title via PUT",
                "Updated details",
                true,
                Priority.LOW,
                LocalDate.now().plusDays(4)
        );

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title via PUT"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @Test
    void testPatchTodo_Success() throws Exception {
        UpdateTodoRequest patchRequest = new UpdateTodoRequest();
        patchRequest.setTitle("Partially Patched Title");

        mockMvc.perform(patch("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Partially Patched Title"));
    }

    @Test
    void testToggleTodo() throws Exception {
        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));

        mockMvc.perform(patch("/api/todos/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCompletedTodos() throws Exception {
        mockMvc.perform(delete("/api/todos/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedCount").value(2));
    }

    @Test
    void testGetStats() throws Exception {
        mockMvc.perform(get("/api/todos/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.completed").value(2))
                .andExpect(jsonPath("$.pending").value(3))
                .andExpect(jsonPath("$.completionPercentage").value(40.0));
    }

    @Test
    void testResetData() throws Exception {
        mockMvc.perform(delete("/api/todos"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/todos/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.total").value(5));
    }
}
