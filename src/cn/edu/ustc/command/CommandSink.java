package cn.edu.ustc.command;

public interface CommandSink {
	abstract void onCommandExcuted(int result, Command cmd, Object[]... args);
}
