package com.jd.utils;


import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESClient {

	private static final Logger LOGGER = LogManager.getLogger(ESClient.class);

	@Autowired
	private TransportClient transportClient;

	private static TransportClient client;

	/**
	 * @PostContruct 是spring容器初始化的时候执行该方法
	 * @param: []
	 * @return: void
	 * @auther: LHL
	 * @date: 2018/10/16 14:19
	 */
	@PostConstruct
	public void init() {
		client = this.transportClient;
	}

	@Bean("bulkProcessor")
	public BulkProcessor bulkProcessor() {
		LOGGER.info("bulkProcessor初始化开始。。。。。");
		return BulkProcessor
				.builder(client, new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long l, BulkRequest bulkRequest) {

					}

					@Override
					public void afterBulk(long l, BulkRequest bulkRequest,
							BulkResponse bulkResponse) {

					}

					@Override
					public void afterBulk(long l, BulkRequest bulkRequest,
							Throwable throwable) {
						LOGGER.error("{} data bulk failed,reason :{}",
								bulkRequest.numberOfActions(), throwable);
					}

				})
				.setBulkActions(10000)
				// 批量导入个数
				.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
				// 满5MB进行导入
				.setFlushInterval(TimeValue.timeValueSeconds(5))
				// 冲刷间隔
				.setConcurrentRequests(3)
				// 并发数
				.setBackoffPolicy(
						BackoffPolicy.exponentialBackoff(
								TimeValue.timeValueSeconds(1), 3)) // 重试3次，间隔1s
				.build();
	}
}