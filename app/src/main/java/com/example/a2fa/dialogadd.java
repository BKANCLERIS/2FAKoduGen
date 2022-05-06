package com.example.a2fa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class dialogadd extends AppCompatDialogFragment {
    private EditText edittexttotp;
    private EditText edittextissuer;
    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);

        builder.setView(view)
                .setTitle("Prideti 2FA ranka")
                .setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String totp = edittexttotp.getText().toString();
                        String issuer = edittextissuer.getText().toString();
                        listener.applyTexts(totp,issuer);
                    }
                });
        edittexttotp = view.findViewById(R.id.edit_TOTP);
        edittextissuer = view.findViewById(R.id.edit_issuerpavad);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialog");
        }
    }

    public interface DialogListener{
        void applyTexts(String totp,String issuer);
    }
}
