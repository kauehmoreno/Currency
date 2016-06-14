package br.com.rkj.currency.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by kauerodrigues on 4/18/16.
 * Usando result receiver que uma interface generica do android
 * para receber o call back do resultado do objeto. Um mediador entre dois objetos
 * para que possam se comunicar
 */
public class CurrencyReceiver extends ResultReceiver {

    private  Receiver mReceiver;

    public CurrencyReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver mReceiver){
        this.mReceiver = mReceiver;

    }
    public interface Receiver{
        void onReceiveResult(int resultCode, Bundle resultData);

    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null){
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
