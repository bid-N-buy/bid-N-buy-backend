package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.SaveAmountRequest;
import com.bidnbuy.server.exception.PaymentErrorResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/payments")
@Controller
public class PaymentController {

    /**
     * 결제 금액을 세션에 임시저장
     * 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도
     */
    @PostMapping("/saveAmount")
    public ResponseEntity<?> saveAmount(HttpSession session, @RequestBody SaveAmountRequest saveAmountRequest) {
        session.setAttribute(saveAmountRequest.getOrderId(), saveAmountRequest.getAmount());
        return ResponseEntity.ok("Payment temp save successful");
    }

    /**
     * 결제 금액을 검증
     */
    @PostMapping("/verifyAmount")
    public ResponseEntity<?> verifyAmount(HttpSession session, @RequestBody SaveAmountRequest saveAmountRequest) {

        Object savedAmount = session.getAttribute(saveAmountRequest.getOrderId());

        // 세션에 금액 정보가 없거나 타입이 예상과 다를 경우
        if (savedAmount == null) {
            return ResponseEntity.badRequest().body(
                    PaymentErrorResponse.builder()
                            .code(400)
                            .message("결제 금액 정보가 유효하지 않습니다.")
                            .build()
            );
        }

        String amountInSession = String.valueOf(savedAmount);
        String amountFromRequest = String.valueOf(saveAmountRequest.getAmount());

        // 결제 금액 검증
        if (!amountInSession.equals(amountFromRequest)) {
            return ResponseEntity.badRequest().body(
                    PaymentErrorResponse.builder()
                            .code(400)
                            .message("결제 금액이 일치하지 않습니다.")
                            .build()
            );
        }

        // 검증 완료 후 세션에서 제거
        session.removeAttribute(saveAmountRequest.getOrderId());

        return ResponseEntity.ok("Payment is valid");
    }


//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
//    private static final String API_SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";
//    private final Map<String, String> billingKeyMap = new HashMap<>();
//
//    @RequestMapping(value = {"/confirm/widget", "/confirm/payment"})
//    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
//        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
//        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");
//        int statusCode = response.containsKey("error") ? 400 : 200;
//        return ResponseEntity.status(statusCode).body(response);
//    }
//
//    @RequestMapping(value = "/confirm-billing")
//    public ResponseEntity<JSONObject> confirmBilling(@RequestBody String jsonBody) throws Exception {
//        JSONObject requestData = parseRequestData(jsonBody);
//        String billingKey = billingKeyMap.get(requestData.get("customerKey"));
//        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/" + billingKey);
//        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
//    }
//
//    @RequestMapping(value = "/issue-billing-key")
//    public ResponseEntity<JSONObject> issueBillingKey(@RequestBody String jsonBody) throws Exception {
//        JSONObject requestData = parseRequestData(jsonBody);
//        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/authorizations/issue");
//
//        if (!response.containsKey("error")) {
//            billingKeyMap.put((String) requestData.get("customerKey"), (String) response.get("billingKey"));
//        }
//
//        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
//    }
//
//    @RequestMapping(value = "/callback-auth", method = RequestMethod.GET)
//    public ResponseEntity<JSONObject> callbackAuth(@RequestParam String customerKey, @RequestParam String code) throws Exception {
//        JSONObject requestData = new JSONObject();
//        requestData.put("grantType", "AuthorizationCode");
//        requestData.put("customerKey", customerKey);
//        requestData.put("code", code);
//
//        String url = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";
//        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);
//
//        logger.info("Response Data: {}", response);
//
//        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
//    }
//
//    @RequestMapping(value = "/confirm/brandpay", method = RequestMethod.POST, consumes = "application/json")
//    public ResponseEntity<JSONObject> confirmBrandpay(@RequestBody String jsonBody) throws Exception {
//        JSONObject requestData = parseRequestData(jsonBody);
//        String url = "https://api.tosspayments.com/v1/brandpay/payments/confirm";
//        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);
//        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
//    }
//
//    private JSONObject parseRequestData(String jsonBody) {
//        try {
//            return (JSONObject) new JSONParser().parse(jsonBody);
//        } catch (ParseException e) {
//            logger.error("JSON Parsing Error", e);
//            return new JSONObject();
//        }
//    }
//
//    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
//        HttpURLConnection connection = createConnection(secretKey, urlString);
//        try (OutputStream os = connection.getOutputStream()) {
//            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
//        }
//
//        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
//             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
//            return (JSONObject) new JSONParser().parse(reader);
//        } catch (Exception e) {
//            logger.error("Error reading response", e);
//            JSONObject errorResponse = new JSONObject();
//            errorResponse.put("error", "Error reading response");
//            return errorResponse;
//        }
//    }
//
//    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//        return connection;
//    }
//
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String index() {
//        return "/widget/checkout";
//    }
//
//    @RequestMapping(value = "/fail", method = RequestMethod.GET)
//    public String failPayment(HttpServletRequest request, Model model) {
//        model.addAttribute("code", request.getParameter("code"));
//        model.addAttribute("message", request.getParameter("message"));
//        return "/fail";
//    }
}
