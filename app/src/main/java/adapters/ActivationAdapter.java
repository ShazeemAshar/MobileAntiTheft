package adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import pk.encodersolutions.mobileantitheft.R;

import static android.content.Context.MODE_PRIVATE;

public class ActivationAdapter extends BaseAdapter{

    private String[]title,description;
    private LayoutInflater layoutInflater;

    private SharedPreferences sharedPreferences;

    public ActivationAdapter(Context context, String[] title, String[] description){
        this.title = title;
        this.description = description;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        sharedPreferences = context.getSharedPreferences("ActivationPref",MODE_PRIVATE);
    }

    @Override
    public boolean isEnabled(int position) {

        switch (position){
            case 0:
                return sharedPreferences.getBoolean("Step1", true);

            case 1:
                return sharedPreferences.getBoolean("Step2", false);

            case 2:
                return sharedPreferences.getBoolean("Step3", false);

            default:
                return false;
        }
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = layoutInflater.inflate(R.layout.custom_row_activation,null);

        TextView title1 = view.findViewById(R.id.title);
        TextView description1 = view.findViewById(R.id.description);
        TextView position1 = view.findViewById(R.id.position);

        title1.setText(title[position]);
        description1.setText(description[position]);
        position1.setText(String.valueOf(position+1)+".");

        if (!isEnabled(position)){
            view.setAlpha((float) 0.5);
        }

        return view;
    }
}
