package com.zendaimoney.mqmonitor.command;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServlet;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Command extends HttpServlet {
	private static final long serialVersionUID = 7575198164688340346L;
	private static Mongo mongo = null;
	private static DB db;
	private static DBCollection queueInfo;
	static BasicDBObject queueInfoDoc;
	public static String mongoIp = "";
	public static String cmdCommand = "";
	public static int mongoPort = 27017;
	public static long monitInterval = 5000;

	@Override
	public void init() {
		System.setProperty("DEBUG.MONGO", "true");

		System.setProperty("DB.TRACE", "true");
		Command.mongoIp = getInitParameter("mongoIp");
		Command.mongoPort = Integer.valueOf(getInitParameter("mongoPort"));
		Command.monitInterval = Long.valueOf(getInitParameter("monitInterval"));
		Command.cmdCommand = getInitParameter("cmdCommand");
		try {
			mongo = new Mongo(mongoIp, mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		db = mongo.getDB("queueInfo");
		queueInfo = db.getCollection("queueInfoColls");
		Timer executeSchedule = new Timer();

		executeSchedule.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					runPerXMill();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}

		}, 0, monitInterval);
	}

	public void runPerXMill() throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
		AuthScope authScope=new AuthScope(new HttpHost(new URI(cmdCommand).getHost()));
		httpClient.getCredentialsProvider().setCredentials(authScope,creds);
		
		String urlprefix = cmdCommand;
		HttpGet httpGet = new HttpGet(urlprefix
				+ "com.zendaimoney.coreaccount.response.readonly");
		HttpResponse response = httpClient.execute(httpGet);
		Long responseReadOnlyCount = parseCount(JSON.parseObject(EntityUtils
				.toString(response.getEntity())));

		httpGet = new HttpGet(urlprefix
				+ "com.zendaimoney.coreaccount.response");
		response = httpClient.execute(httpGet);
		Long responseCount = parseCount(JSON.parseObject(EntityUtils
				.toString(response.getEntity())));

		httpGet = new HttpGet(urlprefix + "com.zendaimoney.coreaccount.request");
		response = httpClient.execute(httpGet);
		Long requestCount = parseCount(JSON.parseObject(EntityUtils
				.toString(response.getEntity())));

		httpGet = new HttpGet(urlprefix
				+ "com.zendaimoney.coreaccount.request.readonly");
		response = httpClient.execute(httpGet);
		Long requestReadOnlyCount = parseCount(JSON.parseObject(EntityUtils
				.toString(response.getEntity())));

		httpGet = new HttpGet(urlprefix + "ActiveMQ.DLQ");
		response = httpClient.execute(httpGet);
		Long dlqCount = parseCount(JSON.parseObject(EntityUtils
				.toString(response.getEntity())));

		httpGet.releaseConnection();
		insert(new Date(), responseReadOnlyCount, responseCount, requestCount,
				requestReadOnlyCount, dlqCount);

	}

	private void insert(Date time, long qu1Num, long qu2Num, long qu3Num,
			long qu4Num, long qu5Num) {
		queueInfo.insert(new BasicDBObject("time", time)
				.append("queue1", qu1Num).append("queue2", qu2Num)
				.append("queue3", qu3Num).append("queue4", qu4Num)
				.append("queue5", qu5Num));
	}

	private Long parseCount(JSONObject root) {
		return root.getJSONObject("value").getLong("EnqueueCount")
				- root.getJSONObject("value").getLong("DequeueCount");
	}

}
