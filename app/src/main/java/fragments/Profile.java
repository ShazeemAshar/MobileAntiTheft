package fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import activities.PhoneNumberRegistration;
import activities.PinRegistration;
import androiddoctors.mobileantitheft.R;
import databases.SQLiteHandler;

public class Profile extends Fragment {

    TextView name,email,password,pin,phone,emergencyContact1,emergencyContact2,emergencyContact3;
    ImageView editIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        name =  view.findViewById(R.id.name);
        email =  view.findViewById(R.id.email);
        password =  view.findViewById(R.id.password);
        pin =  view.findViewById(R.id.pin);
        phone =  view.findViewById(R.id.phone);
        emergencyContact1 = view.findViewById(R.id.emergencyContact1);
        emergencyContact2 = view.findViewById(R.id.emergencyContact2);
        emergencyContact3 = view.findViewById(R.id.emergencyContact3);
        editIcon =  view.findViewById(R.id.edit);


        SQLiteHandler sqLiteHandler = new SQLiteHandler(getActivity());
        Cursor cursor = sqLiteHandler.getUserData();
        cursor.moveToFirst();

        name.append(cursor.getString(1));
        email.append(cursor.getString(2));
        password.append(cursor.getString(3));
        pin.append(cursor.getString(4));
        phone.append(cursor.getString(5));
        emergencyContact1.append(cursor.getString(6));
        emergencyContact2.append(cursor.getString(7));
        emergencyContact3.append(cursor.getString(8));


        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Change PIN", "Change Phone No"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent5 = new Intent(getActivity(), PinRegistration.class);
                                intent5.putExtra("Flag", "ChangePIN");
                                startActivity(intent5);
                                break;
                            case 1:
                                Intent intent6 = new Intent(getActivity(), PhoneNumberRegistration.class);
                                intent6.putExtra("Flag", "ChangePhoneNo");
                                startActivity(intent6);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
    }

}
