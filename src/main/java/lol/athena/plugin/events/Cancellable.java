package lol.athena.plugin.events;

public interface Cancellable {

	boolean isCancelled();
	void setCancelled(boolean cancel);
	
}
