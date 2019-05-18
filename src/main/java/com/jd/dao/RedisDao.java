package com.jd.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.jd.utils.DateUtil;

@Component
public class RedisDao {
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	// 历史一共爬了多少数据
	public long getAllDownloadGoodsKeySize() {
		return redisTemplate.opsForSet().size("uniq_goods_key");
	}

	// 当天爬了多少数据
	public Object getCurrentDayDownloadKeySize() {
		Object number = redisTemplate.opsForHash().get(
				"current_day_statistic_key", DateUtil.getCurrentDay());
		return number == null ? 0 : number;
	}
	public Map<Object,Object> getCurrentDayAllData(){
		Map<Object,Object> map= redisTemplate.opsForHash().entries("current_day_statistic_key");
		return map;
	}
	// 一共下载了多少评论
	public long getDownloadCommentKeySize() {
		return redisTemplate.opsForSet().size("uniq_goods_comment_key");
	}

	// 带采集商品数量
	public long getLoadingDownloadGoodsKeySize() {
		return redisTemplate.opsForSet().size("to_do_task_pojo_set_key");
	}

}
