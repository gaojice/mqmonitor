package com.zendaimoney.mqmonitor.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zendaimoney.mqmonitor.entity.MonitorData;
import com.zendaimoney.mqmonitor.service.mongo.MongoService;

public class DataServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Date start=new Date(Long.valueOf(req.getParameter("start")));
		Date end=new Date(Long.valueOf(req.getParameter("end")));
		
		MongoService mongoService=new MongoService();
		List<MonitorData> data = mongoService.getData("", start, end);
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer = resp.getWriter();
		writer.println("date,response_read_only,response,request,request_read_only,dlq");
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		for (MonitorData monitorData : data) {
			writer.println(df.format(monitorData.getTime())+","+monitorData.getQueue1Count()+","+monitorData.getQueue2Count()+","+monitorData.getQueue3Count()+","+monitorData.getQueue4Count()+","+monitorData.getQueue5Count());
		}
	}
}
