package dev.uknownpragma.json.filter.web.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.uknownpragma.json.filter.web.models.JsonFilterWebResponse;
import dev.unknownpragma.json.filter.JsonFilter;
import dev.unknownpragma.json.filter.JsonFilterException;

@Controller
public class JsonFilterController {

	@ResponseBody
	@RequestMapping("/jsonfilter")
	public Object filter(String fields, String excFields, String content) throws JsonFilterException {
		JsonFilterWebResponse res = new JsonFilterWebResponse();

		try (Writer write = new StringWriter(); Reader read = new StringReader(content);) {
			StopWatch chrono = new StopWatch();
			chrono.start();
			new JsonFilter().filter(read, write, fields, excFields);
			chrono.stop();
			res.setTime(chrono.toString());
			res.setRes(write.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ExceptionUtils.getMessage(e) + "\nOrigine de l'erreur : " + ExceptionUtils.getRootCause(e);
		}
		return res;
	}

}
