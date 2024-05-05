package lol.athena;

public class AthenaRuntimeException extends RuntimeException {

	
	private static final long serialVersionUID = -8686167769017966457L;
	
	public AthenaRuntimeException(String message) {
		super(message);
	}
	
	public AthenaRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
