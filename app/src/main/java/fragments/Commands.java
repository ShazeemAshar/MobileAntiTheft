package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androiddoctors.mobileantitheft.R;

public class Commands extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commands,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Commands");

        ListView commandsList = view.findViewById(R.id.commandsList);
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

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, commands);

        commandsList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
