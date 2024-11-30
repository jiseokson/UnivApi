package edu.hongik.univdemo;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	public StudentRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<Map<String, String>> findDegreeByName(String name) {
		return jdbcTemplate
				.queryForList("SELECT s.name, s.degree FROM students s WHERE s.name = ?", name)
				.stream()
				.map(s -> Map.of((String)s.get("name"), (String)s.get("degree")))
				.toList();
	}
	
	public List<Map<String, String>> findEmailByName(String name) {
		return jdbcTemplate
				.queryForList("SELECT s.name, s.email FROM students s WHERE s.name = ?", name)
				.stream()
				.map(s -> Map.of((String)s.get("name"), (String)s.get("email")))
				.toList();
	}
	
	public Long countStudentByDegree(String degree) {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students s WHERE s.degree = ? ", Long.class, degree);
	}
	
	public int regist(Student s) throws DataAccessException {
		return jdbcTemplate.update("INSERT INTO students(sid, name, email, degree, graduation) VALUES (?, ?, ?, ?, ?)",
				maxStudentId() + 1, s.name(), s.email(), s.degree(), s.graduation());
	}
	
	public int clearAll() throws DataAccessException {
		return jdbcTemplate.update("DELETE FROM students");
	}
	
	
	private Long maxStudentId() {
		Long result = jdbcTemplate.queryForObject("SELECT MAX(s.sid) FROM students s", Long.class);
		if (result == null)
			return 0L;
		return result;
	}
	
}
