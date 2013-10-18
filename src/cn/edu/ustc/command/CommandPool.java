package cn.edu.ustc.command;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CommandPool {
	private static CommandPool instance = new CommandPool();
	
	private ThreadPoolExecutor threadPool;
	
	private CommandPool(){
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public static CommandPool getInstance(){
		return instance;
	}
	
	
	public synchronized void add(final Command command){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				command.onPrepare();
				command.onExcute();
				command.onParse();
			}
		});
		threadPool.execute(t);
	}

}
