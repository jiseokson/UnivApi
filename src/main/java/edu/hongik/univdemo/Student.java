package edu.hongik.univdemo;

public record Student(
		
	String name,
	String email,
	String degree,
	Long graduation
	
) {
	
	public boolean isAnyNull() {
		return name == null || email == null || degree == null || graduation == null;
	}
	
}
