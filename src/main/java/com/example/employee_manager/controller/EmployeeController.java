package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", employeeService.searchEmployees(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "employees";
    }

    @PostMapping("/employees")
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("email") && employee.getEmail() != null
                && employeeService.emailExists(employee.getEmail().trim())) {
            bindingResult.rejectValue("email", "duplicate", "This email is already registered.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("keyword", "");
            return "employees";
        }

        employee.setEmail(employee.getEmail().trim());
        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee saved successfully.");
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String editEmployee(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        return employeeService.getEmployeeById(id)
                .map(employee -> {
                    model.addAttribute("employee", employee);
                    return "edit-employee";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Employee not found.");
                    return "redirect:/employees";
                });
    }

    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (!employeeService.employeeExists(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found.");
            return "redirect:/employees";
        }

        employee.setId(id);
        if (!bindingResult.hasFieldErrors("email") && employee.getEmail() != null
                && employeeService.emailExistsForAnotherEmployee(employee.getEmail().trim(), id)) {
            bindingResult.rejectValue("email", "duplicate", "This email is already registered.");
        }

        if (bindingResult.hasErrors()) {
            return "edit-employee";
        }

        employee.setEmail(employee.getEmail().trim());
        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully.");
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!employeeService.employeeExists(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found.");
            return "redirect:/employees";
        }
        employeeService.deleteEmployeeById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        return "redirect:/employees";
    }
}
