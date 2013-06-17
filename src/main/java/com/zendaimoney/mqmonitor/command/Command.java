package com.zendaimoney.mqmonitor.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServlet;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Command extends HttpServlet {
	private static final long serialVersionUID = 7575198164688340346L;
	private static Mongo mongo = null;
	private static DB db;
	private static DBCollection queueInfo;
	static BasicDBObject queueInfoDoc;
	public static String mongoIp="";
	public static String cmdCommand="";
	public static int mongoPort=27017;
	public static long monitInterval= 5000;

	@Override
	public void init() {
		Command.mongoIp=getInitParameter("mongoIp");
		Command.mongoPort=Integer.valueOf(getInitParameter("mongoPort"));
		Command.monitInterval=Long.valueOf(getInitParameter("monitInterval"));
		Command.cmdCommand=getInitParameter("cmdCommand");
		try {
			mongo = new Mongo(mongoIp,mongoPort);
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

				runPerXMill();
			}

		}, 0,  monitInterval);
		// query();
	}

	public static void query() {
		DBCursor cursor = queueInfo.find();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
		} finally {
			cursor.close();
		}
	}

	public static void runPerXMill() {

		Runtime rn = Runtime.getRuntime();
		Process p = null;
		String text = null;
		String queue[] = new String[100];
		int j = 0;
		try {
			p = rn.exec(cmdCommand);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((text = in.readLine()) != null) {
				if (text.equals("Name = com.zendaimoney.coreaccount.response.readonly")
						|| text.equals("Name = com.zendaimoney.coreaccount.response")
						|| text.equals("Name = com.zendaimoney.coreaccount.request")
						|| text.equals("Name = com.zendaimoney.coreaccount.request.readonly")
						|| text.equals("Name = ActiveMQ.DLQ")) {
					queue[j] = text;
					j++;
				}
				if (text.indexOf("ConsumerCount") > -1
						&& text.indexOf("TotalConsumerCount") == -1) {
					queue[j] = text;
					j++;
				}
				if (text.indexOf("DispatchCount") > -1) {
					queue[j] = text;
					j++;
				}
				if (text.indexOf("DequeueCount") > -1
						&& text.indexOf("TotalDequeueCount") == -1) {//
					queue[j] = text;
					j++;
				}
				if (text.indexOf("EnqueueCount") > -1
						&& text.indexOf("TotalEnqueueCount") == -1) {// TotalEnqueueCount
					queue[j] = text;
					j++;
				}
			}
			int g = 0;
			for (; g < queue.length && queue[g] != null; g++) {// 计算数组大小
			// System.out.println(queue[g]); // 输出测试
			}

			Map<String, Long> mapMQ = new HashMap<String, Long>();
			mapMQ.put("com.zendaimoney.coreaccount.response.readonly",
					Long.valueOf(0));
			mapMQ.put("com.zendaimoney.coreaccount.response", Long.valueOf(0));
			mapMQ.put("com.zendaimoney.coreaccount.request", Long.valueOf(0));
			mapMQ.put("com.zendaimoney.coreaccount.request.readonly",
					Long.valueOf(0));
			mapMQ.put("ActiveMQ.DLQ", Long.valueOf(0));

			String key = "";
			for (int m = 0; m < g; m++) {

				// System.out.println("m=" + m);
				long DispatchCount = 0;
				long EnqueueCount = 0;
				if (queue[m]
						.equals("Name = com.zendaimoney.coreaccount.response.readonly")) {
					key = "com.zendaimoney.coreaccount.response.readonly";
				} else if (queue[m]
						.equals("Name = com.zendaimoney.coreaccount.response")) {
					key = "com.zendaimoney.coreaccount.response";
				} else if (queue[m]
						.equals("Name = com.zendaimoney.coreaccount.request")) {
					key = "com.zendaimoney.coreaccount.request";
				} else if (queue[m]
						.equals("Name = com.zendaimoney.coreaccount.request.readonly")) {
					key = "com.zendaimoney.coreaccount.request.readonly";
				} else if (queue[m].equals("Name = ActiveMQ.DLQ")) {
					key = "ActiveMQ.DLQ";
				}

				if (queue[m].indexOf("DispatchCount") > -1) {
					String tmp1[] = queue[m].split("\\s");
					if (tmp1.length < 0) {
						return;
					} else {
						DispatchCount = (long) Integer.parseInt(tmp1[2]);
						// System.out.println("DispatchCount=" + DispatchCount);
						// // 输出测试
					}
				}

				// System.out.println("EnqueueCount=" + EnqueueCount); // 输出测试
				if (queue[m].indexOf("EnqueueCount") > -1
						&& queue[m].indexOf("TotalEnqueueCount") == -1) {// TotalEnqueueCount
					String tmp2[] = queue[m].split("\\s");
					if (tmp2.length < 0) {
						return;
					} else {
						EnqueueCount = (long) Integer.parseInt(tmp2[2]);
					}
				}

				mapMQ.put(key, (EnqueueCount - DispatchCount));
				// System.out.println(key+"="
				// + (Long) mapMQ
				// .get(key)); // 输出测试
			}
			insert(new Date(),
					(Long) mapMQ
							.get("com.zendaimoney.coreaccount.response.readonly"),
					(Long) mapMQ.get("com.zendaimoney.coreaccount.response"),
					(Long) mapMQ.get("com.zendaimoney.coreaccount.request"),
					(Long) mapMQ
							.get("com.zendaimoney.coreaccount.request.readonly"),
					(Long) mapMQ.get("ActiveMQ.DLQ"));

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void delete() {
		queueInfo.remove(new BasicDBObject("DequeueCount", 2));
	}

	public static void insert(Date time, long qu1Num, long qu2Num, long qu3Num,
			long qu4Num, long qu5Num) {
		queueInfo.insert(new BasicDBObject("time", time)
				.append("queue1", qu1Num).append("queue2", qu2Num)
				.append("queue3", qu3Num).append("queue4", qu4Num)
				.append("queue5", qu5Num));
	}

}
