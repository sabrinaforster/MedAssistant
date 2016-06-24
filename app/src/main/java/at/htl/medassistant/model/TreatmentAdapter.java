package at.htl.medassistant.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import at.htl.medassistant.MedicineDetailsActivity;
import at.htl.medassistant.R;
import at.htl.medassistant.entity.MedType;
import at.htl.medassistant.entity.Treatment;

/**
 * Created by testC01 on 06.06.16.
 */
public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.MyViewHolder>{
    private static final String TAG = MedicineDetailsActivity.class.getName();

    private List<Treatment> treatmentList;
    private Context context;

    public static String EDIT_MEDICENAME = "Medicinename";
    public static String EDIT_PERIODICITY_IN_DAYS = "periodicityInDays";
    public static String EDIT_ACTIVE_SUBSTANCE = "ActiveSubstance";
    public static String EDIT_STARTDATE = "Startdate";
    public static String EDIT_ENDDATE = "Enddate";
    public static String EDIT_TIME_OF_TAKING = "TimeOfTaking";
    public static String EDIT_MEDICINE_TYPE = "MedicineType";
    public static String EDIT_NOTE = "Note";

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, activeSubstance, startdate, enddate, timeOfTaking, periodicityInDays, medType, note;
        public TextView labelName, labelActiveSubstance, labelStartDate, labelEnddate, labelTimeOfTaking, labelPeriodicityInDays;
        private Button itemMenuButton;

        private MedicineDatabaseHelper db;

        public MyViewHolder(final View view, final TreatmentAdapter treatmentAdapter) {
            super(view);
            this.medType = (TextView) view.findViewById(R.id.medType);
            this.name = (TextView) view.findViewById(R.id.tvmedicineName);
            this.periodicityInDays = (TextView) view.findViewById(R.id.tvPeriodicityInDays);
            this.startdate = (TextView) view.findViewById(R.id.tvStartDate);
            this.enddate = (TextView) view.findViewById(R.id.tvEndDate);
            this.timeOfTaking = (TextView) view.findViewById(R.id.tvTimeOfTaking);
            this.note = (TextView) view.findViewById(R.id.tvNote);

            this.labelName = (TextView) view.findViewById(R.id.lableMedicineName);
            this.labelStartDate = (TextView) view.findViewById(R.id.lableStartDate);
            this.labelEnddate = (TextView) view.findViewById(R.id.lableEnddate);
            this.labelTimeOfTaking = (TextView) view.findViewById(R.id.lableTimeOfTaking);
            this.labelPeriodicityInDays = (TextView) view.findViewById(R.id.lablePeriodicityInDays);

            db = MedicineDatabaseHelper.getInstance(view.getContext());

            itemMenuButton = (Button) view.findViewById(R.id.popupmenubutton);

            itemMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //http://www.javatpoint.com/android-popup-menu-example

                    PopupMenu popup = new PopupMenu(view.getContext(), itemMenuButton);
                    popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.DeleteItem:

                                    if (db.deleteTreatmentByUserMedicine(db.findCurrentUserId(), name.getText().toString())) {
                                        Toast.makeText(view.getContext(), "Delete " + name.getText().toString(), Toast.LENGTH_LONG).show();

                                        treatmentAdapter.treatmentList.clear();
                                        treatmentAdapter.treatmentList.addAll(db.getTreatments());
                                        treatmentAdapter.notifyDataSetChanged();

                                    }
                                    break;
                                case R.id.EditItem:
                                    //Toast.makeText(view.getContext(), "Edit "+name.getText().toString(), Toast.LENGTH_SHORT).show();
                                    Treatment treatment = new Treatment(
                                            db.findUserById(db.findCurrentUserId()),
                                            db.findMedicineByName(name.getText().toString()),
                                            startdate.getText().toString(),
                                            enddate.getText().toString(),
                                            timeOfTaking.getText().toString(),
                                            note.getText().toString());

                                    Intent intent = new Intent(view.getContext(), MedicineDetailsActivity.class);
                                    intent.putExtra(EDIT_MEDICENAME, treatment.getMedicine().getName());
                                    intent.putExtra(EDIT_PERIODICITY_IN_DAYS, treatment.getMedicine().getPeriodicityInDays()+"");
                                    intent.putExtra(EDIT_ACTIVE_SUBSTANCE, treatment.getMedicine().getActiveSubstance());
                                    intent.putExtra(EDIT_STARTDATE, treatment.getStartDateToString());
                                    intent.putExtra(EDIT_ENDDATE, treatment.getEndDateToString());
                                    intent.putExtra(EDIT_TIME_OF_TAKING, treatment.getTimeOfTakingToString());
                                    intent.putExtra(EDIT_MEDICINE_TYPE, treatment.getMedicine().getMedType().toString());
                                    intent.putExtra(EDIT_NOTE, treatment.getNote().toString());

                                    view.getContext().startActivity(intent);

                                    /*treatmentAdapter.treatmentList.clear();
                                    treatmentAdapter.treatmentList.addAll(db.getTreatments());
                                    treatmentAdapter.notifyDataSetChanged();*/

                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();
                }
            });
        }
    }

    public TreatmentAdapter(List<Treatment> contractList, Context context) {
        this.treatmentList = contractList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.treatment_item_row, parent, false);
        return new MyViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Treatment treatment = treatmentList.get(position);

        holder.name.setText(treatment.getMedicine().getName());
        holder.periodicityInDays.setText(treatment.getMedicine().getPeriodicityInDays()+"");
        holder.startdate.setText(treatment.getStartDateToString());
        holder.enddate.setText(treatment.getEndDateToString());
        holder.timeOfTaking.setText(treatment.getTimeOfTakingToString());
        holder.medType.setText(treatment.getMedicine().getMedType().toString());
        holder.note.setText(treatment.getNote());

        if (treatment.getMedicine().getMedType().equals(MedType.PHARMACEUTICAL)) {
            holder.labelStartDate.setVisibility(View.GONE);
            holder.startdate.setVisibility(View.GONE);

            holder.labelEnddate.setVisibility(View.GONE);
            holder.enddate.setVisibility(View.GONE);
        } else {
            holder.labelPeriodicityInDays.setVisibility(View.GONE);
            holder.periodicityInDays.setVisibility(View.GONE);

            holder.labelStartDate.setVisibility(View.GONE);
            holder.startdate.setVisibility(View.GONE);

            holder.labelTimeOfTaking.setText("Reminder Time");
            holder.labelEnddate.setText("Reminder Date");
        }

    }

    @Override
    public int getItemCount() {
        return treatmentList.size();
    }

    public void refreshList(){
        treatmentList = MedicineDatabaseHelper.getInstance(context).getTreatments();
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
