package CafeFinder.cafe.global.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GoogleApiController {

    @Value("${google.map.key}")
    private String googleApiKey;

    @GetMapping("/google-key")
    public ResponseEntity<Map<String, String>> getGoogleKey() {
        Map<String, String> body = new HashMap<>();
        body.put("key", googleApiKey);
        return ResponseEntity.ok(body);
    }
}
