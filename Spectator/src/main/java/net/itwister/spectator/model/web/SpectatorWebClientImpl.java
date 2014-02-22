package net.itwister.spectator.model.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.itwister.spectator.App;
import net.itwister.spectator.Constants;
import net.itwister.spectator.model.account.PermanentCookieStorage;

import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.ProtoConverter;
import retrofit.mime.TypedInput;

import com.squareup.okhttp.OkHttpClient;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

@Singleton
public class SpectatorWebClientImpl implements SpectatorWebClient {

    private final OkHttpClient client = new OkHttpClient();
	private SpectatorApi api;

	public SpectatorWebClientImpl() {
		initializeSsl();
	}

	@Override
	public synchronized SpectatorApi api() {
		if (api == null) {
			RestAdapter rest = new RestAdapter.Builder()
					.setEndpoint(Constants.Url.URL_HOST)
					.setClient(new CookieOkClient(client))
					.setConverter(new VoidProtoConverter())
					.build();
			api = rest.create(SpectatorApi.class);
		}
		return api;
	}

	@Override
	public HttpURLConnection open(URL url) {
		return client.open(url);
	}

	private void initializeSsl() {
        try {
            final SSLContext context = SSLContext.getInstance("TLS");
            final KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(App.getInstance().getAssets().open("certificate.pfx"), "1".toCharArray());
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, "1".toCharArray());
            context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { new X509TrustManager() {

                @Override
                public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
                    // TODO Auto-generated method stub
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
                    // TODO Auto-generated method stub
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[] {};
                }
            } }, new SecureRandom());

            client.setSslSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class VoidProtoConverter extends ProtoConverter {

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            if (type == Void.class) return null;
            return super.fromBody(body, type);
        }
    }

    private static class CookieOkClient extends OkClient {

     	private static final Pattern COOKIE_PATTERN = Pattern.compile("([^=]+)=([^;]+);\\s?");
		private static final String SET_COOKIE = "Set-Cookie";
		private static final String COOKIE = "Cookie";

        private final CookieStore cookieStore;

        public CookieOkClient(OkHttpClient client) {
            super(client);
            cookieStore = PermanentCookieStorage.getInstance();
        }

        @Override
        public Response execute(Request request) throws IOException {
            Response response = super.execute(request);
            detacheCookiesFromResponse(response);
            return response;
        }

        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {
            HttpURLConnection conn = super.openConnection(request);
            attachCookiesToConnection(conn);
            return conn;
        }

        private void detacheCookiesFromResponse(Response response) {
            List<String> cookies = getCookieListFromResponse(response);
			if (cookies != null) {
				for (String c : cookies) {
					Matcher m = COOKIE_PATTERN.matcher(c);
					if (m.find()) cookieStore.addCookie(new BasicClientCookie(m.group(1), m.group(2)));
				}
			}
        }

        private List<String> getCookieListFromResponse(Response response) {
            List<String> cookies = new ArrayList<>();
            for (Header h : response.getHeaders()) {
                if (SET_COOKIE.equals(h.getName())) cookies.add(h.getValue());
            }
            return cookies;
        }

        private void attachCookiesToConnection(HttpURLConnection connection) {
            StringBuilder cookie = new StringBuilder();
            for (Cookie c : cookieStore.getCookies()) {
                if (cookie.length() > 0) cookie.append(";");
                cookie.append(c.getName()).append("=").append(c.getValue());
            }
            if (cookie.length() > 0) connection.addRequestProperty(COOKIE,  cookie.toString());
        }
    }
}