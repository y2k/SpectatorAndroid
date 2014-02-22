package net.itwister.spectator.model.web;

import java.net.HttpURLConnection;
import java.net.URL;

public interface SpectatorWebClient {

	SpectatorApi api();

	HttpURLConnection open(URL url);
}