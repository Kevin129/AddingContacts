package pl.game.kontakty;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pl.game.kontakty.R;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> contactList;
    private ArrayAdapter<String> adapter;
    private EditText editTextImie, editTextNazwisko, editTextNumerTelefonu;
    private ListView listViewKontakty;
    private SharedPreferences sharedPreferences;
    private View lastSelectedView;
    private int lastSelectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja SharedPreferences
        sharedPreferences = getSharedPreferences("MyContacts", MODE_PRIVATE);

        // Wczytanie listy kontaktów z SharedPreferences
        contactList = new ArrayList<>(sharedPreferences.getStringSet("contacts", new HashSet<String>()));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);

        editTextImie = findViewById(R.id.editTextImie);
        editTextNazwisko = findViewById(R.id.editTextNazwisko);
        editTextNumerTelefonu = findViewById(R.id.editTextNumerTelefonu);

        listViewKontakty = findViewById(R.id.listViewKontakty);
        listViewKontakty.setAdapter(adapter);

        listViewKontakty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == lastSelectedPosition) {
                    // Odznaczanie kontaktu, jeśli został ponownie kliknięty
                    listViewKontakty.setItemChecked(position, false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                    lastSelectedView = null;
                    lastSelectedPosition = -1;
                } else {
                    // Przywrócenie koloru tła dla poprzednio zaznaczonego elementu
                    if (lastSelectedView != null) {
                        lastSelectedView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    // Zaznaczenie aktualnie wybranego elementu
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    lastSelectedView = view;
                    lastSelectedPosition = position;
                }
            }
        });

        Button buttonDodaj = findViewById(R.id.buttonDodaj);
        buttonDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajKontakt();
            }
        });

        Button buttonWyswietl = findViewById(R.id.buttonWyswietl);
        buttonWyswietl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wyswietlKontakty();
            }
        });

        Button buttonUsun = findViewById(R.id.buttonUsun);
        buttonUsun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usunZaznaczonyKontakt();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Zapisanie listy kontaktów do SharedPreferences podczas zamykania aplikacji
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> contactsSet = new HashSet<>(contactList);
        editor.putStringSet("contacts", contactsSet);
        editor.apply();
    }

    private void dodajKontakt() {
        String imie = editTextImie.getText().toString();
        String nazwisko = editTextNazwisko.getText().toString();
        String numerTelefonu = editTextNumerTelefonu.getText().toString();

        if (!imie.isEmpty() && !nazwisko.isEmpty() && !numerTelefonu.isEmpty()) {
            String kontakt = imie + " " + nazwisko + " - " + numerTelefonu;
            contactList.add(kontakt);
            adapter.notifyDataSetChanged();
            clearFields();
            showToast("The contact has been added");
        } else {
            showToast("Fill in all fields");
        }
    }

    private void wyswietlKontakty() {
        if (!contactList.isEmpty()) {
            showToast("Contact list:");
            for (String contact : contactList) {
                showToast(contact);
            }
        } else {
            showToast("No contacts");
        }
    }

    private void usunZaznaczonyKontakt() {
        if (lastSelectedPosition != -1) {
            contactList.remove(lastSelectedPosition);
            adapter.notifyDataSetChanged();
            clearFields();
            showToast("The selected contact has been deleted");
            lastSelectedView.setBackgroundColor(Color.TRANSPARENT);
            lastSelectedPosition = -1;
        } else {
            showToast("No contact selected for deletion");
        }
    }

    private void clearFields() {
        editTextImie.getText().clear();
        editTextNazwisko.getText().clear();
        editTextNumerTelefonu.getText().clear();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
