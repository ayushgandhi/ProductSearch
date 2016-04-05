package pict.ama.com.beproone;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 4/2/16.
 */
public class MightySeaAPI
{
        private static final String URL_HOST = "https://mighty-sea-77814.herokuapp.com/search/";
        private static final String SEND_STRING_URL=URL_HOST+"get_text/";
        private static final String API_VERSION = "/v1";
        private static final String BASE_URL = URL_HOST + API_VERSION;
        private static final String REGISTER_URL = BASE_URL + "/user/register";
        private static final String RESETPASSWORD_URL = BASE_URL + "/user/password";
        private static final String LOGIN_URL = BASE_URL + "/user/login";
        private static final String LOGOUT_URL = BASE_URL + "/user/logout";
        private static class APICallTask extends AsyncTask<CallInformation, Void, CallResult>
        {
            private Context context;
            private IAPICall caller;
            private Map<String, String> headers;
            ProgressDialog pd = null;
            public APICallTask(Context context, IAPICall caller,Map<String, String> headers)
            {
                this.context = context;
                this.caller = caller;
                this.headers = headers;
            }
            @Override
            protected CallResult doInBackground(CallInformation... params)
            {
                try
                {
                    CallInformation ci = params[0];
                    StringBuffer output = new StringBuffer();
                    HttpURLConnection urlConnection = null;
                    URL url = new URL(ci.url);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    fillHeaders(urlConnection);
                    if(ci.method == CallInformation.GET)
                    {
                        try
                        {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            output = readStream(in);
                        }
                        catch(Exception e)
                        {
                            return new CallResult(e.getMessage());
                        }
                        finally
                        {
                            urlConnection.disconnect();
                        }
                    }
                    else if(ci.method == CallInformation.POST)
                    {
                        try
                        {
                            urlConnection.setDoOutput(true);
                            urlConnection.setChunkedStreamingMode(0);
                            writeStream(urlConnection.getOutputStream(), ci.payload);
                            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
                            if (responseCode >= 400 && responseCode <= 599)
                            {
                                InputStream err = new BufferedInputStream(urlConnection.getErrorStream());
                                output = readStream(err);
                                String msg = null;
                                try
                                {
                                    JSONObject object = new JSONObject(output.toString());
                                    msg = (String)object.get("message");
                                }
                                catch(Exception e)
                                {
                                    msg = context.getResources().getString(R.string.unknownservererror);
                                }
                                return new CallResult(responseCode, msg);
                            }
                            else
                            {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                output = readStream(in);
                            }
                        }
                        catch(Exception e)
                        {
                            return new CallResult(e.getMessage());
                        }
                        finally
                        {
                            urlConnection.disconnect();
                        }
                    }
                    return new CallResult(urlConnection.getResponseCode(), output.toString());
                }
                catch(Exception e)
                {
                    return new CallResult(e.getMessage());
                }
            }
            private void fillHeaders(HttpURLConnection urlConnection)
            {
                for (String header : headers.keySet())
                {
                    urlConnection.setRequestProperty(header, headers.get(header));
                }
            }
            private void writeStream(OutputStream out, String payload) throws IOException
            {
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeBytes(payload);
                dos.flush();
                dos.close();
            }
            private StringBuffer readStream(InputStream in) throws IOException
            {
                StringBuffer buf = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = br.readLine();
                while(line != null)
                {
                    buf.append(line);
                    line = br.readLine();
                }
                return buf;
            }
            @Override
            protected void onPostExecute(CallResult result)
            {
                pd.dismiss();
                if(result.isSuccess())
                {
                    int response = result.getResponse();
                    // Handle 40x and 50x errors
                    if(response >= 400 && response <= 599)
                    {
                        Toast.makeText(context,result.getFailureMessage(), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        caller.response(result);
                    }
                }
                else
                {
                    Toast.makeText(context,result.getFailureMessage(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            protected void onPreExecute()
            {
                pd = new ProgressDialog(context);
                pd.setMessage(context.getResources().getString(R.string.progressmsg));
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }
        }
        static class CallResult
        {
            public static final int CALLRESULT_SUCCESS = 0;
            public static final int CALLRESULT_FAILURE = 1;
            private int result = CALLRESULT_FAILURE;
            private int response;
            private String responseText;
            public CallResult(int response, String responseText)
            {
                this.result = CALLRESULT_SUCCESS;
                this.response = response;
                this.responseText = responseText;
            }
            public CallResult(String failureMessage)
            {
                this.result = CALLRESULT_FAILURE;
                this.responseText = failureMessage;
            }
            public int getResponse()
            {
                return response;
            }
            public String getResponseText()
            {
                return responseText;
            }
            public String getFailureMessage()
            {
                return responseText;
            }
            public boolean isSuccess()
            {
                return (result == CALLRESULT_SUCCESS);
            }
        }
        static class CallInformation
        {
            public static final int GET = 0;
            public static final int POST = 1;
            public static final int PUT = 2;
            public static final int DELETE = 3;
            private String url = null;
            private int method = POST;
            private String payload;
            public CallInformation(int method, String URL)
            {
                this.method = method;
                this.url = URL;
                this.payload = null;
            }
            public String getPayload()
            {
                return payload;
            }
            public CallInformation setPayload(String payload)
            {
                this.payload = payload;
                return this;
            }
            public String getUrl()
            {
                return url;
            }
            public int getMethod()
            {
                return method;
            }
            public static CallInformation GET(String url)
            {
                return new CallInformation(CallInformation.GET, url);
            }
            public static CallInformation POST(String url, String payload)
            {
                return (new CallInformation(CallInformation.POST, url).setPayload(payload));
            }
            public static CallInformation PUT(String url, String payload)
            {
                return (new CallInformation(CallInformation.PUT, url).setPayload(payload));
            }
            public static CallInformation DELETE(String url, String payload)
            {
                return (new CallInformation(CallInformation.DELETE, url).setPayload(payload));
            }
        }
        public interface IAPICallback
        {
            public void success(String response);
            public void failure(String message);
        }
        static class Auth
        {
            private static final String HAK = "dfd8f390c6cbd8fd95c34f79aa4a9480eff582befdb1d86f0027d2703de42996d230fed3ae4c2bb59a7995d9a99c390acbee86d1fd86f20dfb72f81e0a4e5a68";
            private static final String HAS = "984e6a3c9253b494d96ee364efa203d306e88b180d9c630711d4f07ce787d7ed79df0a28adc0ed5de9c8e353b4110b4f181796b563ea9776d05730399a528e3a";
            private static String ps = null;
            private static String authKey = null;
            private static String loginToken = null;
            private static Long serverTime = null;
            private static Long loginTime = null;
            private static Long timeOffset = null;
            private static void addStaticHeaders(Map<String, String> hMap)
            {
                hMap.put("User-Agent" ,"Mozilla/5.0 ( compatible )");
                hMap.put("Accept-Encoding", "identity");
                hMap.put("Accept", "*/*");
                hMap.put("Content-Type", "application/text");
            }
            public static Map<String, String> getHeaders(String ... strings)
            {
                HashMap<String, String> hMap = new HashMap<String, String>();
                addStaticHeaders(hMap);
                hMap.put("AKEY", HAK);
                hMap.put("APWD", strings[0]);
                return hMap;
            }
            public static Map<String, String> getAuthHeaders()
            {
                if(loginTime == null)
                {
                    throw new RuntimeException("getAuthHeaders called when user is not logged in !");
                }
                HashMap<String, String> hMap = new HashMap<String, String>();
                addStaticHeaders(hMap);
                hMap.put("AKEY", HAK);
                hMap.put("Authorization", Auth.authKey);
                hMap.put("LoginToken", Auth.loginToken);
                long timestamp = System.currentTimeMillis() + Auth.timeOffset;
                String strTS = "" + timestamp;
                hMap.put("TimeStamp", strTS);
                hMap.put("MPWD", getMPWD(CrypUtil.encryptSHA512(Auth.ps), Auth.authKey, strTS, HAS));
                return hMap;
            }
            public static void setAuthenticationDetails(String un, String ps, String aKey, String lToken, long sTime)
            {
                Auth.ps = ps;
                Auth.authKey = aKey;
                Auth.loginToken = lToken;
                Auth.serverTime = sTime;
                Auth.loginTime = System.currentTimeMillis();
                Auth.timeOffset = loginTime - serverTime;
            }
            public static void resetAuthenticationDetails()
            {
                Auth.ps = null;
                Auth.authKey = null;
                Auth.loginToken = null;
                Auth.serverTime = null;
                Auth.loginTime = null;
                Auth.timeOffset = null;
            }
            private static String getAPWD(String ... strings)
            {
                StringBuilder builder = new StringBuilder();
                builder.append(HAK + HAS);
                for (String string : strings)
                {
                    builder.append(string);
                }
                return CrypUtil.encryptSHA512(builder.toString());
            }
            private static String getMPWD(String ...strings)
            {
                StringBuilder builder = new StringBuilder();
                for (String string : strings)
                {
                    builder.append(string);
                }
                return CrypUtil.encryptSHA512(builder.toString());
            }
            public static String buildPayload(String ... strings) throws UnsupportedEncodingException
            {
                if(strings.length%2 != 0)
                {
                    throw new IllegalArgumentException("Wrong number of parameters");
                }
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<strings.length; i+=2)
                {
                    if(first)
                    {
                        first = false;
                    }
                    else
                    {
                        sb.append("&");
                    }
                    sb.append(strings[i]);
                    sb.append("=");
                    sb.append(strings[i+1]);
                }
                return sb.toString();
            }
        }


/////////////////////////////////////////////////// APIS /////////////////////////////////////////////////////////
    public static void search(final Context context,String str, final IAPICallback callback)
    {
        try
        {
            CallInformation callInfo = CallInformation.POST(SEND_STRING_URL, str);
            final APICallTask call=new APICallTask(context, new IAPICall() {
                @Override
                public void response(MightySeaAPI.CallResult result) {
                    if((result.getResponse()>=200)&&(result.getResponse()<=299))
                    {
                        callback.success(result.getResponseText());
                    }
                    else
                    {
                        callback.failure(context.getResources().getString(R.string.apierror)+": "+result.getFailureMessage());
                    }
                }
            },Auth.getHeaders(str));
            call.execute(callInfo);
        }
        catch(Exception e)
        {
            callback.failure(e.getMessage());
        }
    }

        public static void register(final Context context, String name, String emailAddress, String password, final IAPICallback callback) {
            try {
                String shaPassword = CrypUtil.encryptSHA512(password);
                CallInformation callInfo = CallInformation.POST(REGISTER_URL, Auth.buildPayload("name", name, "email", emailAddress, "password", shaPassword));

                APICallTask call = new APICallTask(context, new IAPICall() {
                    @Override
                    public void response(MightySeaAPI.CallResult result) {
                        if( (result.getResponse() >= 200) && (result.getResponse() <=299)) {
                            callback.success(result.getResponseText());
                        } else {
                            callback.failure(context.getResources().getString(R.string.apierror) + ": "
                                    + result.getFailureMessage());
                        }
                    }
                }, Auth.getHeaders(name, emailAddress, shaPassword));

                call.execute(callInfo);

            } catch (Exception e) {
                callback.failure(e.getMessage());
            }
        }

        public static void resetPassword(final Context context, String emailAddress,
                                         final IAPICallback callback) {
            try {
                CallInformation callInfo = CallInformation.POST(RESETPASSWORD_URL, Auth.buildPayload(
                        "email", emailAddress));

                APICallTask call = new APICallTask(context, new IAPICall() {
                    @Override
                    public void response(MightySeaAPI.CallResult result) {
                        if( (result.getResponse() >= 200) && (result.getResponse() <=299)) {
                            callback.success(result.getResponseText());
                        } else {
                            callback.failure(context.getResources().getString(R.string.apierror) + ": "
                                    + result.getFailureMessage());
                        }
                    }
                }, Auth.getHeaders(emailAddress));

                call.execute(callInfo);
            } catch (Exception e) {
                callback.failure(e.getMessage());
            }
        }

        public static void logout(final Context context, final IAPICallback callback) {
            try {
                final String uname;
                final String loginToken;

                String data = StoredData.get(context, StoredData.UDATA);
                if(data == null) {
                    throw new RuntimeException();
                } else {
                    String [] values = data.split(":");

                    if(values.length != 3) {
                        StoredData.remove(context, StoredData.UDATA);
                        throw new RuntimeException();
                    }

                    uname = values[0];
                    loginToken = values[2];
                }

                CallInformation callInfo = CallInformation.POST(LOGOUT_URL, Auth.buildPayload("email", uname,
                        "logintoken", loginToken));

                APICallTask call = new APICallTask(context, new IAPICall() {
                    @Override
                    public void response(MightySeaAPI.CallResult result) {
                        if( (result.getResponse() >= 200) && (result.getResponse() <=299)) {
                            Auth.resetAuthenticationDetails();

                            StoredData.remove(context, StoredData.UDATA);
                            callback.success(result.getResponseText());
                        } else {
                            callback.failure(context.getResources().getString(R.string.apierror) + ": "
                                    + result.getFailureMessage());
                        }
                    }
                }, Auth.getHeaders(uname, loginToken));

                call.execute(callInfo);
            } catch (Exception e) {
                callback.failure(e.getMessage());
            }
        }

        private static void doLogin(final Context context,
                                    final String uname, final String shaPassword, final IAPICallback callback) {
            try {
                CallInformation callInfo = CallInformation.POST(LOGIN_URL, Auth.buildPayload("email", uname,
                        "password", shaPassword));

                APICallTask call = new APICallTask(context, new IAPICall() {
                    @Override
                    public void response(MightySeaAPI.CallResult result) {
                        if( (result.getResponse() >= 200) && (result.getResponse() <=299)) {
                            String lToken = null;
                            String aKey = null;
                            Integer sTime = null;

                            try {
                                JSONObject json = new JSONObject(result.getResponseText());
                                aKey = (String)json.get("AuthKey");
                                lToken = (String)json.get("loginToken");
                                sTime = (Integer)json.get("serverTime");

                                Auth.setAuthenticationDetails(uname, shaPassword, aKey, lToken, sTime);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String valueToStore = uname + ":" + shaPassword + ":" + lToken ;
                            StoredData.set(context, StoredData.UDATA, valueToStore);

                            callback.success(result.getResponseText());
                        } else {
                            callback.failure(context.getResources().getString(R.string.apierror) + ": "
                                    + result.getFailureMessage());
                        }
                    }
                }, Auth.getHeaders(uname, shaPassword));

                call.execute(callInfo);

            } catch (Exception e) {
                callback.failure(e.getMessage());
            }
        }

        public static void login(final Context context,
                                 final String uname, String password, final IAPICallback callback) {

            final String shaPassword = CrypUtil.encryptSHA512(password);
            doLogin(context, uname, shaPassword, callback);
        }

        public static void autoLogin(final Context context, final IAPICallback callback) {
            final String uname;
            final String shaPassword;

            String data = StoredData.get(context, StoredData.UDATA);
            if(data == null) {
                throw new RuntimeException();
            } else {
                String [] values = data.split(":");

                if(values.length != 3) {
                    StoredData.remove(context, StoredData.UDATA);
                    throw new RuntimeException();
                }

                uname = values[0];
                shaPassword = values[1];
            }

            doLogin(context, uname, shaPassword, callback);
        }
}
