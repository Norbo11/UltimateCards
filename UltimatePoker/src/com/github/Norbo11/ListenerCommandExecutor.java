package com.github.Norbo11;

import org.bukkit.command.*;

public class ListenerCommandExecutor implements CommandExecutor {

	UltimatePoker p;
	public ListenerCommandExecutor(UltimatePoker p) {
		this.p = p;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		return true;
	}
}
