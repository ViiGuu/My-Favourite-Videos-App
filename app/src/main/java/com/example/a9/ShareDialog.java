package com.example.a9;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ShareDialog extends DialogFragment {

    //interface för lyssnaren till dialogen
    public interface ShareDialogListener {
        void onDialogPositiveClick(String email);
    }

    private ShareDialogListener listener;

    //metoden för när dialogen skapas, använder en egengjord xml-layout så
    //att användaren kan fylla i email adress till vem videon ska delas till.
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.share_video_dialog, null);

        EditText email = dialogView.findViewById(R.id.emailAdress);
        Button share = dialogView.findViewById(R.id.buttonShareVideo);
        Button cancel = dialogView.findViewById(R.id.buttonCancel);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        share.setOnClickListener(view -> {
            if(!email.getText().toString().equals("")) {
                listener.onDialogPositiveClick(email.getText().toString());
                dialog.dismiss();
            } else
                Toast.makeText(getActivity(), "Please enter an email address", Toast.LENGTH_SHORT).show();

        });

        cancel.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    //onAttach ser till att lyssnaren kopplas till fragmentet/aktiviteten som skapar dialogen.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { listener = (ShareDialog.ShareDialogListener) context; }
        catch (ClassCastException e) {
            throw new ClassCastException(context + " failed to attach Listener for the ShareDialog");
        }
    }
}
