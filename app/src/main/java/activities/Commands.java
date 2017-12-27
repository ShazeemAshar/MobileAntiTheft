package activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androiddoctors.mobileantitheft.BaseActivity;
import androiddoctors.mobileantitheft.R;

public class Commands extends BaseActivity {

    ListView commandsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_commands, null, false);
        drawerLayout.addView(contentView, 0);

        initViews();


    }

    private void initViews() {
        setTitle("Commands");

        commandsList = findViewById(R.id.commandsList);

        String[] commands = {
                "\nDelete Logs#PIN" + "\nThis command will delete all the messages\n",
                "\nDelete Contacts#PIN" + "\nThis command will delete all the contacts\n",
                "\nWipe Memory#PIN" + "\nThis command will delete all the gallery images and videos\n",
                "\nNormal Mode#PIN" + "\nThis command will switch the phone from Silent to Sound Mode\n",
                "\nBackup Contacts#PIN" + "\nThis command will upload your contacts to your web account\n",
                "\nLock Phone#PIN" + "\nThis command will lock the phone\n",
                "\nFind Mobile#PIN" + "\nThis command will set last active location of your Mobile on your web account\n",
                "\nSuper User#PIN" + "\nThis is a super user command which causes the mobile to delete all the messages,logs,contacts," +
                        "memory, and Locks the phone\n",
                "\nFactory Reset#PIN" + "\nThis command will Factory Reset your Mobile\n"};

        ArrayAdapter adapter = new ArrayAdapter<>(Commands.this, android.R.layout.simple_list_item_1, commands);

        commandsList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(this, HomeActivity.class));
        }

    }
}
