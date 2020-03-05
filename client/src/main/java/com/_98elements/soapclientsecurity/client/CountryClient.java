package com._98elements.soapclientsecurity.client;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

class CountryClient extends WebServiceGatewaySupport {

    public GetCountryResponse getCountry(String country) {
        GetCountryRequest request = new GetCountryRequest();
        request.setName(country);

        return (GetCountryResponse) getWebServiceTemplate()
                .marshalSendAndReceive("https://localhost:8080/ws/countries", request);
    }

}