package com.baffalotech.integration.configuration;

import java.util.concurrent.Executor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "server.int", ignoreUnknownFields = true)
public class IntegrationServerProperties {
	
	/*
	 * 容器默认的名字
	 */
	private String containerName = "default";

	/**
	 * 服务端-IO线程数 注: (0 = cpu核数 * 2 )
	 */
	private int serverIoThreads = 0;

	/**
	 * 服务端 - 线程执行器
	 */
	private Executor serverHandlerExecutor = null;

	/**
	 * 服务端-io线程执行调度与执行io事件的百分比. 注:(100=每次只执行一次调度工作, 其他都执行io事件), 并发高的时候可以设置最大
	 */
	private int serverIoRatio = 100;

	/**
	 * 每次调用servlet的 OutputStream.Writer()方法写入的最大堆字节,超出后用堆外内存
	 */
	private int responseWriterChunkMaxHeapByteLength = 4096;
	
	private int minWorkerThread = 40;
	
	private int maxWorkerThread = 500;
	
	private int workerQueueSize = 0;
	
	//access log pattern
	@NestedConfigurationProperty
	private Accesslog accesslog = new Accesslog();

	public int getResponseWriterChunkMaxHeapByteLength() {
		return responseWriterChunkMaxHeapByteLength;
	}

	public void setResponseWriterChunkMaxHeapByteLength(int responseWriterChunkMaxHeapByteLength) {
		this.responseWriterChunkMaxHeapByteLength = responseWriterChunkMaxHeapByteLength;
	}

	public int getServerIoThreads() {
		return serverIoThreads;
	}

	public void setServerIoThreads(int serverIoThreads) {
		this.serverIoThreads = serverIoThreads;
	}

	public int getServerIoRatio() {
		return serverIoRatio;
	}

	public void setServerIoRatio(int serverIoRatio) {
		this.serverIoRatio = serverIoRatio;
	}

	public Executor getServerHandlerExecutor() {
		return serverHandlerExecutor;
	}

	public void setServerHandlerExecutor(Executor serverHandlerExecutor) {
		this.serverHandlerExecutor = serverHandlerExecutor;
	}
	
	public String getContainerName() {
		return containerName;
	}
	
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public int getMinWorkerThread() {
		return minWorkerThread;
	}

	public void setMinWorkerThread(int minWorkerThread) {
		this.minWorkerThread = minWorkerThread;
	}

	public int getMaxWorkerThread() {
		return maxWorkerThread;
	}

	public void setMaxWorkerThread(int maxWorkerThread) {
		this.maxWorkerThread = maxWorkerThread;
	}

	public int getWorkerQueueSize() {
		return workerQueueSize;
	}

	public void setWorkerQueueSize(int workerQueueSize) {
		this.workerQueueSize = workerQueueSize;
	}
	
	public Accesslog getAccesslog() {
		return accesslog;
	}
	
	public void setAccesslog(Accesslog accesslog) {
		this.accesslog = accesslog;
	}

	@Override
	public String toString() {
		return "IntegrationProperties{" + "serverWorkerCount=" + serverIoThreads + ", serverIoRatio=" + serverIoRatio
				+ ", responseWriterChunkMaxHeapByteLength=" + responseWriterChunkMaxHeapByteLength + '}';
	}
	
	public static class Accesslog {

		/**
		 * Enable access log.
		 */
		private boolean enabled = false;

		/**
		 * Format pattern for access logs.
		 */
		private String pattern = "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\" %T";

		/**
		 * Directory in which log files are created. Can be absolute or relative to the
		 * Tomcat base dir.
		 */
		private String directory = "logs";

		/**
		 * Log file name prefix.
		 */
		protected String prefix = "access_log";

		/**
		 * Log file name suffix.
		 */
		private String suffix = ".log";

		/**
		 * Whether to enable access log rotation.
		 */
		private boolean rotate = true;

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getPattern() {
			return this.pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		public String getDirectory() {
			return this.directory;
		}

		public void setDirectory(String directory) {
			this.directory = directory;
		}

		public String getPrefix() {
			return this.prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getSuffix() {
			return this.suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public boolean isRotate() {
			return this.rotate;
		}

		public void setRotate(boolean rotate) {
			this.rotate = rotate;
		}
	}
}
