package com.scholarscore.api.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerHomeController {
	@RequestMapping("/")
	public String loadHomePage(Model m) {
		return SwaggerDocController.JSP_NAME;
	}
}
