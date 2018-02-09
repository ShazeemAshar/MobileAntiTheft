package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapters.CommandsAdapter;
import pk.encodersolutions.mobileantitheft.R;

public class Commands extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commands,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Commands");

        RecyclerView rvCommands = view.findViewById(R.id.rvCommands);
        rvCommands.setLayoutManager(new LinearLayoutManager(getActivity()));

        String[] commands = {
                "Lock Phone#PIN",
                "Wipe Memory#PIN",
                "Delete Contacts#PIN",
                "Factory Reset#PIN",
                "Delete Logs#PIN",
                "Backup Contacts#PIN",
                "Normal Mode#PIN",
                "Find Mobile#PIN",
                "Super User#PIN",

        };

        String[] descriptions = {
                "This command will lock the phone",
                "This command will wipe phone storage",
                "This command will delete all the contacts",
                "This command will Factory Reset your Mobile",
                "This command will delete all the Calls & Messages Logs",
                "This command will upload your contacts to your web account",
                "This command will switch the phone from Silent to Sound Mode",
                "This command will show the last active location of your Mobile on your web account",
                "This is a super user command which causes the mobile to delete all the Logs, Contacts, " +
                        "Memory, and Locks the phone"

        };

        CommandsAdapter adapter = new CommandsAdapter(getActivity(),commands,descriptions);
        rvCommands.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
