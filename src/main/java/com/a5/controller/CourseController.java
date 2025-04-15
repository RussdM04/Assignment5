package com.a5.controller;

import com.a5.model.Course;
import com.a5.service.CourseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/form";
    }

    @PostMapping
    public String createCourse(@Valid @ModelAttribute("course") Course course, BindingResult result) {
    if (result.hasErrors()) {
        return "courses/form";
    }
    if (courseService.existsByName(course.getName())) {
        result.rejectValue("name", "error.course", "Course name already exists");
        return "courses/form";
    }
    try {
        courseService.saveCourse(course);
        return "redirect:/courses";
    } catch (IllegalStateException e) {
        result.rejectValue("schedule", "error.course", e.getMessage());
        return "courses/form";
    }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        return "courses/form";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id, @Valid @ModelAttribute("course") Course course, BindingResult result) {
    if (result.hasErrors()) {
        return "courses/form";
    }
    course.setId(id);
    try {
        courseService.saveCourse(course);
        return "redirect:/courses";
    } catch (IllegalStateException e) {
        result.rejectValue("schedule", "error.course", e.getMessage());
        return "courses/form";
    }
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam("query") String query, Model model) {
    List<Course> results = courseService.searchCourses(query);
    model.addAttribute("courses", results);
    return "courses/list";
}

} 