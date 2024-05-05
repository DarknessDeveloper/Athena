package lol.athena.plugin;

public class MissingDependencyException extends Exception {

	private static final long serialVersionUID = -16988941083846110L;

	public MissingDependencyException(String message) {
		super(message);
	}

}
