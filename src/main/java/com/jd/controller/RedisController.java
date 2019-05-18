package com.jd.controller;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.dao.RedisDao;
import com.jd.utils.DateUtil;

@Component
@RestController
@EnableAutoConfiguration
public class RedisController {
	@Autowired
	private RedisDao redisDao;

	@RequestMapping("/status")
	String getSpiderStatus() {
		StringBuilder status = new StringBuilder();
		status.append("一共爬取了" + redisDao.getAllDownloadGoodsKeySize()
				+ "条京东商品信息");
		status.append("<\br>");
		status.append(DateUtil.getCurrentDay() + "爬取了"
				+ redisDao.getCurrentDayDownloadKeySize() + "条信息");
		status.append("<\br>");
		status.append("一共爬取了" + redisDao.getDownloadCommentKeySize()
				+ "条京东商品评论");
		status.append("<\br>");
		status.append("带采集的京东商品有" + redisDao.getLoadingDownloadGoodsKeySize()
				+ "条");
		return status.toString();
	}

	@RequestMapping("spider")
	String getSpiderGoodsStatus() {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		Map<Object, Object> map = redisDao.getCurrentDayAllData();
		for (Entry<Object, Object> entry : map.entrySet()) {
			jsonArray1.add(entry.getKey());
			jsonArray2.add(entry.getValue());
		}
		jsonObj.put("name", jsonArray1);
		jsonObj.put("value", jsonArray2);
		return jsonObj.toJSONString();
	}
}