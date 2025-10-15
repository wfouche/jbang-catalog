// spotless:off
//DEPS com.google.code.gson:gson:2.13.2
// spotless:on

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class JBangImport {

    /**
     * Determines the location of the Jbang installation directory. It checks the JBANG_DIR
     * environment variable first. If not found, it defaults to the user's home directory
     * (~/.jbang).
     *
     * @return The Path to the .jbang directory.
     */
    public static Path getJbangDir() {
        // 1. Check for the JBANG_DIR environment variable
        String jbangDirEnv = System.getenv("JBANG_DIR");

        if (jbangDirEnv != null && !jbangDirEnv.trim().isEmpty()) {
            return Paths.get(jbangDirEnv);
        }

        // 2. If the environment variable is not set, use the default location
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".jbang");
    }

    /**
     * Finds the Maven GAV (GroupId:ArtifactId:Version) for a given SHA-1 hash by querying the Maven
     * Central search API.
     *
     * @param sha1 The SHA-1 checksum of the artifact.
     * @return An Optional containing the GAV string if found, otherwise an empty Optional.
     * @throws IOException If an I/O error occurs when sending or receiving.
     * @throws InterruptedException If the operation is interrupted.
     */
    public static Optional<String> findGavBySha1(String sha1)
            throws IOException, InterruptedException {
        // A single, reusable HttpClient is efficient
        HttpClient httpClient = HttpClient.newHttpClient();
        // Reusable Gson instance
        Gson gson = new Gson();

        // 1. Construct the API request URL
        String url =
                String.format("https://search.maven.org/solrsearch/select?q=1:%s&wt=json", sha1);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .build();

        // 2. Send the request and get the response body as a String
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 3. Check for a successful HTTP response
        if (response.statusCode() != 200) {
            System.err.println("⚠️ API request failed with status code: " + response.statusCode());
            return Optional.empty();
        }

        // 4. Parse the JSON and extract the GAV
        JsonObject root = gson.fromJson(response.body(), JsonObject.class);
        JsonObject responseObj = root.getAsJsonObject("response");

        // Check if the 'numFound' field is greater than 0
        if (responseObj != null && responseObj.get("numFound").getAsInt() > 0) {
            JsonArray docs = responseObj.getAsJsonArray("docs");
            if (docs.size() > 0) {
                // Get the first result from the 'docs' array
                JsonObject firstDoc = docs.get(0).getAsJsonObject();
                String g = firstDoc.get("g").getAsString(); // GroupId
                String a = firstDoc.get("a").getAsString(); // ArtifactId
                String v = firstDoc.get("v").getAsString(); // Version

                // 5. Return the formatted GAV string
                return Optional.of(String.format("%s:%s:%s", g, a, v));
            }
        }

        // Return empty if numFound was 0 or the structure was unexpected
        return Optional.empty();
    }

    public static void main(String... args) throws InterruptedException {
        // 1. Validate input argument
        if (args.length == 0) {
            System.err.println("Error: Please provide a directory path.");
            System.err.println("Usage: jbang JBangImport.java <directory>");
            System.exit(1);
        }

        Path dir = Paths.get(args[0]);

        if (!Files.isDirectory(dir)) {
            System.err.println("Error: '" + dir + "' is not a valid directory.");
            System.exit(1);
        }

        System.out.println("Calculating //DEPS for JAR files in: " + dir.toAbsolutePath());
        System.out.println(
                "-------------------------------------------------------------------------");

        // 2. Iterate through all files ending with .jar
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jar")) {
            for (Path jarFile : stream) {
                try {
                    // 3. Calculate and print SHA-1 hash for each file
                    String sha1 = calculateSha1(jarFile);
                    // System.out.printf("%-40s: %s%n", jarFile.getFileName(), sha1);
                    System.out.println(
                            "//DEPS "
                                    + findGavBySha1(sha1)
                                            .orElse("[" + jarFile.toString() + ", " + sha1 + "]"));
                } catch (IOException | NoSuchAlgorithmException e) {
                    System.err.printf(
                            "Could not process file %s: %s%n",
                            jarFile.getFileName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
            System.exit(1);
        }
    }

    /** Calculates the SHA-1 hash of a file efficiently. */
    private static String calculateSha1(Path file) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        // Use DigestInputStream to calculate hash while reading the file,
        // which is memory-efficient for large files.
        try (InputStream is = Files.newInputStream(file);
                DigestInputStream dis = new DigestInputStream(is, sha1)) {

            byte[] buffer = new byte[8192]; // 8KB buffer
            while (dis.read(buffer) != -1) {
                // The digest is updated automatically as the stream is read
            }
        }

        byte[] digest = sha1.digest();
        return toHexString(digest);
    }

    /** Converts a byte array into a hexadecimal string. */
    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Format each byte as a two-character hex string (with a leading zero if needed)
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
