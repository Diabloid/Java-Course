public class InvalidTimeException extends TaskDomainException {
    private final double wrongTime;

    public InvalidTimeException(String message, double wrongTime) {
        super(message);
        this.wrongTime = wrongTime;
    }

    public double getWrongTime() {
        return wrongTime;
    }
}