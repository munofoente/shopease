package com.example.ListaDeCompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //constantes usuario
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "id";
    private static final String COL_USER_HISTORICO = "user_historico";
    private static final String COL_NOME = "nome";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    //constantes historico de compras
    public static final String TABELA_HISTORICO = "tabela_historico";
    public static final String COLUNA_HISTORICO_ID = "ID";
    public static final String COLUNA_TOTAL = "Total";
    public static final String COLUNA_DATA = "Data";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usuario =
                "CREATE TABLE "
                        + TABLE_NAME + "("
                        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_USER_HISTORICO + " INTEGER, "
                        + COL_NOME + " TEXT,"
                        + COL_EMAIL + " TEXT, "
                        + COL_PASSWORD + " TEXT, " + "FOREIGN KEY(" + COL_USER_HISTORICO + ") REFERENCES " + TABELA_HISTORICO + "(" + COLUNA_HISTORICO_ID + "));";
        db.execSQL(usuario);

        String historico = "CREATE TABLE "
                + TABELA_HISTORICO + "("
                + COLUNA_HISTORICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUNA_TOTAL + " REAL, "
                + COLUNA_DATA + " TEXT);";
        db.execSQL(historico);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);
        onCreate(db);
    }

    public boolean registerUser(String nome, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOME, nome);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }


    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

}

