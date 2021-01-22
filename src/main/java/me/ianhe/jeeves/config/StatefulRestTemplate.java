package me.ianhe.jeeves.config;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * RestTemplate with state. Inspired by https://stackoverflow.com/a/12840202/2364882
 *
 * @author iHelin
 * @since 2018/8/13 20:54
 */
public class StatefulRestTemplate extends RestTemplate {

    private final HttpContext httpContext = new BasicHttpContext();

    StatefulRestTemplate() {
        super();
        HttpClient httpClient = HttpClientBuilder.create().build();
        CookieStore cookieStore = new BasicCookieStore();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, RequestConfig.custom().setRedirectsEnabled(false).build());
        StatefulHttpComponentsClientHttpRequestFactory requestFactory
                = new StatefulHttpComponentsClientHttpRequestFactory(httpClient, httpContext);
        setRequestFactory(requestFactory);
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    static class StatefulHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
        private final HttpContext httpContext;

        StatefulHttpComponentsClientHttpRequestFactory(HttpClient httpClient, HttpContext httpContext) {
            super(httpClient);
            this.httpContext = httpContext;
        }

        @Override
        protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
            return this.httpContext;
        }
    }
}