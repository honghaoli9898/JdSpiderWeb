package com.jd.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RestController
@EnableAutoConfiguration
public class LoginController {
	@RequestMapping("isExistUserId")
	String isExistUserId(@RequestParam("userid") String userId){
		List<Map<String, Object>> resultList = getUserId(userId);
		if (resultList.size() == 0) {
			return "true";
		} else {
			return "false";
		}
	}
	@RequestMapping("register")
	String register(@RequestParam("nickname") String nickName,
			@RequestParam("userid") String userId,
			@RequestParam("password") String password,
			@RequestParam("telephone") String telephone) {
		boolean b = insertUserInfo(nickName, userId, password, telephone);
		if (b) {
			return "true";
		} else {
			return "false";
		}
	}

	@RequestMapping("isExistNickName")
	String isExistNickName(@RequestParam("nickname") String nickName) {
		List<Map<String, Object>> resultList = getNickName(nickName);
		if (resultList.size() == 0) {
			return "true";
		} else {
			return "false";
		}
	}

	@RequestMapping("islogin")
	String isLogin(@RequestParam("username") String username,
			@RequestParam("password") String password) throws JSONException,
			ServletException, IOException {
		List<Map<String, Object>> resultList = getUserName(username);
		if (resultList.size() == 0) {
			return "false";
		} else if (!resultList.get(0).get("password").equals(password)) {
			return "false";
		} else {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes()).getRequest();
			request.getSession().setAttribute("username",
					resultList.get(0).get("name"));
			return "true";
		}
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getNickName(String nickName) {
		String sql = "select userId from user where name='" + nickName + "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	public List<Map<String, Object>> getUserId(String userId) {
		String sql = "select userId from user where userId='" + userId + "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	public List<Map<String, Object>> getUserName(String userId) {
		String sql = "select password,name from user where userId='" + userId
				+ "'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	public boolean insertUserInfo(String nickName, String userId,
			String password, String telephone) {
		String sql = "insert into user values(\"" + nickName + "\",\"" + userId
				+ "\",\"" + password + "\",\"" + telephone + "\")";
		int result = jdbcTemplate.update(sql);
		if (result >= 1) {
			return true;
		} else {
			return false;
		}
	}
}
