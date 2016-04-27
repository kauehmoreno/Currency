package br.com.rkj.currency;

/**
 * Created by kauerodrigues on 4/18/16.
 */
public class Constante {

    //url para pegar dados das mudancas de  currency
    public static final String URL_CURRENCY = "http://api.fixer.io/latest?base=";

    //constante para parsear o valor vindo do json
    public static final String BASE = "base";
    public static final String DATE = "date";
    public static final String RATES = "rates";

    //TODO constante usadas para Servico de currency e para o reciever do mesmo
    // solucao peleativa, informacoes marretadas, integracoes com usuarios futuramente
    public static final String URL = "url";
    public static final String RECEIVER = "receiver";
    public static final String RESULT = "result";
    public static final String CURRENCY_BASE = "currencyBase";
    public static final String CURRENCY_NAME = "currencyName";
    public static final String REQUEST_ID = "requestId";
    public static final String BUNDLE = "bundle";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;


    //Constante usadas para o DATABASE
    public static final String DATABASE_NAME = "CurrencyDB";

    public static final String CURRENCY_TABLE = "currencies";
    public static final String TAB_ID = "_id";
    public static final String TB_BASE = "base";
    public static final String TB_DATE = "date";
    public static final String TB_RATE = "rate";
    public static final String TB_NAME = "name";

    //Numero maximo de requiscoes para pegar os dados em background
    public static final int MAX_DONWLOAD = 5;

    //NUMERO DE TODAS AS MOEDAS
    public static final int CURRENCY_SIZE = 32;

    public static final String [] CURRENCY_CODE = {
            "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBP", "HKD", "HRK",
            "HUF", "IDR", "ILS", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN",
            "RON", "RUB", "SEK", "SGD", "THB", "TRY","USD", "ZAR", "EUR"
    };

    public static final String [] CURRENCY_NAMES = {
            "Australian Dollar", "Bulgarian Lev", "Brazilian Real", "Canadian Dollar", "Swiss Franc",
            "Yuan Renminbi", "Czech Koruna","Danish Krone", "Pound Sterling", "Hong Kong Dollar",
            "Croatian Kuna", "Hungarian Forint","Idonasian Rupiah", "Israeli New Shekel", "Indian Rupee",
            "Japanese Yen", "Korean Won", "Mexican Nuevo Peso", "Malaysian Ringgit", "Norwegian Krone",
            "New Zealand Dollar", "Phillipine Peso", "Polish Zloty", "Romanian New Leu", "Belarussian Ruble",
            "Swidish Krona", "Singapore Dollar", "Thai Baht", "Turkish Lira", "US Dollar", "South Africa Rand",
            "Euro"
    };


    //id de notificacao
    public static final int ID_NOTIFICACAO = 100;
    public static final int REQUEST_ID_NUM = 101;

    //Preferencias
    public static final String CURRENCY_PREFERENCES = "CURRENCY_PREFERENCES";
    public static final String BASE_CURRENCY = "BASE_CURRENCY";
    public static final String TARGET_CURRENCY = "TARGET_CURRENCY";
    public static final String SERVICE_REPETITON = "SERVICE_REPETITION";
    public static final String NUM_DONWOLADS = "NUM_DONWLOADS";

    //web connection constante
    public static final int  CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;
}
