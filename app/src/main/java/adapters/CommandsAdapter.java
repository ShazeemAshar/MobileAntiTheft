package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pk.encodersolutions.mobileantitheft.R;

/**
 * Created by Sadda on 17-Jan-18.
 */

public class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.MyViewHolder>{

    private String[] commands,descriptions;
    private Context context;

    public CommandsAdapter(Context context, String[] commands, String[] descriptions){
        this.context = context;
        this.commands = commands;
        this.descriptions = descriptions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cardview_commands,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvCommand.setText(commands[position]);
        holder.tvDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return commands.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvCommand,tvDescription;

        MyViewHolder(View itemView) {
            super(itemView);
            tvCommand = itemView.findViewById(R.id.tvCommand);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
