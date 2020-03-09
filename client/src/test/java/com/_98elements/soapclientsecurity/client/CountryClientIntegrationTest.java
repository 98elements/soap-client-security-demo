package com._98elements.soapclientsecurity.client;

import io.spring.guides.gs_producing_web_service.Country;
import io.spring.guides.gs_producing_web_service.Currency;
import net.sf.ehcache.search.aggregator.Count;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CountryClientIntegrationTest {

    @Autowired
    CountryClient countryClient;

    @Test
    void shouldDownloadCountry() {
        // given
        String countryName = "Poland";

        // when
        Country country = countryClient.getCountry(countryName).getCountry();

        // then
        Country expectedCountry = new Country();
        expectedCountry.setName("Poland");
        expectedCountry.setCapital("Warsaw");
        expectedCountry.setCurrency(Currency.PLN);
        expectedCountry.setPopulation(38186860);

        assertThat(country.getName()).isEqualTo("Poland");
        assertThat(country.getCapital()).isEqualTo("Warsaw");
        assertThat(country.getCurrency()).isEqualTo(Currency.PLN);
        assertThat(country.getPopulation()).isEqualTo(38186860);
    }

}
