package com.shop.carrito.service;

import com.shop.carrito.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_FOLDERS = Set.of(
            "productos", "categorias", "banners", "resenas", "comprobantes"
    );
    private static final Map<String, String> MIME_TO_EXT = Map.of(
            "image/jpeg", "jpg",
            "image/jpg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif"
    );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String supabaseUrl;
    private final String bucket;
    private final String serviceKey;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.storage.bucket}") String bucket,
            @Value("${supabase.service-key:}") String serviceKey
    ) {
        this.supabaseUrl = supabaseUrl.replaceAll("/+$", "");
        this.bucket = bucket;
        this.serviceKey = serviceKey != null ? serviceKey.trim() : "";
    }

    public UploadResponse upload(MultipartFile file, String folder) {
        requireServiceKey();

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar un archivo");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo supera el límite de 5 MB");
        }

        String safeFolder = sanitizeFolder(folder);
        String contentType = file.getContentType();
        String extension = contentType != null ? MIME_TO_EXT.get(contentType.toLowerCase()) : null;
        if (extension == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo de archivo no permitido. Usa JPG, PNG, WEBP o GIF.");
        }

        String objectPath = "%s/%s/%s.%s".formatted(safeFolder, LocalDate.now(), UUID.randomUUID(), extension);
        URI uploadUri = URI.create("%s/storage/v1/object/%s/%s".formatted(supabaseUrl, bucket, objectPath));

        try {
            HttpRequest request = HttpRequest.newBuilder(uploadUri)
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .header("Content-Type", contentType)
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Supabase Storage respondió " + response.statusCode() + ": " + response.body());
            }

            String publicUrl = "%s/storage/v1/object/public/%s/%s".formatted(supabaseUrl, bucket, objectPath);
            return new UploadResponse(objectPath, publicUrl, contentType, file.getSize());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al subir el archivo a Supabase", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Subida interrumpida", e);
        }
    }

    public void delete(String path) {
        requireServiceKey();

        if (path == null || path.isBlank() || path.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruta de archivo inválida");
        }
        String cleanPath = path.replaceAll("^/+", "");
        String rootFolder = cleanPath.contains("/") ? cleanPath.substring(0, cleanPath.indexOf('/')) : "";
        if (!ALLOWED_FOLDERS.contains(rootFolder)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carpeta no permitida");
        }

        URI deleteUri = URI.create("%s/storage/v1/object/%s/%s".formatted(supabaseUrl, bucket, cleanPath));
        try {
            HttpRequest request = HttpRequest.newBuilder(deleteUri)
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "No se pudo eliminar en Supabase (" + response.statusCode() + "): " + response.body());
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al eliminar en Supabase", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Eliminación interrumpida", e);
        }
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "productos";
        }
        String normalized = folder.trim().toLowerCase();
        if (!ALLOWED_FOLDERS.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Carpeta inválida. Usa: " + String.join(", ", ALLOWED_FOLDERS));
        }
        return normalized;
    }

    private void requireServiceKey() {
        if (serviceKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Falta configurar supabase.service-key (o SUPABASE_SERVICE_KEY) en el backend.");
        }
    }
}
