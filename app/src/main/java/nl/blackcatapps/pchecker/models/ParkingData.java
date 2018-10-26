package nl.blackcatapps.pchecker.models;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import nl.blackcatapps.pchecker.R;
import nl.blackcatapps.pchecker.Utils;

/**
 * Created by Sander on 26-11-2016.
 */

public class ParkingData extends AbstractItem<ParkingData, ParkingData.ViewHolder> {


    /**
     * parking_id : denbosch_900000041
     * name : P04 Paleiskwartier
     * description :
     * parking_capacity : 644
     * vacant_spaces : 252
     * full : 0
     * open : 1
     * last_updated : 1480173597
     */

    private String parking_id;
    private String name;
    private String description;
    private String parking_capacity;
    private String vacant_spaces;
    private String full;
    private String open;
    private String last_updated;

    public String getParking_id() {
        return parking_id;
    }

    public void setParking_id(String parking_id) {
        this.parking_id = parking_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParking_capacity() {
        return parking_capacity;
    }

    public void setParking_capacity(String parking_capacity) {
        this.parking_capacity = parking_capacity;
    }

    public String getVacant_spaces() {
        return vacant_spaces;
    }

    public void setVacant_spaces(String vacant_spaces) {
        this.vacant_spaces = vacant_spaces;
    }

    public boolean getFull() {
        if (full.equals("0")) {
            return false;
        } else {
            return true;
        }

    }

    public void setFull(String full) {
        this.full = full;
    }

    public boolean getOpen() {
        if (open.equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public String toString() {
        return "ParkingData{" +
                "parking_id='" + parking_id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parking_capacity='" + parking_capacity + '\'' +
                ", vacant_spaces='" + vacant_spaces + '\'' +
                ", full='" + full + '\'' +
                ", open='" + open + '\'' +
                ", last_updated='" + last_updated + '\'' +
                '}';
    }


    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.garage_title;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.garage_card;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        //bind our data
        //set the text for the name
        viewHolder.name.setText(name);
        viewHolder.numberProgressBar.setMax(100);
        //set the text for the description or hide
        int percentage = calculatePercentageFree(
                Integer.parseInt(getParking_capacity()),
                Integer.parseInt(getVacant_spaces()));

        int color;

        if (percentage >= 40) {
            color = Color.parseColor("#00c853");
            viewHolder.numberProgressBar.setReachedBarColor(color);
            viewHolder.free_spots.setTextColor(color);
        }


        if (percentage < 40 && percentage > 15) {
            color = Color.parseColor("#ffd600");
            viewHolder.numberProgressBar.setReachedBarColor(color);
            viewHolder.free_spots.setTextColor(color);
        } else if (percentage <= 15 && percentage > 5) {
            color = Color.parseColor("#FFFF6F00");
            viewHolder.numberProgressBar.setReachedBarColor(color);
            viewHolder.free_spots.setTextColor(color);
        }
        if (Integer.parseInt(full) == 1 || percentage <= 5) {
            color = Color.parseColor("#d50000");
            viewHolder.numberProgressBar.setReachedBarColor(color);
            viewHolder.free_spots.setTextColor(color);
        }

        viewHolder.free_spots.setText(getVacant_spaces());

        viewHolder.numberProgressBar.setProgress(percentage);
        viewHolder.numberProgressBar.invalidate();

        String message = "Er zijn nog <b>%s</b> parkeerplaatsen van de %s beschikbaar.";
        message = String.format(message, getVacant_spaces(), getParking_capacity());
        viewHolder.message.setText(Html.fromHtml((message)));


        String lastUpdate = "Laatste update: %s";
        lastUpdate = String.format(lastUpdate, Utils.getDate(Long.parseLong(getLast_updated()) * 1000));
        viewHolder.last_update.setText(lastUpdate);


    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.numberProgressBar.setProgress(0);
        holder.free_spots.setText(null);
    }

    private int calculatePercentageFree(int total, int free) {
        float percent = (free * 100.0f) / total;
        return (int) percent;
    }


    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name, message, free_spots, last_update;
        protected NumberProgressBar numberProgressBar;

        public ViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.garage_title);
            this.numberProgressBar = (NumberProgressBar) view.findViewById(R.id.garage_progress);
            this.message = (TextView) view.findViewById(R.id.garage_message);
            this.free_spots = (TextView) view.findViewById(R.id.garage_free_spots);
            this.last_update = (TextView) view.findViewById(R.id.garage_update);

        }
    }
}
