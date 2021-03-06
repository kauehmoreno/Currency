package br.com.rkj.currency.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.rkj.currency.Constante;

/**
 * Created by kauerodrigues on 4/19/16.
 */
public class CurrencyDatabaseAdapter extends SQLiteOpenHelper {

    public static final String TAG = CurrencyDatabaseAdapter.class.getName();

    public static final int DATABASE_VERSION = 1;

    public static final String CURRENCY_TABLE_CREATE = "create table " + Constante.CURRENCY_TABLE + " (" +
                                Constante.TAB_ID + " integer primary key autoincrement, "+
                                Constante.TB_BASE + " text not null, " +
                                Constante.TB_NAME + " text not null, " +
                                Constante.TB_RATE + " real, " +
                                Constante.TB_DATE + " date);";

    public CurrencyDatabaseAdapter(Context context) {
        super(context, Constante.DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db = getWritableDatabase();
        try{
            db.execSQL(CURRENCY_TABLE_CREATE);
            Log.d(TAG, "Tabela criada");
        } catch(SQLException e){
            Log.d(TAG, "erro ao criar a tabela -->" + e);
        }
    }

    // metodo responsavel por atualizsar a tabela ja criada em caso de modificacao da mesma numa futura versao
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCurrentTable(db);
        onCreate(db);
    }

    private void clearCurrentTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS" + Constante.CURRENCY_TABLE);
    }
}
