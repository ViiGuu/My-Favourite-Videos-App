package com.example.a9;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteDialog extends DialogFragment {
    //dialog för att bekräfta raderingen av en video i listan

    //interface för lyssnaren
    public interface DeleteDialogListener {
        void onDialogPositiveClick();
    }

    private DeleteDialogListener listener;

    //om användaren trycker Yes så tas videon bort från listan i MainActivity
    //(meddelas med lyssnaren)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this video?")
                .setPositiveButton("Yes", (dialog, which) ->
        {
            listener.onDialogPositiveClick();
        }).setNegativeButton("No", (dialog, which) -> {
            //inget händer
        });

        return builder.create();
    }


    //onAttach ser till att lyssnaren kopplas till fragmentet som skapar dialogen.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { listener = (DeleteDialogListener) context; }
        catch (ClassCastException e) {
            throw new ClassCastException(context + " failed to attach Listener for the DeleteDialog");
        }
    }
}