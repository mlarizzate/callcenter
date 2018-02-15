package ar.com.exam.callcenter.model;

public enum RejectReason {
    CENTRAL_OVERLOAD("Overloaded Central"),
    UNAVAILABLE_AGENTS("Unavailable Agents"),
    OTHER_REASON("Unknown Reason");

    String description;
    RejectReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
