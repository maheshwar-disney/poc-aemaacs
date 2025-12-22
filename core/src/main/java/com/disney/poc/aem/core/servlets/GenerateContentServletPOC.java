package com.disney.poc.aem.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/ai/generateContent",
                "sling.servlet.methods=POST"
        }
)
public class GenerateContentServletPOC extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateContentServletPOC.class);

    // ⚠️ POC ONLY – move to OSGi config later
    private static final String HUGGINGFACE_API_TOKEN = "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";
    private static final String HUGGINGFACE_MODEL_URL =
            "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        LOG.info("GenerateContentServletPOC :: doPost() called");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);
            LOG.debug("Request payload: {}", requestJson);

            String productName = requestJson.has("productName")
                    ? requestJson.get("productName").getAsString() : "";
            String audience = requestJson.has("audience")
                    ? requestJson.get("audience").getAsString() : "";
            String tone = requestJson.has("tone")
                    ? requestJson.get("tone").getAsString() : "";

            String prompt = String.format(
                    "You are a marketing copywriter.\n" +
                    "Generate content for:\n" +
                    "Product: %s\nAudience: %s\nTone: %s\n" +
                    "Return STRICT JSON with keys: title, description, body",
                    productName, audience, tone
            );

            LOG.info("Prompt sent to HuggingFace");
            LOG.debug("Prompt content: {}", prompt);

            String hfRawResponse = callHuggingFaceAPI(prompt);
            LOG.debug("Raw HF response: {}", hfRawResponse);

            JsonObject finalResponse = new JsonObject();

            try {
                JsonArray arr = gson.fromJson(hfRawResponse, JsonArray.class);
                String generatedText = arr.get(0)
                        .getAsJsonObject()
                        .get("generated_text")
                        .getAsString();

                finalResponse.addProperty("body", generatedText);
            } catch (Exception parseEx) {
                LOG.error("Error parsing HF response", parseEx);
                finalResponse.addProperty("body", hfRawResponse);
            }

            out.print(gson.toJson(finalResponse));
            out.flush();

            LOG.info("GenerateContentServletPOC :: response sent successfully");

        } catch (Exception e) {
            LOG.error("Error in GenerateContentServletPOC", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            response.getWriter().print(gson.toJson(error));
        }
    }

    private String callHuggingFaceAPI(String prompt) throws IOException {

        LOG.info("Calling HuggingFace API");

        URL url = new URL(HUGGINGFACE_MODEL_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + HUGGINGFACE_API_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);

        JsonObject payload = new JsonObject();
        payload.addProperty("inputs", prompt);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        LOG.info("HF response status: {}", status);

        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        if (status < 200 || status >= 300) {
            LOG.error("HF error response: {}", response);
            throw new IOException("HF API error: " + response);
        }

        return response.toString();
    }
}
