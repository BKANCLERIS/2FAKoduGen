package com.example.a2fa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import de.taimos.totp.TOTP;

public class MainActivity extends AppCompatActivity implements dialogadd.DialogListener {
    ArrayList<elementas> elementasList;
    listviewAdapteris adapter;
    private ListView listView;
    ActivityResultLauncher<Intent> AddActivityResultLauncher;
    private FloatingActionButton buttonadd;
    int pos;
    String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        elementasList = new ArrayList<elementas>();
        buttonadd = (FloatingActionButton) findViewById(R.id.dab);
        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }

        });

        atkurtiduomenis();


        AddActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String duom = data.getStringExtra("qrdata");
                            String[] qrData = duom.split(";");
                            String totp = qrData[0];
                            String issuer = qrData[1];
                            elementas add = new elementas(totp, issuer, MainActivity.this);
                            elementasList.add(add);

                            isaugotiDuomenis(gautiduomenis(),totp,issuer);
                            System.out.println(gautiduomenis());
                            setupList();
                        }
                    }
                }
        );
        setupList();

        ListView listView= (ListView)  findViewById(R.id.listview);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("pasirinkimai");
                pos = position;

                adb.setNeutralButton("Atsaukti", null);
                adb.setNegativeButton("Keisti", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                       editDialog();

                    }});
                adb.setPositiveButton("Trinti", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Trinti?");
                        builder.setMessage("Ar tikrai norite istrinti lauka?\n"+ elementasList.get(position).getissuer()+"\ništrynus šį failą jo atgauti nebeimanoma");
                        builder.setNegativeButton("Atsaukti", null);
                        builder.setPositiveButton("Trinti", new AlertDialog.OnClickListener(){
                        public void onClick (DialogInterface dialog,int which){
                            elementasList.remove(pos);
                            adapter.notifyDataSetChanged();
                            issaugotiSarasoduomenis();
                        }});
                        builder.show();
                    }});
                adb.show();
                return false;
            }
        });

        setupList();
    }


    public void editDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Iveskite norima pavadinima");
        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit, findViewById(R.id.edit_pavad), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.edit_pavad);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!input.getText().toString().equals("")) {
                    m_Text = input.getText().toString();
                    Log.wtf("naujas pavadinimas",m_Text);
                    if(!m_Text.equals("")){
                        elementasList.get(pos).setPavadinimas(m_Text);

                        m_Text="";
                        adapter.notifyDataSetChanged();
                        issaugotiSarasoduomenis();
                    }

                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        builder.show();
    }

    public void issaugotiSarasoduomenis(){//issaugo visa sarasa: naudojamas trinant elementa
        String kelias = getExternalFilesDir(null) + "/Mano katalogas";
        File file = new File(kelias, "Tekstas.txt");
        FileWriter fw;
        String failas="";

        try {
            fw = new FileWriter(file);

            for(int i=0;i<elementasList.size();i++) {

                fw.write(failas + elementasList.get(i).getTotp() + ";" + elementasList.get(i).getissuer() + ":");
            }
            fw.close();
            Toast.makeText(this, "Failas issaugotas", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Nepavyko issaugoti", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void atkurtiduomenis() {

        String duomenys = gautiduomenis();

        if (!duomenys.equals("")) {
            String[] parts = duomenys.split(":");
            for (String part : parts) {
                String[] parts2 = part.split(";");
                elementas add = new elementas(parts2[0], parts2[1], MainActivity.this);
                elementasList.add(add);
                System.out.println(part);
                setupList();
            }
        }

    }

    public void isaugotiDuomenis(String failas,String totp,String issuer) {
        String kelias = getExternalFilesDir(null) + "/Mano katalogas";
        File file = new File(kelias, "Tekstas.txt");
        FileWriter fw;


        try {
            fw = new FileWriter(file);
            fw.write( failas+totp + ";" + issuer + ":");
            fw.close();
        } catch (IOException e) {
            Toast.makeText(this, "Nepavyko issaugoti", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }



    public String gautiduomenis() {

        String kelias = getExternalFilesDir(null) + "/Mano katalogas";
        File f = new File(kelias);
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File(kelias, "Tekstas.txt");
        String content = "";

        if (file.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                content = new String(chars);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    private void setupList() {

        listView = (ListView) findViewById(R.id.listview);
        adapter = new listviewAdapteris(elementasList, 0, getApplicationContext());
        listView.setAdapter(adapter);

    }

    public void atnaujintiduomenis() {//naudojama elementas.totp()
        if (adapter != null) {
            adapter.notifyDataSetChanged();//funkcija naudojama atnaujinti sarasa realiuoju laiku
        }
    }

    public void nextActivity(View v) {
        Intent i = new Intent(this, QRscanner.class);
        AddActivityResultLauncher.launch(i);

    }




    public void openDialog() {
        dialogadd Dialogadd = new dialogadd();
        Dialogadd.show(getSupportFragmentManager(), "Prideti 2FA ranka");
    }
    @Override
    public void applyTexts(String totp1, String issuer) {
        if (totp1.matches("\\w{3}-\\w{3}-\\w{3}-\\w{3}-\\w{3}-\\w{3}")) {
            elementas badd = new elementas(totp1, issuer, MainActivity.this);
            elementasList.add(badd);
            isaugotiDuomenis(gautiduomenis(),totp1,issuer);
            gautiduomenis();
            setupList();

        } else {
            openDialog();
        }

    }
}