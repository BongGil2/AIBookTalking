package com.eduhansol.fmlibrary.network;

import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * implementation 'com.google.code.gson:gson:2.8.5'
 * implementation 'com.squareup.retrofit2:converter-scalars:2.7.2'
 * implementation 'com.squareup.retrofit2:retrofit:2.6.1'
 * implementation 'com.squareup.retrofit2:adapter-rxjava2:2.6.1'
 * implementation 'com.squareup.retrofit2:converter-gson:2.6.1'
 */
public class FMRetrofit2 {
    private final String mBaseUrl;
    private Context mContext = null;
    private Retrofit mRetrofit = null;
    private Class mServiceClass = null;
    private Object mApiService = null;
    private boolean mIsIgnoreSSL = true;
    private String mSSLKey = "";
    private Map<String, String> mHeaderMap = null;

    public FMRetrofit2(Context c, String baseUrl) {
        mContext = c;
        mBaseUrl = baseUrl;
    }

    /**
     * OPEN SSL 우회와 Key값 설정
     *
     * @param isIgnore 우회 여부
     * @param key      우회 하지 않을 경우 Key값
     */
    public void setSsl(boolean isIgnore, String key) {
        mIsIgnoreSSL = isIgnore;
        mSSLKey = key;
    }


    /**
     * 결과값이 String 으로 전달 받을때
     *
     * @param service
     */
    public void setRetrofit(Class service) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getUnsafeOkHttpClient().build())
                .build();

        setService(service);
    }

    /**
     * 결과값을 Gson으로 변경하여 받을
     *
     * @param service
     */
    public void setRetrofitToGson(Class service) {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mBaseUrl)
                .client(getUnsafeOkHttpClient().build())
                .build();

        setService(service);
    }

    public void setRetrofitToRxJava(Class service) {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(mBaseUrl)
                .client(getUnsafeOkHttpClient().build())
                .build();

        setService(service);
    }

    public void setHeader(Map<String, String> dataMap) {
        mHeaderMap = dataMap;
    }

    private void setService(final Class service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }

        mServiceClass = service;
        mApiService = mRetrofit.create(service);
    }

    public void getWebData(Call call, final DMRetroCallback callback) {
        if (call == null) {
            //Toast.makeText(mContext, "Call is null!!", Toast.LENGTH_LONG).show();
            return;
        }

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code(), response.body());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getWebData(String method, final DMRetroCallback callback, Object... vals) {
        Call call = makeCall(method, vals);
        if (call == null) {
            //Toast.makeText(mContext, "Call is null!!", Toast.LENGTH_LONG).show();
            return;
        }

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code(), response.body());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * Parameter와 method로 Call 형태로 만듦.
     *
     * @param method
     * @param vals
     * @return
     */
    public Call makeCall(String method, Object... vals) {
        try {
            Object[] obj = vals;
            Class[] cla = new Class[obj.length];
            for (int i = 0; i < vals.length; i++) {
                cla[i] = obj[i].getClass();
            }

            Method m = mServiceClass.getMethod(method, cla);
            return (Call) (m.invoke(mApiService, obj));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 인증서 없이 우회 하여 ssl 접근하기 위하여 사용
    private OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            if (!mIsIgnoreSSL) {
                sslSocketFactory = getPinnedCertSslSocketFactory();
            }

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            //해더 추가 부분
            if (mHeaderMap != null) {
                builder.addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request newRequest;
                        Request.Builder newRequestBuilder = chain.request().newBuilder();
                        for (Map.Entry<String, String> elem : mHeaderMap.entrySet()) {
                            newRequestBuilder.addHeader(elem.getKey(), elem.getValue());
                        }
                        newRequest = newRequestBuilder.build();
                        return chain.proceed(newRequest);
                    }
                });
            }

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * openssl 로 key 값을 가지고 보안 설정 할때 사용
     *
     * @return
     */
    private SSLSocketFactory getPinnedCertSslSocketFactory() {
        try {

            String certificateString = mSSLKey;

            ByteArrayInputStream derInputStream = new ByteArrayInputStream(certificateString.getBytes());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(derInputStream);
            String alias = cert.getSubjectX500Principal().getName();

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry(alias, cert);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory sf = new SSLSocketFactory() {
                @Override
                public String[] getDefaultCipherSuites() { return new String[0]; }

                @Override
                public String[] getSupportedCipherSuites() {
                    return new String[0];
                }

                @Override
                public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
                    return sslContext.getSocketFactory().createSocket(s, host, port, autoClose);
                }

                @Override
                public Socket createSocket(String host, int port) throws IOException {
                    return sslContext.getSocketFactory().createSocket(host, port);
                }

                @Override
                public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
                    return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
                }

                @Override
                public Socket createSocket(InetAddress host, int port) throws IOException {
                    return sslContext.getSocketFactory().createSocket(host, port);
                }

                @Override
                public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
                    return sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
                }
            };

            return sf;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface DMRetroCallback<T> {
        void onError(Throwable t);

        void onSuccess(int code, T receivedData);

        void onFailure(int code, T receivedData);
    }
}
