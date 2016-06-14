package br.com.rkj.currency;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.rkj.currency.model.CurrencyDatabaseAdapter;
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

    private CoordinatorLayout mLogLayout;
    private FloatingActionButton floatingActionButton;

    private boolean mIsLogVisible = true;
    private boolean fabVisible = true;

    private ListView mBaseCurrencyList;
    private ListView mTargetCurrencyList;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDownloads();
        initCurrencies();
        iniciaModel();
        initToolbar();
        initSpinner();
        initCurrencyList();
        initLineChart();
        addActionButtonListener();
//        showLogs();
        mLogLayout = (CoordinatorLayout) findViewById(R.id.log_layout);
       }

    @Override
    protected void onResume() {
        super.onResume();
        serviceRepetition = SharedPrefrencesUtils.getServiceRepetition(this);
        retrieveCurrencyExchangeRate();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    //        LogUtils.setLogListener(null);
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
                              String msgModel = "Currency(db)" + currency.getBase() + " - " +
                                      currency.getName() + ": " + currency.getRate();
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
                          } else{
                              updateLineChart();
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
        CurrencyDatabaseAdapter currencyDataBaseAdapter = new CurrencyDatabaseAdapter(this);
        tableHelper = new CurrencyTableHelper(currencyDataBaseAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void initSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.time_frequency);
        spinner.setSaveEnabled(true);
        spinner.setSelection(SharedPrefrencesUtils.getServiceRepetition(this), false);
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharedPrefrencesUtils.updateServiceRepetition(MainActivity.this, position);
                        serviceRepetition = position;
                        if (position >= AlarmUtils.REPEAT.values().length) {
                            AlarmUtils.stopService();
                        } else {
                            retrieveCurrencyExchangeRate();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void initCurrencyList() {
        mBaseCurrencyList = (ListView) findViewById(R.id.base_currency_list);
        mTargetCurrencyList = (ListView) findViewById(R.id.target_currency_list);

        br.com.rkj.currency.adapters.CurrencyAdapter baseCurrencyAdapter = new br.com.rkj.currency.adapters.CurrencyAdapter(this);
        br.com.rkj.currency.adapters.CurrencyAdapter targetCurrencyAdapter = new br.com.rkj.currency.adapters.CurrencyAdapter(this);

        mBaseCurrencyList.setAdapter(baseCurrencyAdapter);
        mTargetCurrencyList.setAdapter(targetCurrencyAdapter);

        int baseCurrencyIndex = retrieveIndexOf(baseCurrency);
        int targetCurrencyIndex = retrieveIndexOf(targetCurrency);

        mBaseCurrencyList.setItemChecked(baseCurrencyIndex, true);
        mTargetCurrencyList.setItemChecked(targetCurrencyIndex, true);

        mBaseCurrencyList.setSelection(baseCurrencyIndex);
        mTargetCurrencyList.setSelection(targetCurrencyIndex);

        addCurrencySelectionListener();
    }

    private int retrieveIndexOf(String currency) {
        return Arrays.asList(Constante.CURRENCY_CODE).indexOf(currency);
    }

    private void addCurrencySelectionListener() {
        mBaseCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                baseCurrency = Constante.CURRENCY_CODE[position];
               Log.d(TAG, "Base Currency has changed to: " + baseCurrency);
                SharedPrefrencesUtils.updateCurrency(MainActivity.this, baseCurrency, true);
                retrieveCurrencyExchangeRate();
            }
        });

        mTargetCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                targetCurrency = Constante.CURRENCY_CODE[position];
                Log.d(TAG, "Target Currency has changed to: " + targetCurrency);
                SharedPrefrencesUtils.updateCurrency(MainActivity.this, targetCurrency, false);
                retrieveCurrencyExchangeRate();
            }
        });
    }

    /*
        Chart Metodos
     */
    private void initLineChart() {
        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineChart.setNoDataText("No Data");
        lineChart.setHighlightEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);

        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.BLUE);
        lineChart.setData(lineData);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(ColorTemplate.getHoloBlue());

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setAxisMaxValue(120f);
        yAxis.setDrawGridLines(true);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
    }

    private void updateLineChart() {
        lineChart.setDescription("Currency Exchange Rate: " + baseCurrency + " - " +targetCurrency);
        ArrayList<Currency> currencies = tableHelper.getCurrencyHistorico(baseCurrency, targetCurrency);
        LineData lineData = lineChart.getData();
        lineData.clearValues();
        for(Currency currency : currencies) {
            addChartEntry(currency.getDate(), currency.getRate());
        }
    }

    private void addChartEntry(String date, double value) {
        LineData lineData = lineChart.getData();
        if(lineData != null) {
            LineDataSet lineDataSet = lineData.getDataSetByIndex(0);
            if(lineDataSet == null) {
                lineDataSet = createSet();
                lineData.addDataSet(lineDataSet);
            }

            if(!lineChart.getData().getXVals().contains(date)) {
                lineData.addXValue(date);
            }
            lineData.addEntry(new Entry((float) value, lineDataSet.getEntryCount()), 0);
            lineChart.notifyDataSetChanged();
        }
    }

    private LineDataSet createSet() {
        LineDataSet lineDataSet = new LineDataSet(null, "Value");
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(ColorTemplate.getHoloBlue());
        lineDataSet.setCircleColor(ColorTemplate.getHoloBlue());
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleSize(4f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
        lineDataSet.setHighLightColor(Color.CYAN);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);
        return lineDataSet;


    }


    private void initCurrencies() {
        baseCurrency = SharedPrefrencesUtils.getCurrency(this, true);
        targetCurrency = SharedPrefrencesUtils.getCurrency(this, false);
    }

//    private void showLogs() {
//        final TextView logText = (TextView) findViewById(R.id.log_text);
//        LogUtils.setLogListener(new LogUtils.LogListener() {
//            @Override
//            public void onLogged(final StringBuffer log) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        logText.setText(log.toString());
//                        logText.invalidate();
//
//                    }
//                });
//            }
//        });
//    }

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

    private void addActionButtonListener(){
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PopupMenu popuMenu = new PopupMenu(MainActivity.this, floatingActionButton);
                popuMenu.getMenuInflater().inflate(R.menu.popup_menu, popuMenu.getMenu());
                popuMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.clear_database:
                                tableHelper.deleteCurrencyTable();
                                Log.d(TAG, "Currency database foi apagado");
                                lineChart.clearValues();
                                updateLineChart();
                                break;
                            case R.id.graph:
                                findViewById(R.id.currency_list_layout).setVisibility(View.GONE);
                                lineChart.setVisibility(View.VISIBLE);
                                updateLineChart();
                                break;
                            case R.id.selection:
                                findViewById(R.id.currency_list_layout).setVisibility(View.VISIBLE);
                                lineChart.setVisibility(View.GONE);
                                break;
                        }
                        return true;
                    }
                });
                popuMenu.show();
            }
        });
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
        switch(item.getItemId()) {
            case R.id.action_clear_logs:
//                LogUtils.clearLogs();
                return true;

            case R.id.action_show_logs:
                mIsLogVisible = !mIsLogVisible;
                item.setIcon(mIsLogVisible ? R.drawable.ic_keyboard_hide : R.drawable.ic_keyboard);
                mLogLayout.setVisibility(mIsLogVisible ? View.VISIBLE : View.GONE);
                break;

            case R.id.action_show_fab:
                fabVisible= !fabVisible;
                item.setIcon(fabVisible ? R.drawable.ic_remove: R.drawable.ic_add);
                floatingActionButton.setVisibility(fabVisible ? View.VISIBLE : View.GONE);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
