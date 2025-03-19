package com.example.ListaDeCompras;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FazerCompras extends AppCompatActivity {

    private ImageButton btnvoltar;
    private LinearLayout productList;
    private TextView tvTotal;
    private double total = 0.0;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fazer_compras);

        productList = findViewById(R.id.productList);
        tvTotal = findViewById(R.id.tvTotal);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        Button btnFinalize = findViewById(R.id.btnFinalize);

        dbHelper = new DatabaseHelper(this);

        btnvoltar = (ImageButton) findViewById(R.id.btnvoltar);
        btnvoltar.setOnClickListener(view -> showExitConfirmationDialog());

        // Adicionar produto
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        // Finalizar compra
        btnFinalize.setOnClickListener(v -> showFinalizationConfirmationDialog());
    }

    // Exibe o dialog de adicionar produto
    private void showAddProductDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_dialog_add_product);

        TextView etProductName = dialog.findViewById(R.id.etProductName);
        TextView etQuantity = dialog.findViewById(R.id.etQuantity);
        TextView etPrice = dialog.findViewById(R.id.etPrice);
        Button btnAddToCart = dialog.findViewById(R.id.btnAddToCart);

        btnAddToCart.setOnClickListener(v -> {
            String productName = etProductName.getText().toString();
            int quantity = Integer.parseInt(etQuantity.getText().toString());
            double price = Double.parseDouble(etPrice.getText().toString());

            // Adiciona o produto ao layout
            addProductToList(productName, quantity, price);

            // Atualiza o total
            total += price * quantity;
            tvTotal.setText("Total: R$ " + String.format(Locale.getDefault(), "%.2f", total));

            dialog.dismiss(); // Fecha o dialog
        });

        dialog.show();
    }

    // Adiciona produto à lista de compras
    private void addProductToList(String productName, int quantity, double price) {
        LinearLayout productItemLayout = new LinearLayout(this);
        productItemLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Exibe informações do produto
        TextView productView = new TextView(this);
        productView.setText(productName + " - Quantidade: " + quantity + " - R$" + price);
        productItemLayout.addView(productView);

        // Adiciona o ícone de editar
        ImageButton editButton = new ImageButton(this);
        editButton.setImageResource(R.drawable.ic_edit); // ícone de edição
        editButton.setOnClickListener(v -> showEditProductDialog(productName, quantity, price, productItemLayout));

        // Adiciona o ícone de excluir
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.ic_delete); // ícone de exclusão
        deleteButton.setOnClickListener(v -> {
            productList.removeView(productItemLayout); // Remove o item da lista
            // Atualiza o total ao excluir o produto
            total -= total;
            tvTotal.setText("Total: R$ " + String.format(Locale.getDefault(), "%.2f", total));
        });

        productItemLayout.addView(editButton);
        productItemLayout.addView(deleteButton);

        // Adiciona o item à lista de produtos
        productList.addView(productItemLayout);
    }

    // Exibe o dialog de edição de produto
    private void showEditProductDialog(String originalName, int originalQuantity, double originalPrice, LinearLayout productLayout) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_dialog_add_product);

        TextView etProductName = dialog.findViewById(R.id.etProductName);
        TextView etQuantity = dialog.findViewById(R.id.etQuantity);
        TextView etPrice = dialog.findViewById(R.id.etPrice);

        etProductName.setText(originalName);
        etQuantity.setText(String.valueOf(originalQuantity));
        etPrice.setText(String.valueOf(originalPrice));

        Button btnSaveChanges = dialog.findViewById(R.id.btnAddToCart);
        btnSaveChanges.setText("Salvar Alterações");

        btnSaveChanges.setOnClickListener(v -> {
            String newName = etProductName.getText().toString();
            int newQuantity = Integer.parseInt(etQuantity.getText().toString());
            double newPrice = Double.parseDouble(etPrice.getText().toString());

            // Atualiza o total antes de salvar a alteração
            total -= originalPrice * originalQuantity; // Subtrai o valor do produto original
            total += newPrice * newQuantity; // Adiciona o novo valor

            // Atualiza a lista com as novas informações
            updateProductInList(newName, newQuantity, newPrice, productLayout);

            // Atualiza o total na tela
            tvTotal.setText("Total: R$ " + String.format(Locale.getDefault(), "%.2f", total));

            dialog.dismiss(); // Fecha o dialog
        });

        dialog.show();
    }

    // Atualiza o produto na lista
    private void updateProductInList(String newName, int newQuantity, double newPrice, LinearLayout productLayout) {
        TextView productTextView = (TextView) productLayout.getChildAt(0);
        productTextView.setText(newName + " - Quantidade: " + newQuantity + " - R$" + newPrice);
    }

    // Finaliza a compra e salva no banco de dados
    private void finalizePurchase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Total", total);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        values.put("Data", date);

        db.insert("tabela_historico", null, values);
        db.close();

        productList.removeAllViews();
        total = 0.0;
        tvTotal.setText("Total: R$ 0.00");

        Toast.makeText(this, "Compra finalizada e salva no histórico.", Toast.LENGTH_SHORT).show();
    }

    // Exibe um alerta de confirmação para sair sem finalizar
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar saída")
                .setMessage("Você deseja realmente sair? Sua sacola será limpa.")
                .setCancelable(false)
                .setPositiveButton("Sim", (dialog, which) -> finish()) // Sair da tela
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss()) // Ficar na tela
                .show();
    }

    // Exibe um alerta de confirmação para finalizar a compra
    private void showFinalizationConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar finalização")
                .setMessage("Você deseja finalizar a compra? A compra será salva no histórico.")
                .setCancelable(false)
                .setPositiveButton("Sim", (dialog, which) -> {
                    finalizePurchase(); // Finaliza a compra
                })
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss()) // Cancelar
                .show();
    }
}