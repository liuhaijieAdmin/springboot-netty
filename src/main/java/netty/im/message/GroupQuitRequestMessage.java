package netty.im.message;

public class GroupQuitRequestMessage extends Message {
    private String groupName;

    private String username;

    @Override
    public String toString() {
        return "GroupQuitRequestMessage{" +
                "groupName='" + groupName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GroupQuitRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GroupQuitRequestMessage;
    }
}
