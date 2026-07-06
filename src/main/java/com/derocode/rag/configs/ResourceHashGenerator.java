package com.derocode.rag.configs;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class ResourceHashGenerator {

    public static String generate(Resource resource){
        try (InputStream is = resource.getInputStream()){
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] hash = md.digest();
            return HexFormat.of().formatHex(hash);

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
