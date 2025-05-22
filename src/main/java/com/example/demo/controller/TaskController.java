package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    private String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    // Mostrar lista de tareas
    @GetMapping
    public String listTasks(Model model, HttpSession session) {
        String sessionId = getOrCreateSessionId(session);
        model.addAttribute("tasks", taskRepository.findBySessionId(sessionId));
        model.addAttribute("newTask", new Task());
        return "tasks";
    }

    // Crear nueva tarea
    @PostMapping
    public String addTask(@ModelAttribute Task newTask, HttpSession session) {
        newTask.setSessionId(getOrCreateSessionId(session));
        taskRepository.save(newTask);
        return "redirect:/tasks";
    }

    // Marcar tarea como completada o no
    @PostMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id, HttpSession session) {
        String sessionId = getOrCreateSessionId(session);
        Task task = taskRepository.findById(id).orElseThrow();
        if (task.getSessionId().equals(sessionId)) {
            task.setCompleted(!task.isCompleted());
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }

    // Eliminar tarea
    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        String sessionId = getOrCreateSessionId(session);
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getSessionId().equals(sessionId)) {
            taskRepository.deleteById(id);
        }
        return "redirect:/tasks";
    }
}