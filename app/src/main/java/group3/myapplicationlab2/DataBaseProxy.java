package group3.myapplicationlab2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by anr.putina on 26/05/17.
 */

public class DataBaseProxy {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference referenceGroups = firebaseDatabase.getReference("Groups");
    private DatabaseReference referenceUser = firebaseDatabase.getReference("Users");

    private final String membersFieldInGroups = "members2";

    public String insertGroup(Group group){
        String groupId = referenceGroups.push().getKey();
        group.setId(groupId);
        referenceGroups.child(groupId).setValue(group);
        return groupId;
    }

    public void insertMemberInGroup(String groupId, String userUid, GroupMember groupMember){
        referenceGroups.child(groupId)
                        .child(membersFieldInGroups)
                        .child(userUid)
                        .setValue(groupMember);
        return;
    }

    public void insertGroupPreview(GroupPreview groupPreview, String userId, String groupId){

        referenceUser.child(userId)
                        .child("groupsHash")
                        .child(groupId)
                        .setValue(groupPreview);
        return;

    }

}
