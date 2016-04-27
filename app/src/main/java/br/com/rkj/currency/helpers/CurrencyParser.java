package br.com.rkj.currency.helpers;

import org.json.JSONObject;

import br.com.rkj.currency.Constante;
import br.com.rkj.currency.object.Currency;

/**
 * Created by kauerodrigues on 4/18/16.
 * Converte o json para se tornar um currency class
 */
public class CurrencyParser {

    public  static Currency  parseCurrency(JSONObject obj, String currencyName){

        Currency currency = new Currency();

        currency.setBase(obj.optString(Constante.BASE));
        currency.setDate(obj.optString(Constante.DATE));
        JSONObject rateObject = obj.optJSONObject(Constante.RATES);

        if(rateObject != null){
            currency.setRate(rateObject.optDouble(currencyName));
        }
        currency.setName(currencyName);

        return currency;
    }

}
