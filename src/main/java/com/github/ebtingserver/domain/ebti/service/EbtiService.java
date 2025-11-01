package com.github.ebtingserver.domain.ebti.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.domain.ebti.dto.request.EbtiCalculateRequest;
import com.github.ebtingserver.domain.ebti.dto.response.EbtiCalculateResponse;
import com.github.ebtingserver.domain.ebti.exception.EbtiExceptionCode;
import com.github.ebtingserver.domain.user.entity.User;
import com.github.ebtingserver.domain.user.exception.UserExceptionCode;
import com.github.ebtingserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EbtiService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Transactional
    public EbtiCalculateResponse calculateEbti(Long userId, EbtiCalculateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.USER_NOT_FOUND));

        EbtiCalculateResponse response = callFastApi(request);
        user.updateEbti(response.getPred());

        return response;
    }

    private EbtiCalculateResponse callFastApi(EbtiCalculateRequest request) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(request);
            HttpURLConnection conn = createConnection();

            sendRequest(conn, jsonPayload);

            return readResponse(conn);
        } catch (Exception e) {
            throw new CustomException(EbtiExceptionCode.FASTAPI_REQUEST_FAILED);
        }
    }

    private HttpURLConnection createConnection() {
        try {
            URL url = new URL(fastApiUrl + "/predict");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            return conn;
        } catch (Exception e) {
            throw new CustomException(EbtiExceptionCode.FASTAPI_CONNECTION_FAILED);
        }
    }

    private void sendRequest(HttpURLConnection conn, String jsonPayload) {
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new CustomException(EbtiExceptionCode.FASTAPI_REQUEST_FAILED);
        }
    }

    private EbtiCalculateResponse readResponse(HttpURLConnection conn) {
        try {
            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseBody = br.lines().reduce("", String::concat);
                    return objectMapper.readValue(responseBody, EbtiCalculateResponse.class);
                }
            } else {
                throw new CustomException(EbtiExceptionCode.FASTAPI_REQUEST_FAILED);
            }
        } catch (Exception e) {
            throw new CustomException(EbtiExceptionCode.INVALID_RESPONSE);
        }
    }
}
