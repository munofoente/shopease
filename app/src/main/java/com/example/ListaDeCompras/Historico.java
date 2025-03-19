package com.example.ListaDeCompras;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Historico extends AppCompatActivity {

    private Button btnCompare;
    private DatabaseHelper dbHelper;
    private ListView lvHistory;
    private TextView tvComparisonResult;
    private ArrayList<String> purchases;
    private ArrayList<Double> totals;
    private ArrayList<String> dates;
    private int selectedItem1 = -1, selectedItem2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvHistory = findViewById(R.id.lvHistory);
        tvComparisonResult = findViewById(R.id.tvComparisonResult);
        btnCompare = findViewById(R.id.btnCompare);

        dbHelper = new DatabaseHelper(this);
        purchases = new ArrayList<>();
        totals = new ArrayList<>();
        dates = new ArrayList<>();

        // Carrega o histórico do banco de dados
        loadHistory();

        // Configura o ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, purchases);
        lvHistory.setAdapter(adapter);
        lvHistory.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Evento para comparar as compras selecionadas
        btnCompare.setOnClickListener(v -> comparePurchases());


    }

    private void loadHistory() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta os dados do histórico
        Cursor cursor = db.rawQuery("SELECT * FROM tabela_historico", null);

        if (cursor.moveToFirst()) {
            do {
                // Adiciona os dados às listas
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("Total"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Data"));

                totals.add(total);
                dates.add(date);
                purchases.add("Data: " + date + " | Total: R$ " + total);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    private void comparePurchases() {
        // Verifica quais itens estão selecionados
        selectedItem1 = -1;
        selectedItem2 = -1;

        int selectedCount = 0;

        for (int i = 0; i < lvHistory.getCount(); i++) {
            if (lvHistory.isItemChecked(i)) {
                if (selectedCount == 0) {
                    selectedItem1 = i;
                } else if (selectedCount == 1) {
                    selectedItem2 = i;
                }
                selectedCount++;
            }
        }

        // Verifica se dois itens foram selecionados
        if (selectedItem1 != -1 && selectedItem2 != -1) {
            double total1 = totals.get(selectedItem1);
            double total2 = totals.get(selectedItem2);

            // Calcula a diferença percentual
            double percentageChange = ((total2 - total1) / total1) * 100;
            double valueDifference = total2 - total1;

            if (valueDifference < 0){
                valueDifference = valueDifference * -1;
            }

            // Exibe o resultado
            String resultText = String.format("Diferença: %.2f%%", percentageChange);

            DecimalFormat df=new DecimalFormat("0.00");
            String valueFormated = df.format(valueDifference);

            if (percentageChange < 0) {
                resultText += "\nVocê economizou! (R$ " + valueFormated + ")";
                tvComparisonResult.setTextColor(getResources().getColor(R.color.green));
            } else {
                resultText += "\nVocê gastou mais! (R$ " + valueFormated + ")";
                tvComparisonResult.setTextColor(getResources().getColor(R.color.red));
            }

            tvComparisonResult.setText(resultText);

        } else {
            // Exibe uma mensagem se não houver seleção suficiente
            showAlert("Seleção insuficiente", "Por favor, selecione duas compras para comparar.");
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


}