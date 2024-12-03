package DBD_env_unification;

import java.sql.Timestamp;

public class Announcement {
    private int textNumber;
    private String title;
    private String managerName;
    private String type;
    private Timestamp createDate;
    private int views;
    private String content;

    // 생성자
    public Announcement(int textNumber, String title, String managerName, String type,
                        Timestamp createDate, int views, String content) {
        this.textNumber = textNumber;
        this.title = title;
        this.managerName = managerName;
        this.type = type;
        this.createDate = createDate;
        this.views = views;
        this.content = content;
    }

    // Getter 및 Setter
    public int getTextNumber() {
        return textNumber;
    }

    public void setTextNumber(int textNumber) {
        this.textNumber = textNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // 출력 메서드 (옵션)
    public void printAnnouncement() {
        System.out.println("Text Number: " + textNumber);
        System.out.println("Title: " + title);
        System.out.println("Manager Name: " + managerName);
        System.out.println("Type: " + type);
        System.out.println("Create Date: " + createDate);
        System.out.println("Views: " + views);
        System.out.println("Content: " + content);
        System.out.println("---------------------------------------------------");
    }
}
