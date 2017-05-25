package group3.myapplicationlab2;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * Created by anr.putina on 25/05/17.
 */

public class DataBaseProxy{

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference referenceGroups = firebaseDatabase.getReference("Groups");
    private DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
    private final String membersFieldInGroups = "members2";

    public String insertGroup(Group group){
        String groupId = referenceGroups.push().getKey();
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

    public DatabaseReference getReferenceGroups(){
        return referenceGroups;
    }

}
