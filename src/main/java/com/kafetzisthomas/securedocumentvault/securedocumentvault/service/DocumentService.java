package com.kafetzisthomas.securedocumentvault.securedocumentvault.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class DocumentService {

    private final GridFsTemplate gridFsTemplate;

    public DocumentService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public ObjectId addDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        // sanitize filename before storing
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        DBObject metaData = new BasicDBObject();
        metaData.put("type", "file");
        metaData.put("filename", filename);

        return gridFsTemplate.store(file.getInputStream(), filename, file.getContentType(), metaData);
    }

}
