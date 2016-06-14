package br.com.rkj.currency.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import br.com.rkj.currency.Constante;
import br.com.rkj.currency.helpers.CurrencyParser;
import br.com.rkj.currency.object.Currency;
import br.com.rkj.currency.utils.WebService;

/**
 * Created by kauerodrigues on 4/18/16.
 * IntenteService é nativo do android, serve para lidar com request assincrono
 *
 */
public class CurrencyService extends IntentService {
    public static final String TAG = CurrencyService.class.getName();

    public CurrencyService(String name) {
        super(TAG);
    }

    public CurrencyService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Currency Service has started");
        Bundle intentBundle = intent.getBundleExtra(Constante.BUNDLE);
        final ResultReceiver receiver = intentBundle.getParcelable(Constante.RECEIVER);
        Parcel parcel = Parcel.obtain();
        receiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
//        ResultReceiver receiverForSending =
//                android.os.ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        String url = intentBundle.getString(Constante.URL);
        String currencyName = intentBundle.getString(Constante.CURRENCY_NAME);

        Bundle bundle = new Bundle();
        if(url != null && !TextUtils.isEmpty(url)) {
            receiverForSending.send(Constante.STATUS_RUNNING, Bundle.EMPTY);
            if(WebService.hasConnection(getApplicationContext())) {
                try {
                    JSONObject obj = WebService.requestJSONObject(url);
                    if(obj != null) {
                        Currency currency = CurrencyParser.parseCurrency(obj, currencyName);
                        bundle.putParcelable(Constante.RESULT, currency);
                        receiverForSending.send(Constante.STATUS_FINISHED, bundle);
                    }
                } catch(Exception  e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiverForSending.send(Constante.STATUS_ERROR, bundle);
                }
            } else {
                Log.d(TAG, "No internet connection");
            }
        }
        Log.d(TAG, "Currency Service has stopped");
        stopSelf();
    }
}
