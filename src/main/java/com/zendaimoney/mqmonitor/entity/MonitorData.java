package com.zendaimoney.mqmonitor.entity;

import java.util.Date;

public class MonitorData {
	private Date time;
	private Long queue1Count;
	private Long queue2Count;
	private Long queue3Count;
	private Long queue4Count;
	private Long queue5Count;
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Long getQueue1Count() {
		return queue1Count;
	}
	public void setQueue1Count(Long queue1Count) {
		this.queue1Count = queue1Count;
	}
	public Long getQueue2Count() {
		return queue2Count;
	}
	public void setQueue2Count(Long queue2Count) {
		this.queue2Count = queue2Count;
	}
	public Long getQueue3Count() {
		return queue3Count;
	}
	public void setQueue3Count(Long queue3Count) {
		this.queue3Count = queue3Count;
	}
	public Long getQueue4Count() {
		return queue4Count;
	}
	public void setQueue4Count(Long queue4Count) {
		this.queue4Count = queue4Count;
	}
	public Long getQueue5Count() {
		return queue5Count;
	}
	public void setQueue5Count(Long queue5Count) {
		this.queue5Count = queue5Count;
	}
	
}
