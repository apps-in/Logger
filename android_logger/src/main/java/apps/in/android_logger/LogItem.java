package apps.in.android_logger;

public class LogItem {

    private String sessionId;

    private String text;

    public LogItem(){

    }

    public LogItem(String sessionId, String text) {
        this.sessionId = sessionId;
        this.text = text;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
