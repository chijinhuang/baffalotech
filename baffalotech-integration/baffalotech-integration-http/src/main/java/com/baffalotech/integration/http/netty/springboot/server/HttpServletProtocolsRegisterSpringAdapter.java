package com.baffalotech.integration.http.netty.springboot.server;

import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.util.ResourceUtils;

import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.core.StandardThreadExecutor;
import com.baffalotech.integration.http.netty.protocol.HttpServletProtocolsRegister;
import com.baffalotech.integration.http.netty.servlet.NettyServletContext;
import com.baffalotech.integration.http.netty.servlet.support.NettyServletErrorPage;
import com.baffalotech.integration.http.netty.session.CompositeSessionServiceImpl;
import com.baffalotech.integration.http.netty.session.SessionService;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * httpServlet协议注册器 （适配spring）
 *
 * @author acer01
 * 2018/11/12/012
 */
public class HttpServletProtocolsRegisterSpringAdapter extends HttpServletProtocolsRegister {

    public HttpServletProtocolsRegisterSpringAdapter(IntegrationServerProperties properties, NettyServletContext servletContext,
    		StandardThreadExecutor serverExecutor,AbstractServletWebServerFactory configurableWebServer) throws Exception {
        super(properties,
                servletContext,
                serverExecutor,
                configurableWebServer.getSsl() != null && configurableWebServer.getSsl().isEnabled()?
                        SslContextBuilder.forServer(getKeyManagerFactory(configurableWebServer.getSsl(),configurableWebServer.getSslStoreProvider())):null);
        initServletContext(servletContext,configurableWebServer,properties);
        if(configurableWebServer.getSsl() != null && configurableWebServer.getSsl().isEnabled()) {
            initSslContext(configurableWebServer);
        }
    }

    /**
     * 初始化servlet上下文
     * @return
     */
    protected NettyServletContext initServletContext(NettyServletContext servletContext,AbstractServletWebServerFactory configurableWebServer, IntegrationServerProperties properties){
        servletContext.setContextPath(configurableWebServer.getContextPath());
        servletContext.setServerHeader(configurableWebServer.getServerHeader());
        servletContext.setServletContextName(configurableWebServer.getDisplayName());

        //session超时时间
        servletContext.setSessionTimeout((int) configurableWebServer.getSession().getTimeout().getSeconds());
        servletContext.setSessionService(newSessionService(properties,servletContext));
        for (MimeMappings.Mapping mapping :configurableWebServer.getMimeMappings()) {
            servletContext.getMimeMappings().add(mapping.getExtension(),mapping.getMimeType());
        }

        //注册错误页
        for(ErrorPage errorPage : configurableWebServer.getErrorPages()) {
            NettyServletErrorPage servletErrorPage = new NettyServletErrorPage(errorPage.getStatusCode(),errorPage.getException(),errorPage.getPath());
            servletContext.getErrorPageManager().add(servletErrorPage);
        }
        return servletContext;
    }

    /**
     * 新建会话服务
     * @return
     */
    protected SessionService newSessionService(IntegrationServerProperties properties,NettyServletContext servletContext){
        //组合会话 (默认本地存储)
        CompositeSessionServiceImpl compositeSessionService = new CompositeSessionServiceImpl();
        return compositeSessionService;
    }

    /**
     * 初始化 HTTPS的SSL 安全配置
     * @param configurableWebServer
     * @return SSL上下文
     * @throws Exception
     */
    protected SslContextBuilder initSslContext(AbstractServletWebServerFactory configurableWebServer) throws Exception {
        SslContextBuilder builder = getSslContextBuilder();
        Ssl ssl = configurableWebServer.getSsl();
        SslStoreProvider sslStoreProvider = configurableWebServer.getSslStoreProvider();

        builder.trustManager(getTrustManagerFactory(ssl, sslStoreProvider));
        if (ssl.getEnabledProtocols() != null) {
            builder.protocols(ssl.getEnabledProtocols());
        }
        if (ssl.getCiphers() != null) {
            builder.ciphers(Arrays.asList(ssl.getCiphers()));
        }
        if (ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            builder.clientAuth(ClientAuth.REQUIRE);
        }
        else if (ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            builder.clientAuth(ClientAuth.OPTIONAL);
        }

        ApplicationProtocolConfig protocolConfig = new ApplicationProtocolConfig(
                ApplicationProtocolConfig.Protocol.ALPN,
                // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                ApplicationProtocolNames.HTTP_2,
                ApplicationProtocolNames.HTTP_1_1);
        builder.applicationProtocolConfig(protocolConfig);

        return builder;
    }

    /**
     * 获取信任管理器，用于对安全套接字执行身份验证。
     * @param ssl
     * @param sslStoreProvider
     * @return
     * @throws Exception
     */
    protected TrustManagerFactory getTrustManagerFactory(Ssl ssl,SslStoreProvider sslStoreProvider) throws Exception {
        KeyStore store;
        if (sslStoreProvider != null) {
            store = sslStoreProvider.getTrustStore();
        }else {
            store = loadKeyStore(ssl.getTrustStoreType(), ssl.getTrustStoreProvider(),ssl.getTrustStore(), ssl.getTrustStorePassword());
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(store);
        return trustManagerFactory;
    }

    /**
     * 获取密匙管理器
     * @param ssl
     * @param sslStoreProvider
     * @return
     * @throws Exception
     */
    private static KeyManagerFactory getKeyManagerFactory(Ssl ssl,SslStoreProvider sslStoreProvider) throws Exception {
        KeyStore keyStore;
        if (sslStoreProvider != null) {
            keyStore = sslStoreProvider.getKeyStore();
        }else {
            keyStore = loadKeyStore(ssl.getKeyStoreType(), ssl.getKeyStoreProvider(),ssl.getKeyStore(), ssl.getKeyStorePassword());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        char[] keyPassword = (ssl.getKeyPassword() != null) ? ssl.getKeyPassword().toCharArray() : null;
        if (keyPassword == null && ssl.getKeyStorePassword() != null) {
            keyPassword = ssl.getKeyStorePassword().toCharArray();
        }
        keyManagerFactory.init(keyStore, keyPassword);
        return keyManagerFactory;
    }

    /**
     * 加载密匙
     * @param type
     * @param provider
     * @param resource
     * @param password
     * @return
     * @throws Exception
     */
    private static KeyStore loadKeyStore(String type, String provider, String resource,String password) throws Exception {
        if (resource == null) {
            return null;
        }
        type = (type != null) ? type : "JKS";
        KeyStore store = (provider != null) ? KeyStore.getInstance(type, provider) : KeyStore.getInstance(type);
        URL url = ResourceUtils.getURL(resource);
        store.load(url.openStream(), (password == null) ? null : password.toCharArray());
        return store;
    }

    @Override
    public void onServerStart() throws Exception {
        super.onServerStart();
    }
}
