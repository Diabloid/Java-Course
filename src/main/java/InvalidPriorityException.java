public class InvalidPriorityException extends TaskDomainException {
    private final int wrongPriority;

    public InvalidPriorityException(String message, int wrongPriority) {
        super(message);
        this.wrongPriority = wrongPriority;
    }

    public int getWrongPriority() {
        return wrongPriority;
    }
}