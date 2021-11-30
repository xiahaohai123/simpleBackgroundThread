package top.summersea.backgroundthread;


public class TaskVO {

    private String name;

    private String content;

    public boolean isParamAvailable() {
        return name != null && !name.isEmpty() && content != null && !content.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
