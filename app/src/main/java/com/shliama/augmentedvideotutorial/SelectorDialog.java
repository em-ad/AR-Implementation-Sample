package com.shliama.augmentedvideotutorial;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class SelectorDialog extends Dialog implements View.OnClickListener {

    ArrayList<MagazineResult> versions = new ArrayList<>();
    RadioGroup group;
    ItemSelectedInterface callback;

    public SelectorDialog(@NonNull Context context, ArrayList<MagazineResult> versions, ItemSelectedInterface callback) {
        super(context);
        this.versions.addAll(versions);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selector);
        group = findViewById(R.id.radioGroup);
        for (int i = 0; i < versions.size(); i++) {
            RadioButton button = new RadioButton(getContext());
            button.setText(versions.get(i).version + " " + versions.get(i).title);
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setId(i + 220);
            button.setGravity(Gravity.RIGHT);
            button.setOnClickListener(this);
            group.addView(button);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        callback.itemSelected(versions.get(id - 220).version);
        dismiss();
    }
}
