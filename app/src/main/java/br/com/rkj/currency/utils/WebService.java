package br.com.rkj.currency.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import br.com.rkj.currency.Constante;

/**
 * Created by kauerodrigues on 4/18/16.
 */

public class WebService {

    public static final String TAG = WebService.class.getName();

    public static JSONObject requestJSONObject (String urlServico){
        HttpURLConnection urlConnection = null;

        try{
            URL urlToRequest = new URL(urlServico);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(Constante.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(Constante.READ_TIMEOUT);

            int status  = urlConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_UNAUTHORIZED){
                Log.d(TAG, "acesso não autorizado!! ");
            }else if (status == HttpURLConnection.HTTP_NOT_FOUND){
                Log.d(TAG, "404 api not found");
            }else if (status != HttpURLConnection.HTTP_OK){
                Log.d(TAG, "erro ao tentar acessar a API");
            }

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(convertInputStreamToString(inputStream));

        } catch (MalformedURLException e){
            Log.d(TAG, e.getMessage());
        } catch (SocketTimeoutException e){
            Log.d(TAG, e.getMessage());
        } catch (IOException e){
            Log.d(TAG, e.getMessage());
        } catch (JSONException e){
            Log.d(TAG, e.getMessage());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }

        // se cair em algum erro ele ira desconectar e retornara nulo
        return null;
    }

    private static String convertInputStreamToString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));
        String response;

        try{
            while((response = reader.readLine()) != null){
                stringBuilder.append(response);
            }

        } catch(IOException e){
            Log.d( "erro ao converter JSON:",e.getMessage());
        }

        return stringBuilder.toString();
    }

    public static boolean hasConnection (Context context){

        ConnectivityManager managerConexao = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return managerConexao != null && managerConexao.getActiveNetworkInfo() !=null &&
                managerConexao.getActiveNetworkInfo().isConnected();

    }
}
