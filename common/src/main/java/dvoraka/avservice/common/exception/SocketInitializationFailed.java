package dvoraka.avservice.common.exception;

public class SocketInitializationFailed extends AvException {

    private static final long serialVersionUID = -7295508549939654548L;


    public SocketInitializationFailed(String message) {
        super(message);
    }

    public SocketInitializationFailed(Throwable cause) {
        super(cause);
    }
}
