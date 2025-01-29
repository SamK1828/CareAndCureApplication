package com.cac.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cac.client.model.UserInfo;
import com.cac.client.model.DoctorDTO;
import com.cac.client.model.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class PatientClientController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${base.url}")
	private String baseUrl;

	DoctorDTO doctorSession = null;

	Patient patientSession = null;

	UserInfo userSession = null;

	String role = null;

	// @ModelAttribute
	// public void getDoc(@SessionAttribute(name = "docObj", required = false)
	// Doctor docObj) {
	// System.out.println("Session obj doc "+docObj);
	// if (docObj != null) {
	// System.out.println("SESSSSSIIOOOOOON "+docObj+" "+docObj.getDoctorId());
	// doctorSession = docObj;
	// }
	// }

	@ModelAttribute
	public void getPatient(@SessionAttribute(name = "patientObj", required = false) Patient patObj) {
			patientSession = patObj;
			
	}

	@ModelAttribute
	public void getUser(@SessionAttribute(name = "userObj", required = false) UserInfo userObj) {

			userSession = userObj;
	}

	@ModelAttribute
	public void getRole(@SessionAttribute(name = "userRole", required = false) String userRole, Model model) {
			this.role = userRole;
			model.addAttribute("userRole", userRole);
	}

	@ModelAttribute
	public String checkLogin(){
		if(role==null)
		return "redirect:/";
		return null;
	}

	public boolean isAuthorized() {
		if (role == null || role.equalsIgnoreCase("patient")) {
			return false;
		}
		return true;
	}

	

	@GetMapping("/searchPatient")
	public String searchPatient() {
		if (!isAuthorized())
			return "unauthorized";
		return "patient/patientSearch";
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

	@GetMapping("/patientPage")
	public String patientPage(HttpSession session, Model model) {
		cleanUpSessionAttributes(session, model);
		if (role == null || !role.equalsIgnoreCase("patient"))
			return "unauthorized";
		model.addAttribute("patientId", patientSession.getPatientId());
		return "patient/patientPage";
	}

	@RequestMapping(value = "/findPatientByName", method = RequestMethod.GET)
	public String findPatientByName(@RequestParam("name") String name, Model model) {
		if (!isAuthorized())
			return "unauthorized";
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl + "/api/patient/viewPatientByName/" + name;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				model.addAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}

		}
		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			// Add search type and value to the model
			model.addAttribute("searchType", "name");
			model.addAttribute("searchValue", name);
			return "patient/patientList";
		}
		return "patient/patientSearch";
	}

	@RequestMapping(value = "/findPatientById", method = RequestMethod.GET)
	public String findPatientById(@RequestParam("patientId") int patientId, Model model) {
		Patient patient = null;
		String url = baseUrl + "/api/patient/viewPatient/" + patientId;

		try {
			ResponseEntity<Patient> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					Patient.class);
			patient = response.getBody();

			if (patient != null) {
				List<Patient> patientList = new ArrayList<>();
				patientList.add(patient);
				model.addAttribute("patientList", patientList);

				// Add search type and value to the model
				model.addAttribute("searchType", "id");
				model.addAttribute("searchValue", patientId);
				return "patient/patientList";
			}

		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				model.addAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Some Internal Error Occur ");
		}
		return "patient/patientSearch";
	}

	@GetMapping("/patient/viewPatientProfile")
	public String viewProfileByPatient(Model model) {
		if(role==null) {
			model.addAttribute("errorMessage", "Some error occur or Login required.");
			return "unauthorized";
		}
		if (!role.equalsIgnoreCase("patient") || patientSession == null)
			return "unauthorized";
		int patientId = patientSession.getPatientId();
		Patient patient = null;
		String url = baseUrl + "/api/patient/viewPatient/" + patientId;
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					Patient.class);
			patient = response.getBody();
			model.addAttribute("patient", patient);
			return "patient/viewPatientProfilePage";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patient/viewPatientProfilePage";
		}
	}

	@GetMapping("/admin/viewPatientProfile")
	public String viewPatientProfileByAdmin(@RequestParam int patientId, Model model) {

		if (!isAuthorized())
			return "unauthorized";

		Patient patient = null;
		String url = baseUrl + "/api/patient/viewPatient/" + patientId;
		try {
			ResponseEntity<Patient> response = restTemplate.getForEntity(
					url,
					Patient.class, patientId);
			patient = response.getBody();
			model.addAttribute("patient", patient);
			return "patient/viewPatientProfilePage";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					e.getResponseBodyAs(new ParameterizedTypeReference<Map<String, String>>() {}).get("error"));
			return "statusPage";
		}
	}

	@GetMapping("/updatePatient")
	public String updatePatient(@RequestParam(value = "patientId", required = false) Integer patientId, Model model) {
		
		if (role == null) {
			model.addAttribute("errorMessage", "Need to Login first!.");
			return "unauthorized";
		}
		if (role.equals("patient")) {
			if(patientId!=null && patientSession!=null && patientSession.getPatientId()!=patientId) return "unauthorized"; 
			if (patientSession != null)
				patientId = patientSession.getPatientId();
			else {
				model.addAttribute("errorMessage", "No patient ID found in session for patient role.");
				return "unauthorized";
			}
		} else if (role.equals("admin")) {
			if (patientId == null) {
				return "redirect:/adminLoginForm";
				// Replace with your error view
			}
		} else {
			return "redirect:/"; // Replace with your error view
		}

		Patient patient = null;
		String url = baseUrl + "/api/patient/viewPatient/" + patientId;
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.GET, null, Patient.class);
			patient = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patient/patientList";
		}
		if (patient != null) {
			model.addAttribute("patient", patient);
			return "patient/updatePatient";
		} else {
			model.addAttribute("errorMessage", "No Patient found with the given patientId : " + patientId);
			return "patient/patientList";
		}
	}

	@PostMapping("/updatePatient")
	public String submitUpdatePatient(@ModelAttribute("patient") Patient patient, Model model)
			throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl = baseUrl + "/api/patient/updatePatient/" + patient.getPatientId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

		try {
			ResponseEntity<Patient> response = restTemplate.exchange(reuestUrl, HttpMethod.PUT, requestEntity,
					Patient.class);
			patientObj = response.getBody();
		} catch (HttpClientErrorException e) {

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				@SuppressWarnings("unchecked")
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
				String errorMessage = rootNode.path("message").asText();
				model.addAttribute("errorMessage", errorMessage);
			}

			model.addAttribute("patient", patient);
			return "patient/updatePatient";
		}

		model.addAttribute("patient", patientObj);
		model.addAttribute("succMessage", " Patient updated Successfully!");
		return "patient/updatePatient";

	}

	@GetMapping("/deactivatePatient")
	public String deactivatePatient(@RequestParam("patientId") int patientId,
			@RequestParam(value = "searchType", required = false) String searchType,
			@RequestParam(value = "searchValue", required = false) String searchValue,
			Model model) {
		Patient patient = null;
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized"; 
		String url = baseUrl + "/api/patient/deactivatePatient/" + patientId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Patient.class);
			patient = response.getBody();

			// Determine success message based on the patient's status
			if (patient != null && patient.isActive()) {
				model.addAttribute("successMessage", "Patient with ID (" + patientId + ") activated successfully.");
			} else {
				model.addAttribute("errorMessage", "Patient with ID (" + patientId + ") deactivated successfully.");
			}

			// Redirect based on the previous search type
			if ("id".equalsIgnoreCase(searchType)) {
				return findPatientById(patientId, model);
			} else if ("name".equalsIgnoreCase(searchType) && searchValue != null) {
				return findPatientByName(searchValue, model);
			} else if ("viewProfile".equals(searchType)) {
				return viewPatientProfileByAdmin(patientId, model);
			} else {
				// Default behavior: load all patients
				return getAllPatient(model);
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patient/patientList";
		}
	}

	@RequestMapping(value = "/viewAllPatient", method = RequestMethod.GET)
	public String getAllPatient(Model model) {
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized"; 
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl + "/api/patient/viewAllPatient";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
			return "patient/patientList";
		}

		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			return "patient/patientList";
		} else {
			model.addAttribute("errorMessage", "No Patient Record Found.");
			return "patient/patientList";
		}
	}

	// @SuppressWarnings("unchecked")
	// @PostMapping("/patientLogin")
	// public String patientLogin(@RequestParam String username, @RequestParam String password, Model model,
	// 		HttpSession session) {
	// 	if (username.isEmpty()) {
	// 		session.setAttribute("errorMessage", "Please enter Patient Id");
	// 		return "redirect:/patientLoginForm";
	// 	}
	// 	if (password.isEmpty()) {
	// 		session.setAttribute("errorMessage", "Please enter name as password");
	// 		return "redirect:/patientLoginForm";
	// 	}
	// 	UserInfo details = new UserInfo(username, password, "patient");

	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.set("Content-Type", "application/json");

	// 	HttpEntity<UserInfo> requestEntity = new HttpEntity<>(details, headers);

	// 	String requestUrl = baseUrl + "/login";

	// 	try {
	// 		ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
	// 				String.class);
	// 		String message = response.getBody();
	// 		session.setAttribute("message", message);
	// 		session.setAttribute("patientId", Integer.parseInt(username));
	// 		session.setAttribute("userRole", "patient");
	// 		return "redirect:/patientPage"; // Admin-specific page

	// 	} catch (HttpStatusCodeException e) {
	// 		System.out.println(e.getMessage());
	// 		try {
	// 			ObjectMapper objectMapper = new ObjectMapper();
	// 			Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);

	// 			session.setAttribute("errorMessage", errorMessage.get("error"));
	// 		} catch (Exception parseException) {

	// 			session.setAttribute("errorMessage", "An error occurred while parsing the validation errors.");
	// 		}
	// 	}
	// 	return "redirect:/patientLoginForm"; // Redirect back to the login page in case of failure
	// }

	@GetMapping("/viewAllActivePatient")
	public String getAllPatientByStatus(@RequestParam boolean active, Model model) {
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized"; 
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl + "/api/patient/viewAllPatientByStatus?active=" + active;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
			return "patient/patientList";
		}

		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			return "patient/patientList";
		} else {
			model.addAttribute("errorMessage", "No Patient Record Found.");
			return "patient/patientList";
		}
	}

	@GetMapping("/viewPatientInsuranceDetails")
	public String ViewPatientInsurance(@RequestParam("patientId") int patientId, Model model) {
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized"; 
		Patient patient = null;
		String url = baseUrl + "/api/patient/viewPatient/" + patientId;

		try {
			ResponseEntity<Patient> response = restTemplate.getForEntity(
					url,
					Patient.class);
			patient = response.getBody();

			if (patient != null) {
				model.addAttribute("patient", patient);
				return "patient/patientInsuranceReport";
			}

		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {});
				model.addAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				model.addAttribute("errorMessage", "Some error occur.");
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Some Internal Error Occur ");
		}
		return "statusPage";
	}

	//Get PatientList by InsuranceProvider
	@GetMapping("/viewAllPatientByInsuranceProvider")
	public String getMethodName(@RequestParam String insuranceProvider, Model model) {
		if(role==null || !role.equalsIgnoreCase("admin")) return "unauthorized";
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl + "/api/patient/viewAllByInsuranceProvider?insuranceProvider=" + insuranceProvider;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(null, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
			return "patient/patientListByInsuranceProvider";
		}

		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			return "patient/patientListByInsuranceProvider";
		} else {
			model.addAttribute("errorMessage", "No Patient Record Found with Insurance Provider "+insuranceProvider);
			return "patient/patientListByInsuranceProvider";
		}
	}
	

}
