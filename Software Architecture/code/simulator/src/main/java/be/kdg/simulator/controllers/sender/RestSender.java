package be.kdg.simulator.controllers.sender;

import org.springframework.http.ResponseEntity;

public interface RestSender<T> {
    ResponseEntity getRequest(String url, Class responseType);
    ResponseEntity<T> postRequest(String url, Object request, Class<T> responseType);
}
