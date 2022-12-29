package netty.im.message;

import java.util.Set;

public class GroupCreateRequestMessage extends Message {
    private String groupName;

    @Override
    public String toString() {
        return "GroupCreateRequestMessage{" +
                "groupName='" + groupName + '\'' +
                ", members=" + members +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    private Set<String> members;

    public GroupCreateRequestMessage(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupCreateRequestMessage;
    }
}
