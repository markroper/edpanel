package com.scholarscore.api.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.knappsack.swagger4springweb.controller.ApiDocumentationController;

@Controller
@RequestMapping(value = "/documentation")
public class SwaggerDocController extends ApiDocumentationController {
	public static final String JSP_NAME = "documentation";
	public static final String CONTROLLER_PACKAGE = "com.scholarscore.api.controller";
	public static final String API_VERSION = "v1";
	
	
	public SwaggerDocController() {
		setBaseControllerPackage(CONTROLLER_PACKAGE);
		setApiVersion(API_VERSION);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String documentation() {
		return JSP_NAME;
	}
}
