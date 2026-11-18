package com.shop.carrito.controller;

import com.shop.carrito.dto.UploadResponse;
import com.shop.carrito.service.SupabaseStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final SupabaseStorageService storageService;

    public StorageController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "productos") String folder
    ) {
        return storageService.upload(file, folder);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam String path) {
        storageService.delete(path);
    }
}
