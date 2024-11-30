package edu.hongik.univdemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CrawlStudentProvider implements CommandLineRunner {
	
	private final StudentRepository studentRepository;
	
	private final String url = "https://apl.hongik.ac.kr/lecture/dbms";
	
	public CrawlStudentProvider(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
		
		studentRepository.clearAll();
	}
	
	@Override
	public void run(String... args) {
		
		try {
			
			Document doc = Jsoup.connect(url).get();
			Elements headingElements = doc.select("h2");
			
			for (Element headingElement: headingElements) {
				
				Element ulElement = headingElement.nextElementSibling().stream()
						.filter(s -> s.tagName().equals("ul"))
						.findFirst()
						.get();
				Elements tupleElements = ulElement.select("li p");
				
				for (Element tupleElement: tupleElements) {
					List<String> tuple = Arrays.stream(tupleElement.text()
							.split(","))
							.map(s -> s.trim())
							.toList();
					studentRepository.regist(new Student(
							tuple.get(0), tuple.get(1), headingElement.text(), Long.parseLong(tuple.get(2))));
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
