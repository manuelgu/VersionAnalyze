package eu.manuelgu.versionanalyze.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.manuelgu.versionanalyze.VersionAnalyzePlugin;
import org.json.JSONObject;

public class APIUtil {

    /**
     * Get the fully qualified version name by the protocol version number.<br><br>
     *
     * Request format:<br>
     * <code>
     *     POST {url}<br>
     *     Content-Type: application/json<br>
     *     <br>
     *     {'protocol_version': 42}
     * </code>
     *
     *
     * @param protocolVersionNumber The protocol version number of the requested version
     * @return A fully qualified name of the version (e.g. 1.11.2)
     */
    public static String getVersionByProtocolVersion(int protocolVersionNumber) {
        // Create JSON payload
        JSONObject payload = new JSONObject().put("protocol_version", protocolVersionNumber);

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.post(VersionAnalyzePlugin.get().getApiUrl())
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asJson();
        } catch (UnirestException e) {
            // Unable to request (access API)
            VersionAnalyzePlugin.get().getLogger().warning("Error while sending request to API - " + e.getMessage());
            return null;
        }

        if (response.getStatus() == 200) {
            JSONObject responseObj = response.getBody().getObject();

            String version = responseObj.getString("name");
            if (version != null) {
                // Found it! Returning correct version string
                return version;
            } else {
                // Error parsing response
                VersionAnalyzePlugin.get().getLogger().warning("Error while parsing the response body - key not found");
            }
        } else {
            // Unknown response code
            VersionAnalyzePlugin.get().getLogger().warning("Unknown response code " + response.getStatus() + " for request");
        }
        return null;
    }
}
