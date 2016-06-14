package br.com.rkj.currency.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;

import br.com.rkj.currency.Constante;
import br.com.rkj.currency.object.Currency;

/**
 * Created by kauerodrigues on 4/19/16.
 */
public class CurrencyTableHelper {

    public static final String TAG = CurrencyTableHelper.class.getName();

    private CurrencyDatabaseAdapter adapter;

    public CurrencyTableHelper(CurrencyDatabaseAdapter adapter) {
        this.adapter = adapter;
    }

    public long insertCurrency(Currency currency){
        ArrayList<Currency> currencies = getCurrencyHistorico(currency.getBase(), currency.getName(), currency.getDate());
        if (currencies.size() == 0){
            Log.d(TAG, "Não há gravacao no banco");

            ContentValues valoresInicias = new ContentValues();
            valoresInicias.put(Constante.TB_BASE, currency.getBase());
            valoresInicias.put(Constante.TB_DATE, currency.getDate());
            valoresInicias.put(Constante.TB_NAME, currency.getName());
            valoresInicias.put(Constante.TB_RATE, currency.getRate());

            long id = adapter.getWritableDatabase().insert(Constante.CURRENCY_TABLE, null, valoresInicias);
            adapter.getWritableDatabase().close();
            return id;
        }else{
            Log.d(TAG, "Há dados no BD");

        }

        return currencies.get(0).getId();
    }

    // usado pelo insert, copiarei o codigo para usar sem o parametro date. verificar como extrair depois
    // de uma maneira melhor
    public ArrayList<Currency> getCurrencyHistorico(String base, String name, String date){
        ArrayList<Currency> currencies = new ArrayList<>();
        Cursor cursor = adapter.getWritableDatabase().query(
                Constante.CURRENCY_TABLE,
                new String[]{Constante.TAB_ID, Constante.TB_BASE, Constante.TB_DATE,
                        Constante.TB_RATE, Constante.TB_NAME},
                Constante.TB_BASE + " = '" + base + "' AND " + Constante.TB_NAME + " = " +
                        "'" + name + "'     AND " + Constante.TB_DATE + " = '" + date + "'",
                null, null, null, null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                currencies.add(parseCurrency(cursor));
            }
            while (cursor.moveToNext()){
                currencies.add(parseCurrency(cursor));
            }
        }
        return currencies;
    }

    public ArrayList<Currency> getCurrencyHistorico(String base, String name){
        ArrayList<Currency> currencies = new ArrayList<>();
        Cursor cursor = adapter.getWritableDatabase().query(
                Constante.CURRENCY_TABLE,
                new String[]{Constante.TAB_ID, Constante.TB_BASE, Constante.TB_DATE,
                        Constante.TB_RATE, Constante.TB_NAME},
                Constante.TB_BASE + " = '" + base + "' AND " + Constante.TB_NAME + " = " +
                        "'" + name + "'",
                null, null, null, null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                currencies.add(parseCurrency(cursor));
            }
            while (cursor.moveToNext()){
                currencies.add(parseCurrency(cursor));
            }
        }
        return currencies;
    }

    /*
        Metodo para recuperar objeto (currency) dado o id do mesmo
     */
    public Currency getCurrency(long id) throws SQLException{
        Cursor cursor = adapter.getWritableDatabase().query(
                Constante.CURRENCY_TABLE,
                new String[]{Constante.TAB_ID, Constante.TB_BASE, Constante.TB_DATE,
                        Constante.TB_RATE, Constante.TB_NAME},
                Constante.TAB_ID + " = " + id, null, null, null, null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                return parseCurrency(cursor);
            }
        }

        return null;
    }

    public Currency parseCurrency(Cursor cursor){
        Currency currency = new Currency();

        currency.setId(cursor.getLong(cursor.getColumnIndex(Constante.TAB_ID)));
        currency.setBase(cursor.getString(cursor.getColumnIndex(Constante.TB_BASE)));
        currency.setName(cursor.getString(cursor.getColumnIndex(Constante.TB_NAME)));
        currency.setRate(cursor.getDouble(cursor.getColumnIndex(Constante.TB_RATE)));
        currency.setDate(cursor.getString(cursor.getColumnIndex(Constante.TB_DATE)));


        return currency;
    }

    public void deleteCurrencyTable(){
        adapter.getWritableDatabase().delete(Constante.CURRENCY_TABLE, null, null);
    }
}
