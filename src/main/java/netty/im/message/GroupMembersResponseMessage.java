package netty.im.message;

import java.util.Set;

public class GroupMembersResponseMessage extends Message {

    private Set<String> members;

    @Override
    public String toString() {
        return "GroupMembersResponseMessage{" +
                "members=" + members +
                '}';
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
