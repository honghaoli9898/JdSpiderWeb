package com.jd.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.entity.Book;
import com.jd.entity.EsModel;
import com.jd.entity.EsPage;
import com.jd.utils.ElasticsearchUtil;

@RestController
@Component
@RequestMapping("/test")
public class EsController {

	@GetMapping("/hello")
	public String helloTest() {
		return "Holle World";
	}

	/**
	 * 测试索引
	 */
	private String indexName = "jd_joods_index";
	/**
	 * 类型
	 */
	private String esType = "jd_joods_type";
	private String commentIndexName = "jd_comments_index";
	private String commentEsType = "jd_comments_type";

	/**
	 * http://127.0.0.1:8080/test/createIndex 创建索引
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createIndex")
	public String createIndex(HttpServletRequest request,
			HttpServletResponse response) {
		if (!ElasticsearchUtil.isIndexExist(indexName)) {
			ElasticsearchUtil.createIndex(indexName);
		} else {
			return "索引已经存在";
		}
		return "索引创建成功";
	}

	/**
	 * 创建索引以及类型，并给索引某些字段指定iK分词，以后向该索引中查询时，就会用ik分词。
	 * 
	 * @param: [request, response]
	 * @return: java.lang.String
	 * @auther: LHL
	 * @date: 2018/10/15 17:11
	 */
	@RequestMapping("/createIndexTypeMapping")
	public String createIndexTypeMapping(HttpServletRequest request,
			HttpServletResponse response) {
		if (!ElasticsearchUtil.isIndexExist(indexName)) {
			ElasticsearchUtil.createIndex(indexName, esType);
		} else {
			return "索引已经存在";
		}
		return "索引创建成功";
	}

	@RequestMapping("/deleteIndex")
	public String deleteIndex(String indexName, HttpServletRequest request,
			HttpServletResponse response) {
		boolean b = ElasticsearchUtil.deleteIndex(indexName);
		if (!b) {
			return "失败";
		}
		return "索引删除成功";
	}

	/**
	 * ik分词测试
	 * 
	 * @param: []
	 * @return: void
	 * @auther: LHL
	 * @date: 2018/10/11 15:13
	 */
	@RequestMapping("getik")
	public String ikMapping() {
		String ik = ElasticsearchUtil.ik();
		return ik;
	}

	/**
	 * 插入记录
	 * 
	 * @return
	 */
	@RequestMapping("/insertJson")
	public String insertJson() {
		JSONObject jsonOject = new JSONObject();
		jsonOject.put("id",
				DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));
		jsonOject.put("age", 25);
		jsonOject.put("name", "j-" + new Random(100).nextInt());
		jsonOject.put("date", new Date());
		String id = ElasticsearchUtil.addData(jsonOject, indexName, esType,
				jsonOject.getString("id"));
		return id;
	}

	/**
	 * 插入记录
	 * 
	 * @return
	 */
	@PostMapping("/insertModel")
	public String insertModel() {
		EsModel esModel = new EsModel();
		esModel.setId(DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));
		esModel.setName("m-" + new Random(100).nextInt());
		esModel.setAge(30);
		esModel.setDate(new Date());
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(esModel);
		String id = ElasticsearchUtil.addData(jsonObject, indexName, esType,
				jsonObject.getString("id"));
		return id;
	}

	/**
	 * 删除记录
	 * 
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(String id) {
		if (StringUtils.isNotBlank(id)) {
			ElasticsearchUtil.deleteDataById(indexName, esType, id);
			return "删除id=" + id;
		} else {
			return "id为空";
		}
	}

	/**
	 * 更新数据
	 * 
	 * @return
	 */
	@RequestMapping("/update/{id}")
	public String update(@PathVariable("id") String id) {
		if (StringUtils.isNotBlank(id)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("age", 31);
			jsonObject.put("name", "修改");
			jsonObject.put("date", new Date());
			ElasticsearchUtil.updateDataById(jsonObject, indexName, esType, id);
			return "id=" + id;
		} else {
			return "id为空";
		}
	}

	/**
	 * 获取数据 http://127.0.0.1:8080/test/getData/id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getData/{id}")
	public String getData(@PathVariable("id") String id) {
		if (StringUtils.isNotBlank(id)) {
			Map<String, Object> map = ElasticsearchUtil.searchDataById(
					indexName, esType, id, null);
			return JSONObject.toJSONString(map);
		} else {
			return "id为空";
		}
	}

	/**
	 * 查询数据 模糊查询
	 * 
	 * @return
	 */
	String id = null;

	String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	@RequestMapping("/queryMatchData")
	public String queryMatchData(String context) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolean matchPhrase = false;
		if (matchPhrase == Boolean.TRUE) {
			// 不进行分词搜索
			boolQuery.must(QueryBuilders
					.matchPhraseQuery("goods_name", context));
		} else {
			boolQuery.must(QueryBuilders.matchQuery("goods_name", context));
		}
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, boolQuery, 20,
				"goods_name,goods_style,goods_sku,goods_price,comment_count",
				null, "goods_name");
		for (Map<String, Object> map : list) {
			map.remove("id");
			String s = (String) map.get("goods_price");
			s = s.split("￥")[0];
			map.put("goods_price", s);
		}
		return JSONObject.toJSONString(list);
	}
	@RequestMapping("/queryMatchPieData")
	public String queryMatchPieData(String goodsSku) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolean matchPhrase = true;
		if (matchPhrase == Boolean.TRUE) {
			// 不进行分词搜索
			boolQuery.must(QueryBuilders
					.matchPhraseQuery("goods_sku", goodsSku));
		} else {
			boolQuery.must(QueryBuilders.matchQuery("goods_sku", goodsSku));
		}
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, boolQuery, 20,
				"good_count,after_count,general_count,poor_count,video_count,image_list_count",
				null, null);
		for (Map<String, Object> map : list) {
			map.remove("id");
			for (Entry<String, Object> entry : map.entrySet()) {
				String value = entry.getValue().toString();
				if(value.contains("万+")){
					value = String.valueOf(Double.parseDouble(value.split("万")[0])*10000);
					map.put(entry.getKey(), value);
				}else if(value.contains("+")){
					value = String.valueOf(Integer.parseInt(value.replace("+", "")));
					map.put(entry.getKey(), value);
				}
			}
		}
		return getCommentCountJsonArray(JSONObject.toJSONString(list)).toJSONString();
	}
	public static List<String> fields = new ArrayList<String>();
	public static List<String> fieldsContext = new ArrayList<String>();
	public static void initList(){
		//good_count,after_count,general_count,poor_count,video_count,image_list_count
		fields.add("good_count");
		fields.add("after_count");
		fields.add("general_count");
		fields.add("poor_count");
		fields.add("video_count");
		fields.add("image_list_count");
		fieldsContext.add("好评");
		fieldsContext.add("追评");
		fieldsContext.add("中评");
		fieldsContext.add("差评");
		fieldsContext.add("视频晒单");
		fieldsContext.add("晒图");
	}
	static{
		initList();
	}
	public JSONArray getCommentCountJsonArray(String jsonString) {
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		JSONArray array = JSONArray.parseArray(jsonString);
		for (Object object : array) {
			JSONObject jsonObject = JSONObject.parseObject(object.toString());
			for (Entry<String, Object> entry : jsonObject.entrySet()) {
				jsonObj = new JSONObject();
				String value = fieldsContext.get(fields.indexOf(entry.getKey()));
				String count = entry.getValue().toString();
				jsonObj.put("value", count);
				jsonObj.put("name", value);
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray;
	}

	@RequestMapping("/queryMatchWordCloudData")
	public String queryMatchWordCloudData(String goodsSku) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolean matchPhrase = true;
		if (matchPhrase == Boolean.TRUE) {
			// 不进行分词搜索
			boolQuery.must(QueryBuilders
					.matchPhraseQuery("goods_sku", goodsSku));
		} else {
			boolQuery.must(QueryBuilders.matchQuery("goods_sku", goodsSku));
		}
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, boolQuery, 20, "comment_type_array", null,
				null);
		for (Map<String, Object> map : list) {
			map.remove("id");
		}
		Object object = list.get(0).get("comment_type_array");
		JSONArray jsonArray = JSONArray.parseArray(object.toString());
		jsonArray = getHotWordsJsonArray(jsonArray);
		return jsonArray.toJSONString();
	}

	public JSONArray getHotWordsJsonArray(JSONArray array) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		for (Object object : array) {
			JSONObject jsonObject = JSONObject.parseObject(object.toString());
			jsonObj = new JSONObject();
			for (Entry<String, Object> entry : jsonObject.entrySet()) {
				jsonObj.put("name", entry.getKey());
				jsonObj.put("value", entry.getValue().toString());
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray;
	}

	@RequestMapping("/queryMatchCommentData")
	public String queryMatchCommentData(String goodsSku) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolean matchPhrase = true;
		if (matchPhrase == Boolean.TRUE) {
			// 不进行分词搜索
			boolQuery.must(QueryBuilders.matchPhraseQuery("reference_id",
					goodsSku));
		} else {
			boolQuery.must(QueryBuilders.matchQuery("reference_id", goodsSku));
		}
		// "username,itemUrl,product_size,product_color,content,reference_time"
		List<Map<String, Object>> list = ElasticsearchUtil
				.searchListData(
						commentIndexName,
						commentEsType,
						boolQuery,
						10,
						"reference_time,content,product_color,product_size,itemUrl,username",
						null, null);
		for (Map<String, Object> map : list) {
			map.remove("id");
		}
		return JSONObject.toJSONString(list);
	}

	/**
	 * 通配符查询数据 通配符查询 ?用来匹配1个任意字符，*用来匹配零个或者多个字符
	 * 
	 * @return
	 */
	@RequestMapping("/queryWildcardData")
	public String queryWildcardData(String context) {
		QueryBuilder queryBuilder = QueryBuilders.wildcardQuery(
				"goods_title.keyword", context);
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, queryBuilder, 10, null, null, null);
		return JSONObject.toJSONString(list);
	}

	/**
	 * 正则查询
	 * 
	 * @return
	 */
	@RequestMapping("/queryRegexpData")
	public String queryRegexpData(String context) {
		QueryBuilder queryBuilder = QueryBuilders.regexpQuery(
				"goods_title.keyword", context);
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, queryBuilder, 10, null, null, null);
		return JSONObject.toJSONString(list);
	}

	/**
	 * 查询数字范围数据
	 * 
	 * @return
	 */
	@RequestMapping("/queryIntRangeData")
	public String queryIntRangeData() {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.rangeQuery("price").from(21.0).to(25.0)
				.includeLower(true) // true 包含下界， false 不包含下界
				.includeUpper(false)); // true 包含下界， false 不包含下界
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, boolQuery, 10, null, null, null);
		return JSONObject.toJSONString(list);
	}

	/**
	 * 查询日期范围数据
	 * 
	 * @return
	 */
	@RequestMapping("/queryDateRangeData")
	public String queryDateRangeData() {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.rangeQuery("creatDate")
				.from("2018-10-17T02:03:08.829Z")
				.to("2018-10-17T02:03:09.727Z").includeLower(true)
				.includeUpper(true));
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(
				indexName, esType, boolQuery, 10, null, null, null);
		return JSONObject.toJSONString(list);
	}

	/**
	 * 查询分页 高亮 排序
	 * 
	 * @param startPage
	 *            第几条记录开始 从0开始
	 * @param pageSize
	 *            每页大小
	 * @return
	 */
	@RequestMapping("/queryPage")
	public String queryPage(String startPage, String pageSize, String context) {
		if (StringUtils.isNotBlank(startPage)
				&& StringUtils.isNotBlank(pageSize)) {
			long start = System.currentTimeMillis();
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(
					QueryBuilders.rangeQuery("creatDate")
							.from("2018-10-17T02:03:08.829Z")
							.to("2018-10-17T02:03:09.727Z").includeLower(true)
							.includeUpper(true)).filter(
					QueryBuilders.matchQuery("name", context));
			// boolQuery.filter(QueryBuilders.matchQuery("name",context));
			EsPage list = ElasticsearchUtil.searchDataPage(indexName, esType,
					Integer.parseInt(startPage), Integer.parseInt(pageSize),
					boolQuery, null, "price", "name");
			long end = System.currentTimeMillis();
			System.out.println((end - start) / 1000 + "s");
			System.out.println((end - start) + "ms");
			return JSONObject.toJSONString(list);
		} else {
			return "startPage或者pageSize缺失";
		}
	}

	/**
	 * 深度排序 分页 从当前页为1001开始
	 * 
	 * @param: [startPage, pageSize]
	 * @return: com.aqh.utils.EsPage
	 * @auther: LHL
	 * @date: 2018/10/17 13:45
	 */
	@RequestMapping("/deepPageing")
	public EsPage deepPageing(int startPage, int pageSize) {
		if (startPage < (10000 / pageSize)) {
			System.out.println("startPage需要>=" + (10000 / pageSize));
		}
		long start = System.currentTimeMillis();
		EsPage result = ElasticsearchUtil.deepPageing(indexName, esType,
				startPage, pageSize, "name");
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + "s");
		System.out.println((end - start) + "ms");
		return result;
	}

	/**
	 * 批量添加
	 * 
	 * @param: []
	 * @return: org.elasticsearch.rest.RestStatus
	 * @auther: LHL
	 * @date: 2018/10/15 14:10
	 */
	@RequestMapping("addBulk")
	public void addBulk() {
		List<Book> bookList = Stream
				.iterate(1, i -> i + 1)
				.limit(1000000L)
				.parallel()
				.map(integer -> new Book(String.valueOf(integer), "书名"
						+ integer, "信息" + integer, Double.valueOf(integer),
						new Date())).collect(Collectors.toList());
		long start = System.currentTimeMillis();
		ElasticsearchUtil.bulkAddDocument(indexName, esType, bookList);
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + "s");
		System.out.println((end - start) + "ms");
	}

	/**
	 * 批量添加
	 * 
	 * @param: []
	 * @return: void
	 * @auther: LHL
	 * @date: 2018/10/16 13:55
	 */
	@RequestMapping("/bulkProcessorAdd")
	public void bulkProcessorAdd() {
		List<Book> bookList = Stream
				.iterate(1, i -> i + 1)
				.limit(1000000L)
				.parallel()
				.map(integer -> new Book(String.valueOf(integer), "书名"
						+ integer, "信息" + integer, Double.valueOf(integer),
						new Date())).collect(Collectors.toList());
		long start = System.currentTimeMillis();
		ElasticsearchUtil.bulkProcessorAdd(indexName, esType, bookList);
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + "s");
		System.out.println((end - start) + "ms");
	}

	/**
	 * 批量删除
	 * 
	 * @param: []
	 * @return: org.elasticsearch.rest.RestStatus
	 * @auther: LHL
	 * @date: 2018/10/15 14:18
	 */
	@RequestMapping("deleteBulk")
	public RestStatus deleteBulk() {
		List<String> idsList = Stream.iterate(1, i -> i + 1).limit(1000000L)
				.parallel().map(integer -> String.valueOf(integer))
				.collect(Collectors.toList());
		long start = System.currentTimeMillis();
		BulkResponse bulkItemResponses = ElasticsearchUtil.bulkDeleteDocument(
				indexName, esType, idsList);
		if (bulkItemResponses.hasFailures()) {
			System.out.println(bulkItemResponses.buildFailureMessage());
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + "s");
		System.out.println((end - start) + "ms");
		return bulkItemResponses.status();
	}

	/**
	 * 获取所有
	 * 
	 * @param: []
	 * @return: java.lang.String
	 * @auther: LHL
	 * @date: 2018/10/15 15:03
	 */
	@RequestMapping("/getAll")
	public List<String> getAll() {
		return ElasticsearchUtil.searchAll(indexName);
	}

}
