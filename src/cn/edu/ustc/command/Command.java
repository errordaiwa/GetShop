package cn.edu.ustc.command;

public abstract class Command {
	
	public abstract void onPrepare();
	
	public abstract void onExcute();
	
	public abstract void onParse();

}
