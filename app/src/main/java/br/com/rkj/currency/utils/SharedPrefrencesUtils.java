package br.com.rkj.currency.utils;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.rkj.currency.Constante;

/**
 * Created by kauerodrigues on 5/26/16.
 *
 * "Interface for accessing and modifying preference data returned by getSharedPreferences(String, int).
 * For any particular set of preferences, there is a single instance of this class that all clients share"
 */
public class SharedPrefrencesUtils {

    public static String getCurrency(Context context, boolean isBaseCurrency){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );

        return sharedPreferences.getString(
                isBaseCurrency ? Constante.BASE_CURRENCY : Constante.TARGET_CURRENCY,
                isBaseCurrency ? "BRL" : "USD"
        );


    }

    public static void updateCurrency(Context context, String currency, boolean isBaseCurrency){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(isBaseCurrency ? Constante.BASE_CURRENCY : Constante.TARGET_CURRENCY, currency);
        editor.commit();
    }

    // caso seja a primeira vez que o servico é chamado, retornremos 0 para que na proxima vez ja possa retornar
    // o serviceRepetition
    public static int getServiceRepetition(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );
        return sharedPreferences.getInt(Constante.SERVICE_REPETITON, 0);
    }

    public static void updateServiceRepetition(Context context, int serviceRepetition){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constante.SERVICE_REPETITON, serviceRepetition);
        editor.commit();
    }


    //numeros de donwloads
    // tenta previnir(controlar) o numero de vezes de running in backgrounds para salvar bateria

    public static int getNumDownloads(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );
        return sharedPreferences.getInt(Constante.NUM_DONWOLADS, 0);
    }

    public static void updateNumDownloads(Context context, int numDownloads){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constante.CURRENCY_PREFERENCES, Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constante.NUM_DONWOLADS, numDownloads);
        editor.commit();
    }
}
