package com.a5.controller;

import com.a5.model.Student;
import com.a5.model.Course;
import com.a5.model.Enrollment;
import com.a5.service.StudentService;
import com.a5.service.CourseService;
import com.a5.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public String listEnrollments(Model model,
                                   @ModelAttribute("success") String success,
                                   @ModelAttribute("error") String error) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());

        if (!success.isEmpty()) {
            model.addAttribute("success", success);
        }
        if (!error.isEmpty()) {
            model.addAttribute("error", error);
        }

        return "enrollments/list";
    }

    @PostMapping("/enroll")
    public String enrollStudent(@RequestParam Long studentId,
                                 @RequestParam Long courseId,
                                 RedirectAttributes redirectAttributes) {
        logger.info("Enrolling student {} in course {}", studentId, courseId);

        try {
            enrollmentService.enrollStudent(studentId, courseId);
            redirectAttributes.addFlashAttribute("success", "Student enrolled successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/enrollments";
    }

    @PostMapping("/unenroll")
    public String unenrollStudent(@RequestParam Long studentId,
                                   @RequestParam Long courseId,
                                   RedirectAttributes redirectAttributes) {
        logger.info("Unenrolling student {} from course {}", studentId, courseId);
        enrollmentService.unenrollStudent(studentId, courseId);
        redirectAttributes.addFlashAttribute("success", "Student unenrolled successfully!");
        return "redirect:/enrollments";
    }
}
