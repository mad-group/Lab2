package group3.myapplicationlab2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class GroupModification extends AppCompatActivity {
    private String group_name;
    private String group_desc;
    private String group_id;
/*    private EditText et_name = (EditText) findViewById(R.id.new_group);
    private EditText et_desc = (EditText) findViewById(R.id.group_description);
    private EditText pin = (EditText) findViewById(R.id.group_pin);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_modifcation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         EditText et_name = (EditText) findViewById(R.id.new_group);
         EditText et_desc = (EditText) findViewById(R.id.group_description);
      EditText pin = (EditText) findViewById(R.id.group_pin);
        group_desc = getIntent().getStringExtra("group_desc");
        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");
        setTitle(group_name + " - Modify");


        et_name.setText(group_name);
        et_desc.setText(group_desc);
        Button bt = (Button) findViewById(R.id.new_part_btn);
        bt.setText(R.string.bt_modify_group);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Objects> hm = new HashMap();
                DatabaseReference groups = FirebaseDatabase.getInstance().getReference("Users")
                        .child(group_id);

            }
        });

    }

}
