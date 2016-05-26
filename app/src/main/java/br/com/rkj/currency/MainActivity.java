package br.com.rkj.currency;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.rkj.currency.model.CurrencyAdapter;
import br.com.rkj.currency.model.CurrencyTableHelper;
import br.com.rkj.currency.object.Currency;
import br.com.rkj.currency.receiver.CurrencyReceiver;
import br.com.rkj.currency.services.CurrencyService;
import br.com.rkj.currency.utils.AlarmUtils;
import br.com.rkj.currency.utils.NotificationUtils;
import br.com.rkj.currency.utils.SharedPrefrencesUtils;

public class MainActivity extends AppCompatActivity implements CurrencyReceiver.Receiver {

    //Escolher um currency marretado por enquanto
    private String baseCurrency = Constante.CURRENCY_CODE[30];
    private String targetCurrency = "CAD"; //Constante.CURRENCY_NAMES[0];
    private CurrencyTableHelper tableHelper;

    public static final String TAG = MainActivity.class.getName();
    private int serviceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_MINUTE.ordinal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDownloads();
        iniciaModel();
        retrieveCurrencyExchangeRate();
       }


    /*
        Metodo que tem o resultado dos dados
     */
    @Override
    public void onReceiveResult(int resultCode, final Bundle dataResult){

      switch (resultCode){

          case Constante.STATUS_RUNNING:
              Log.d(TAG, "Servico currency esta rodando");
              break;

          case Constante.STATUS_FINISHED:
              runOnUiThread(new Runnable(){
                  @Override
                  public void run(){
                      Currency currencyParcel = dataResult.getParcelable(Constante.RESULT);
                      if(currencyParcel != null){
                          // logarei o resultado afim de ve-lo no log
                          String mensagem = "Currency" + currencyParcel.getBase() + " - " +
                                  currencyParcel.getName() + ": " + currencyParcel.getRate();
                          Log.d(TAG, mensagem);

                          long id = tableHelper.insertCurrency(currencyParcel);
                          Currency currency = currencyParcel;

                          try{
                              currency = tableHelper.getCurrency(id);
                          } catch (SQLException e){
                              e.printStackTrace();
                              Log.d(TAG, "Erro ao pegar currency");
                          }

                          if(currency != null){
                              String msgModel = "Currency(db)" + currencyParcel.getBase() + " - " +
                                      currencyParcel.getName() + ": " + currencyParcel.getRate();
                              Log.d(TAG, msgModel);
                              NotificationUtils.showNotificationMsg(getApplicationContext(),
                                      "Currency Exchange rate", msgModel);
                          }

                          if(NotificationUtils.isAppInBackGround(MainActivity.this)){
                              int numDownloads = SharedPrefrencesUtils.getNumDownloads(getApplicationContext());
                              SharedPrefrencesUtils.updateNumDownloads(getApplicationContext(), ++numDownloads);
                              if(numDownloads == Constante.MAX_DONWLOAD){
                                  Log.d(TAG, "Ja excedeu o maximo de donwloads em background ");
                                  serviceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_DAY.ordinal();
                                  retrieveCurrencyExchangeRate();
                              }
                          }
                      }
                  }
              });
              break;

          case Constante.STATUS_ERROR:
              String erro = dataResult.getString(Intent.EXTRA_TEXT);
              Log.d(TAG, erro);
              //mensagem na tela informando que houve um erro
              Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
      }

    }

    /*
        Metodo para inicial model
     */
    private void iniciaModel(){
        CurrencyAdapter adapter = new CurrencyAdapter(this);
        tableHelper = new CurrencyTableHelper(adapter);
    }

    private void retrieveCurrencyExchangeRate(){
        CurrencyReceiver receiver = new CurrencyReceiver(new Handler());
        receiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), CurrencyService.class);
        //dizendo ao android qual classe ele precisa loadar
        intent.setExtrasClassLoader(CurrencyService.class.getClassLoader());

        Bundle bundle = new Bundle();
        String url = Constante.URL_CURRENCY + baseCurrency;

        bundle.putString(Constante.URL, url);
        bundle.putParcelable(Constante.RECEIVER, receiver);
        bundle.putInt(Constante.REQUEST_ID, Constante.REQUEST_ID_NUM);
        bundle.putString(Constante.CURRENCY_NAME, targetCurrency);
        bundle.putString(Constante.CURRENCY_BASE, baseCurrency);

        intent.putExtra(Constante.BUNDLE, bundle);
//        startService(intent);
        AlarmUtils.startService(this, intent, AlarmUtils.REPEAT.values()[serviceRepetition]);
    }

    // on create, toda vez que invocarmos setaremos o numero de downloads para  0, para evitar que haja lixos
    // que foram contabilizados a cada operacao em background
    private void resetDownloads(){
        SharedPrefrencesUtils.updateNumDownloads(this, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
