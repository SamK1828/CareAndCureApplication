package com.cac.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cac.client.model.AdminDto;
import com.cac.client.model.AppointmentDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminCleintController {

	@Autowired
	public RestTemplate restTemplate;

	private String role=null;

	private AdminDto adminSession=null;

	@ModelAttribute
	public void getRole(@SessionAttribute(name = "userRole", required = false) String userRole, Model model) {
		if (userRole != null) {
			System.out.println(role);
			role = userRole;
			model.addAttribute("userRole", userRole);
		} 
	}

	@ModelAttribute
	public String checkLogin(){
		if(role==null)
		return "redirect:/adminLoginForm";
		return null;
	}

	@ModelAttribute
	public void checkAdminObject(@SessionAttribute(name = "adminObj", required = false) AdminDto adminObj){
		if(adminObj!=null) {
			this.adminSession=adminObj;
			System.out.println(adminSession);
		}
	}

	private void cleanUpSessionAttributes(HttpSession session, Model model) {
		String errorMessage = (String) session.getAttribute("errorMessage");
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute("errorMessage");
		}
		String message = (String) session.getAttribute("message");
		if (message != null) {
			model.addAttribute("message", message);
			session.removeAttribute("message");
		}
	}

	@GetMapping("/adminPage")
	public String adminPage(HttpSession session, Model model) {
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized";
		cleanUpSessionAttributes(session, model);
		model.addAttribute("adminObj", adminSession);
		return "admin/adminPage";
	}

	@ModelAttribute
	public void addModelAttribute(HttpSession session, Model model) {
		String username = (String) session.getAttribute("username");
		if (username != null) {
			model.addAttribute("username", username);
		}
	}




	@GetMapping("/viewAdminProfile")
	public String viewProfile(HttpSession session, Model model) {
		if(!role.equalsIgnoreCase("admin")) return "redirect:/adminLoginForm";
		if(adminSession==null) return "redirect:/adminLoginForm";
		AdminDto dto = null;
		String url = "http://localhost:8080/api/admin/viewAdminInfo/" + adminSession.getUsername();
		try {
			ResponseEntity<AdminDto> response = restTemplate.getForEntity(
					url,
					AdminDto.class, 
					String.class);
			dto = response.getBody();
			model.addAttribute("admin", dto);

		} catch (HttpStatusCodeException e) {
			model.addAttribute("errorMessage", "Unable to fetch Admin details. Please try again later." +e.getResponseBodyAsString());
			return "statusPage";
		}
		return "admin/viewAdminProfilePage";
	}

	// // List of Schedule with "NotPresent" cancellation reason filter
	// @GetMapping("/viewAllSchedule")
	// public String getAllSchedule(Model model) {
	// 	List<AppointmentDTO> notPresentAppointments = new ArrayList<>();
	// 	String url = "http://localhost:8080/api/adminPage/viewAllSchedule";  // Correct API URL
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.set("Content-Type", "application/json");
	// 	HttpEntity<List<AppointmentDTO>> requestEntity = new HttpEntity<>(notPresentAppointments, headers);
	
	// 	try {
	// 		ResponseEntity<List<AppointmentDTO>> response = restTemplate.exchange(
	// 				url,
	// 				HttpMethod.GET,
	// 				requestEntity,
	// 				new ParameterizedTypeReference<List<AppointmentDTO>>() {
	// 				});
	// 		notPresentAppointments = response.getBody();
			
	// 	} catch (HttpClientErrorException | HttpServerErrorException e) {
	// 		model.addAttribute("errorMessage", "Unable to fetch Schedule List. Please try again later.");
	// 		return "patient/scheduleList";
	// 	}
	
	// 	// Filter schedules for cancellation reason "NotPresent"

		
	// 	if (notPresentAppointments != null && !notPresentAppointments.isEmpty()) {
	// 		model.addAttribute("appointmentList", notPresentAppointments);
	// 		return "patient/scheduleList";
	// 	}
	
	// 	model.addAttribute("errorMessage", "No Appointment Record Found with cancellation reason 'NotPresent'.");
	// 	return "patient/scheduleList";
	// }
	

	// List of Schedule with "NotPresent" cancellation reason filter
@GetMapping("/viewAllSchedule")
public String getAllSchedule(Model model) {
    List<AppointmentDTO>allAppointments = new ArrayList<>();
    List<AppointmentDTO> notPresentAppointments = new ArrayList<>();
    String url = "http://localhost:8080/patient/1/appointments/viewAllSchedule";  // Correct API URL
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    try {
        ResponseEntity<List<AppointmentDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<AppointmentDTO>>() {}
        );
        allAppointments = response.getBody();
        
        // Filter schedules for cancellation reason "NotPresent"
        if (allAppointments != null) {
            for (AppointmentDTO appointment : allAppointments) {
                if ("NotPresent".equalsIgnoreCase(appointment.getReasonOfCancellation())) {
                    notPresentAppointments.add(appointment);
                }
            }
        }
    } catch (HttpClientErrorException | HttpServerErrorException e) {
        model.addAttribute("errorMessage", "Unable to fetch Schedule List. Please try again later.");
        return "patient/scheduleList";
    }

    if (!notPresentAppointments.isEmpty()) {
        model.addAttribute("scheduledList", notPresentAppointments);
    } else {
        model.addAttribute("errorMessage", "No Appointment Record Found with cancellation reason 'NotPresent'.");
    }
    return "patient/scheduleList";
}
}
