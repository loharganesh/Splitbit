package app.splitbit.GroupSplits.Create;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import app.splitbit.R;

public class SelectMembers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_members);
    }

    public void back(View view){
        onBackPressed();
    }

}
