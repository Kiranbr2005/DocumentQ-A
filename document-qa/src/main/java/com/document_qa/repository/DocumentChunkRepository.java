package com.document_qa.repository;

import com.document_qa.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    @Query(value = """
    SELECT content FROM document_chunk
    WHERE content ILIKE '%' || :query || '%'
       OR to_tsvector('english', content) @@ plainto_tsquery('english', :query)
    LIMIT :k
    """, nativeQuery = true)
    List<String> findByFullTextSearch(@Param("query") String query, @Param("k") int k);

    @Query(value = """
    SELECT content FROM document_chunk
    ORDER BY id DESC
    LIMIT :k
    """, nativeQuery = true)
    List<String> findAllChunks(@Param("k") int k);

    void deleteByDocumentId(String documentId);

    List<DocumentChunk> findByDocumentId(String documentId);

    void deleteByDocumentName(String originalFilename);
}