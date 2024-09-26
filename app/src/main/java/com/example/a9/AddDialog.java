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

public class AddDialog extends DialogFragment {

    public interface AddDialogListener {
        void onDialogPositiveClick(String name, String url);
    }

    private AddDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_video_dialog, null);

        EditText videoName = dialogView.findViewById(R.id.videoName);
        EditText videoURL = dialogView.findViewById(R.id.videoURL);
        Button addVideo = dialogView.findViewById(R.id.buttonAddVideo);
        Button cancel = dialogView.findViewById(R.id.buttonCancel);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        addVideo.setOnClickListener(view -> {
            String name = videoName.getText().toString();
            String url = videoURL.getText().toString();
            if(!name.equals("") && !url.equals("")) {
                listener.onDialogPositiveClick(name, url);
                dialog.dismiss();
            } else
                Toast.makeText(getActivity(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
        });

        cancel.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { listener = (AddDialog.AddDialogListener) context; }
        catch (ClassCastException e) {
            throw new ClassCastException(context + " failed to attach Listener for the AddDialog");
        }
    }
}
