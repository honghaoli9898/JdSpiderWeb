package com.jd.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class JdAnalysisController {

	@RequestMapping("/search")
	String search(HttpServletRequest request, ModelMap modelMap) {
		HttpSession httpSession = request.getSession();
		String o = (String) httpSession.getAttribute("username");
		if (o==null) {
			modelMap.put("key", "null");
		} else {
			modelMap.put("key", o.toString());
		}
		return "jdSearch";
	}

	@RequestMapping("/comment")
	String comment(String goodssku,HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		String o = (String) httpSession.getAttribute("username");
		System.out.println(o);
		if(o!=null){
			return "commentDetail";
		}else{
			return "error";
		}
		
	}

	@RequestMapping("/status2")
	String status() {
		return "spiderStatus";
	}

	@RequestMapping("/index")
	String index() {
		return "index";
	}

	@RequestMapping("/spiderstatus")
	String getTest() {
		return "spiderStatus";
	}

	@RequestMapping("login")
	String login() {
		return "index";
	}

	@RequestMapping("english")
	String englishLogin() {
		return "English";
	}
}
