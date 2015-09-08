package br.com.comoqueta.comoquetasaude.android.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import br.com.comoqueta.comoquetasaude.android.R;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 12/07/2015
 * @since #
 */
public class TimePickerFragment extends DialogFragment {

    private static final String PACKAGE = TimePickerFragment.class.getPackage().getName();

    public static final String EXTRA_TIME = PACKAGE + ".time";

    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date time) {
        TimePickerFragment fragment = new TimePickerFragment();

        if (time != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_TIME, time);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Date time = null;
        if (getArguments() != null) {
            time = (Date) getArguments().getSerializable(ARG_TIME);
        }

        Calendar calendar = Calendar.getInstance();
        if (time != null) {
            calendar.setTime(time);
        }
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.time_picker_dialog, (ViewGroup) getTargetFragment().getView(),
                        false);

        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_title_time_picker)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = Calendar.getInstance();

                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                int hour = mTimePicker.getCurrentHour();
                                int minute = mTimePicker.getCurrentMinute();
                                Date time = new GregorianCalendar(year, month, day, hour, minute)
                                        .getTime();
                                sendResult(Activity.RESULT_OK, time);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date time) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
    
}
