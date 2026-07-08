package com.derocode.rag.repository;

import com.derocode.rag.entities.PointMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PointMetaDataRepository extends JpaRepository<PointMetaData, Long> {

    Optional<PointMetaData> findByFileNameAndFilePath(String fileName, String filePath);

}
