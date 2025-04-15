package com.a5.controller;

import com.a5.model.Student;
import com.a5.service.MailService;
import com.a5.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private MailService mailService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "students/form";
        }

        if (studentService.existsByEmail(student.getEmail())) {
            result.rejectValue("email", "error.student", "Email already exists");
            return "students/form";
        }

        studentService.saveStudent(student);

        // ✅ Send email notification after saving
        try {
            mailService.sendStudentRegistrationEmail(student.getEmail(), student.getName());
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            // Optional: add a flash attribute or log the failure
        }

        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        return "students/form";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @Valid @ModelAttribute("student") Student student,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "students/form";
        }

        student.setId(id);
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("/search")
    public String searchStudents(@RequestParam("query") String query, Model model) {
        List<Student> results = studentService.searchStudents(query);
        model.addAttribute("students", results);
        return "students/list";
    }
}
