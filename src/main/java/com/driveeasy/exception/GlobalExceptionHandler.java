package com.driveeasy.exception;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(com.driveeasy.exception.ResourceNotFoundException ex, Model model){
        model.addAttribute("errorTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(com.driveeasy.exception.ValidationException.class)
    public String handleValidation(com.driveeasy.exception.ValidationException ex, Model model){
        model.addAttribute("errorTitle", "Validation Error");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(com.driveeasy.exception.BookingConflictException.class)
    public String handleConflict(com.driveeasy.exception.BookingConflictException ex, Model model){
        model.addAttribute("errorTitle", "Booking Conflict");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model){
        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
