package com.nashirul.zakat.service.implemetation;

import com.doku.java.library.dto.va.payment.request.CustomerRequestDto;
import com.doku.java.library.dto.va.payment.request.OrderRequestDto;
import com.doku.java.library.dto.va.payment.request.PaymentRequestDto;
import com.doku.java.library.dto.va.payment.request.VirtualAccountInfoRequestDto;
import com.doku.java.library.dto.va.payment.response.PaymentResponseDto;
import com.doku.java.library.pojo.SetupConfiguration;
import com.doku.java.library.service.va.VaServices;
import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.service.DokuService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class DokuImpl implements DokuService {
    @Value("${doku.api.client-id}")
    private String clientId;
    @Value("${doku.api.secret-key}")
    private String secretKey;

    private final SetupConfiguration setupConfiguration = SetupConfiguration.builder()
            .clientId(clientId)
            .key(secretKey)
            .environment("sandbox")
            .setupServerLocation()
            .build();
    private static final String dokuApiUrl = "api-sandbox.doku.com";

    public String initiateDokuPayment(PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException, InvalidKeyException {

        //body for request and generate digest
        String jsonBody = dokuJsonBody(paymentTransactionDto);
        String requestId = UUID.randomUUID().toString();
        String requestTimeStamp = getUtcTimestamp();

        HttpHeaders headers = new HttpHeaders();

        headers.set("Client-Id", clientId);
        headers.set("Request-Id", requestId);
        headers.set("Request-Timestamp", requestTimeStamp);
        headers.set("Signature", generateSignature(jsonBody, requestId, requestTimeStamp));
        headers.setContentType(MediaType.APPLICATION_JSON);

        System.out.println(headers);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://api-sandbox.doku.com/dokuwallet-emoney/v1/payment",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println("Response: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + responseEntity.getBody());
            return responseEntity.getBody();
        }catch (HttpClientErrorException e){
            System.out.println("Error Response: " + e.getStatusCode());
            System.out.println("Error Response Body: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    //virtual account payment
    @Override
    public String requestPayment(PaymentTransactionDto paymentTransactionDto) throws IOException {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .customer(CustomerRequestDto.builder()
                        .email(paymentTransactionDto.getEmail())
                        .name(paymentTransactionDto.getName())
                        .build())
                .order(OrderRequestDto.builder()
                        .invoiceNumber(paymentTransactionDto.getId().toString())
                        .amount(paymentTransactionDto.getAmount().toString())
                        .build())
                .virtualAccountInfo(VirtualAccountInfoRequestDto.builder()
                        .expiredTime(60)
                        .reusableStatus(false)
                        .info1("FREE TEXT 1")
                        .info2("FREE TEXT 2")
                        .info3("FREE TEXT 3")
                        .build())
                .setAdditionalInfo()
                .build();

        PaymentResponseDto paymentResponseDto = new VaServices().generateDokuVa(setupConfiguration, paymentRequestDto);
        return paymentResponseDto.getVirtualAccountInfo().getHowToPayPage();
    }

    //generate digest
    private String generateDigest(String requestBody) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(requestBody.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    private String generateSignature(String requestBody, String requestId, String requestTimeStamp)
            throws InvalidKeyException, NoSuchAlgorithmException {

        final String CLIENT_ID = "Client-Id";
        final String REQUEST_ID = "Request-Id";
        final String REQUEST_TIMESTAMP = "Request-Timestamp";
        final String REQUEST_TARGET = "Request-Target";
        final String DIGEST = "Digest";
        final String COLON_SYMBOL = ":";
        final String NEW_LINE = "\n";

        String digest = generateDigest(requestBody);

        // Prepare Signature Component
        System.out.println("----- Component Signature -----");
        StringBuilder component = new StringBuilder();
        component.append(CLIENT_ID).append(COLON_SYMBOL).append(clientId);
        component.append(NEW_LINE);
        component.append(REQUEST_ID).append(COLON_SYMBOL).append(requestId);
        component.append(NEW_LINE);
        component.append(REQUEST_TIMESTAMP).append(COLON_SYMBOL).append(requestTimeStamp);
        component.append(NEW_LINE);
        component.append(REQUEST_TARGET).append(COLON_SYMBOL).append("/dokuwallet-emoney/v1/payment");
        // If body not send when access API with HTTP method GET/DELETE
        if(digest != null && !digest.isEmpty()) {
            component.append(NEW_LINE);
            component.append(DIGEST).append(COLON_SYMBOL).append(digest);
        }

        System.out.println(component.toString());
        System.out.println();

        byte[] decodeKey = secretKey.getBytes();
        SecretKey originalKey = new SecretKeySpec(decodeKey, 0, decodeKey.length, "HmacSHA256");
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(originalKey);
        hmacSha256.update(component.toString().getBytes());
        byte[] HmacSha256DigestBytes = hmacSha256.doFinal();
        String signature = Base64.getEncoder().encodeToString(HmacSha256DigestBytes);
        //prepend encode result with algorithm info HMACSHA256=
        System.out.println(signature);
        return "HMACSHA256=" + signature;
    }

    private String dokuJsonBody(PaymentTransactionDto paymentTransactionDto){
        return new JSONObject()
                .put("order", new JSONObject()
                        .put("invoice_number", "INV-" + new Date())
                        .put("amount", paymentTransactionDto.getAmount())
                        .put("currency", "IDR")
                )
                .put("payment", new JSONObject()
                        .put("payment_do_date", 60)
                )
                .put("customer", new JSONObject()
                        .put("name", paymentTransactionDto.getName())
                        .put("phone", paymentTransactionDto.getNoWhatsApp())
                        .put("email", paymentTransactionDto.getEmail())
                ).toString();
    }
    private String getUtcTimestamp() {
        // Mendapatkan waktu saat ini dan mengonversinya ke UTC
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneOffset.UTC);
        return formatter.format(localDateTime);
    }
}
