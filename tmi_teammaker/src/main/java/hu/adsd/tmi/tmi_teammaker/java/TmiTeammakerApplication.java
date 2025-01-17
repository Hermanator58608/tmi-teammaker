package hu.adsd.tmi.tmi_teammaker.java;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@SpringBootApplication
public class TmiTeammakerApplication {

	@RequestMapping("/")
	void home( HttpServletResponse httpResponse) {

		try {
			httpResponse.sendRedirect("/startpagina.html");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	public static void main(String[] args) {
		SpringApplication.run(TmiTeammakerApplication.class, args);
	}
}
