package bpm.vanilla.api.exception;

import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import bpm.vanilla.api.core.exception.VanillaApiException;

@ControllerAdvice
public class VanillaApiAdvice extends ResponseEntityExceptionHandler{
	

	  
	@ExceptionHandler(VanillaApiException.class)
	protected ResponseEntity<Object> handleConflict(VanillaApiException ex, WebRequest request) {
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("status","error");
		map.put("code",ex.getErrorCode());
		map.put("message",ex.getMessage());
		HttpStatus.ACCEPTED.valueOf( ex.getErrorStatusCode());
		return handleExceptionInternal(ex, map, new HttpHeaders(), HttpStatus.valueOf(ex.getErrorStatusCode()) , request);
	    //return handleExceptionInternal(ex, map, new HttpHeaders(), ex.getErrorStatus(), request);
	}	
	  
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("status","error");
		map.put("code","0");
		map.put("message","The requested URL was not found.");
		return handleExceptionInternal(ex, map, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		pageNotFoundLogger.warn(ex.getMessage());

		Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
		if (!CollectionUtils.isEmpty(supportedMethods)) {
			headers.setAllow(supportedMethods);
		}
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("status","error");
		map.put("code","0");
		map.put("message","Method not allowed.");			
		return handleExceptionInternal(ex, map, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(
			MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("status","error");
		map.put("code","0");
		map.put("message","Missing path variable.");		
		return handleExceptionInternal(ex, map, headers, status, request);
	}	
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("status","error");
		map.put("code","0");
		map.put("message","Missing request parameter(s).");
		return handleExceptionInternal(ex, map, headers, status, request);
	}
}
