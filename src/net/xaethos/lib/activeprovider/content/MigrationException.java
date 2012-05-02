package net.xaethos.lib.activeprovider.content;

@SuppressWarnings("UnusedDeclaration")
public class MigrationException extends RuntimeException {
	private static final long serialVersionUID = -5944780384961476618L;

	public MigrationException() {
		super();
	}

	public MigrationException(String detailMessage) {
		super(detailMessage);
	}

	public MigrationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MigrationException(Throwable throwable) {
		super(throwable);
	}

}
