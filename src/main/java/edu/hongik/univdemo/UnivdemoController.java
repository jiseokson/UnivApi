package edu.hongik.univdemo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class UnivdemoController {
	
	private final StudentRepository studentRepository;
	
	public UnivdemoController(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}
	
	
	@GetMapping("/degree")
	public ResponseEntity<?> degree(@RequestParam(defaultValue = "") String name) {
		return resolveStudent(studentRepository::findDegreeByName, name);
	}
	
	@GetMapping("/email")
	public ResponseEntity<?> email(@RequestParam(defaultValue = "") String name) {
		return resolveStudent(studentRepository::findEmailByName, name);
	}
	
	@GetMapping("/stat")
	public ResponseEntity<?> degreeCount(@RequestParam(defaultValue = "") String degree) {
		Long count = studentRepository.countStudentByDegree(degree);
		return ResponseEntity
				.status(200)
				.body(Map.of("Number of " + degree + "\'s student", count));
	}
	
	@PutMapping("/register")
	public ResponseEntity<?> register(@RequestBody Student student) {
		System.out.println(student);
		if (student.isAnyNull()) {
			return ResponseEntity
					.status(400)
					.body(Map.of("error", "Bad request body"));
		}
		if (studentRepository.findDegreeByName(student.name()).size() != 0) {
			return ResponseEntity
					.status(400)
					.body(Map.of("error", "Already registered"));
		}
		
		try {
			studentRepository.regist(student);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return ResponseEntity
					.status(400)
					.body(Map.of("error", "Something crashed."));
		}
		return ResponseEntity
				.status(200)
				.body(Map.of("message", "Registration successful"));
	}
	

	private ResponseEntity<?> resolveStudent(Function<String, List<Map<String, String>>> queryFunc, String name) {
		List<Map<String, String>> list = queryFunc.apply(name);
		if (list.size() == 0) {
			return ResponseEntity
					.status(404)
					.body(Map.of("error", "No such student"));
		}
		if (list.size() > 1) {
			return ResponseEntity
					.status(400)
					.body(Map.of("error", "There are multiple students with the same name. Please contact the administrator by phone."));
		}
		return ResponseEntity.ok(list.get(0));
	}
	
}
