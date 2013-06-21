package com.zendaimoney.mqmonitor.service.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.zendaimoney.mqmonitor.command.Command;
import com.zendaimoney.mqmonitor.entity.MonitorData;

public class MongoService {
	public List<MonitorData> getData(String name,Date start,Date end) {
		List<MonitorData> result=new ArrayList<MonitorData>();
		Mongo mongoClient=null;
		try {
			mongoClient = new Mongo(Command.mongoIp, Command.mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB db = mongoClient.getDB("queueInfo");
		DBCollection dbCollection = db.getCollection("queueInfoColls");
		BasicDBObject query = new BasicDBObject();
		query.put("time", new BasicDBObject("$gte", start).append("$lte", end)); 
		DBCursor cursor = dbCollection.find(query);
		try {
			while (cursor.hasNext()) {
				MonitorData monitorData=new MonitorData();
				DBObject next = cursor.next();
				monitorData.setTime((Date) next.get("time"));
				monitorData.setQueue1Count((Long) next.get("queue1"));
				monitorData.setQueue2Count((Long) next.get("queue2"));
				monitorData.setQueue3Count((Long) next.get("queue3"));
				monitorData.setQueue4Count((Long) next.get("queue4"));
				monitorData.setQueue5Count((Long) next.get("queue5"));
				result.add(monitorData);
			}
		} finally {
			cursor.close();
		}
		return result;
	}
}
