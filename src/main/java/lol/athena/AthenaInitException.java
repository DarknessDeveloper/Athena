package lol.athena;

public class AthenaInitException extends Exception {
	private static final long serialVersionUID = -2462801422409692568L;

	public AthenaInitException() {
	}
	
	public AthenaInitException(String message) {
		super(message);
	}
	
	public AthenaInitException(Throwable throwable) {
		super(throwable);
	}
	
	
}
