package be.kdg.simulator.controllers.sender.impl;

import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.exceptions.ExternalSimulatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestSenderImpl<T> implements RestSender<T> {
    private final RestTemplate restTemplate;

    @Autowired
    public RestSenderImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity getRequest(String url, Class responseType) {
        try {
            return restTemplate.getForEntity(url, responseType);
        } catch (HttpClientErrorException ex) {
            throw new ExternalSimulatorException("Received an unsuccessful http response");
        } catch (ResourceAccessException ex) {
            throw new ExternalSimulatorException("Could not reach the ride service REST api");
        }
    }

    @Override
    public ResponseEntity<T> postRequest(String url, Object request, Class<T> responseType) {
        try {
            return restTemplate.postForEntity(url, request, responseType);
        } catch (HttpClientErrorException ex) {
            throw new ExternalSimulatorException("Received an unsuccessful http response");
        } catch (ResourceAccessException ex) {
            throw new ExternalSimulatorException("Could not reach the ride service REST api");
        }
    }
}
