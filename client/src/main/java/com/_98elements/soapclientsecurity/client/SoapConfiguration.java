package com._98elements.soapclientsecurity.client;

import org.apache.wss4j.common.WSS4JConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.support.KeyManagersFactoryBean;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import java.io.IOException;

@Configuration
class SoapConfiguration {

    private static final Resource KEYSTORE_LOCATION = new ClassPathResource("client-keystore.jks");
    private static final String KEYSTORE_PASSWORD = "keystore";
    private static final String KEY_ALIAS = "client-cert";

    @Bean
    Jaxb2Marshaller marshaller() {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("io.spring.guides.gs_producing_web_service");
        return marshaller;
    }

    @Bean
    KeyStoreFactoryBean keyStore() {
        var factoryBean = new KeyStoreFactoryBean();
        factoryBean.setLocation(KEYSTORE_LOCATION);
        factoryBean.setPassword(KEYSTORE_PASSWORD);
        return factoryBean;
    }

    @Bean
    TrustManagersFactoryBean trustManagers(KeyStoreFactoryBean keyStore) {
        var factoryBean = new TrustManagersFactoryBean();
        factoryBean.setKeyStore(keyStore.getObject());
        return factoryBean;
    }

    @Bean
    HttpsUrlConnectionMessageSender messageSender(
            KeyStoreFactoryBean keyStore,
            TrustManagersFactoryBean trustManagers
    ) throws Exception {
        var sender = new HttpsUrlConnectionMessageSender();

        var keyManagersFactoryBean = new KeyManagersFactoryBean();
        keyManagersFactoryBean.setKeyStore(keyStore.getObject());
        keyManagersFactoryBean.setPassword(KEYSTORE_PASSWORD);
        keyManagersFactoryBean.afterPropertiesSet();

        sender.setKeyManagers(keyManagersFactoryBean.getObject());

        sender.setTrustManagers(trustManagers.getObject());
        return sender;
    }

    @Bean
    CryptoFactoryBean cryptoFactoryBean() throws IOException {
        var cryptoFactoryBean = new CryptoFactoryBean();
        cryptoFactoryBean.setKeyStoreLocation(KEYSTORE_LOCATION);
        cryptoFactoryBean.setKeyStorePassword(KEYSTORE_PASSWORD);
        return cryptoFactoryBean;
    }

    @Bean
    Wss4jSecurityInterceptor securityInterceptor(CryptoFactoryBean cryptoFactoryBean) throws Exception {
        var securityInterceptor = new Wss4jSecurityInterceptor();

        securityInterceptor.setSecurementActions("Signature");
        securityInterceptor.setSecurementUsername(KEY_ALIAS);
        securityInterceptor.setSecurementPassword(KEYSTORE_PASSWORD);

        securityInterceptor.setSecurementSignatureKeyIdentifier("DirectReference");
        securityInterceptor.setSecurementSignatureAlgorithm(WSS4JConstants.RSA_SHA1);
        securityInterceptor.setSecurementSignatureDigestAlgorithm(WSS4JConstants.SHA1);

        securityInterceptor.setSecurementSignatureCrypto(cryptoFactoryBean.getObject());

        return securityInterceptor;
    }

    @Bean
    CountryClient countryClient(
            Jaxb2Marshaller marshaller,
            HttpsUrlConnectionMessageSender messageSender,
            Wss4jSecurityInterceptor securityInterceptor
    ) {
        CountryClient countryClient = new CountryClient();

        countryClient.setInterceptors(new ClientInterceptor[]{securityInterceptor});
        countryClient.setMessageSender(messageSender);

        countryClient.setMarshaller(marshaller);
        countryClient.setUnmarshaller(marshaller);

        return countryClient;
    }

}
