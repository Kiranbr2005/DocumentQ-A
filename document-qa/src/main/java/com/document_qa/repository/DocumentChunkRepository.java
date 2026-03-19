package com.document_qa.repository;

import com.document_qa.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    // FTS scoped to session
    @Query(value = """
        SELECT content FROM document_chunk
        WHERE session_id = :sessionId
          AND (content ILIKE '%' || :query || '%'
           OR to_tsvector('english', content)
              @@ plainto_tsquery('english', :query))
        ORDER BY ts_rank(
            to_tsvector('english', content),
            plainto_tsquery('english', :query)
        ) DESC
        LIMIT :k
        """, nativeQuery = true)
    List<String> findBySessionAndQuery(
            @Param("sessionId") String sessionId,
            @Param("query") String query,
            @Param("k") int k);

    // Fallback: latest chunks for this session
    @Query(value = """
        SELECT content FROM document_chunk
        WHERE session_id = :sessionId
        ORDER BY id DESC
        LIMIT :k
        """, nativeQuery = true)
    List<String> findLatestBySession(
            @Param("sessionId") String sessionId,
            @Param("k") int k);

    // Delete by documentName + sessionId
    @Modifying
    @Transactional
    void deleteByDocumentNameAndSessionId(
            String documentName, String sessionId);

    // Delete all docs for a session
    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);

    // List documents for a session
    @Query(value = """
        SELECT DISTINCT document_id, document_name,
               COUNT(*) as chunk_count,
               MIN(created_at) as uploaded_at
        FROM document_chunk
        WHERE session_id = :sessionId
        GROUP BY document_id, document_name
        ORDER BY uploaded_at DESC
        """, nativeQuery = true)
    List<Object[]> findDocumentsBySession(
            @Param("sessionId") String sessionId);

    // Search within specific document + session
    @Query(value = """
    SELECT content FROM document_chunk
    WHERE document_id = :documentId
      AND session_id = :sessionId
      AND (content ILIKE '%' || :query || '%'
       OR to_tsvector('english', content)
          @@ plainto_tsquery('english', :query))
    ORDER BY id ASC
    LIMIT :k
    """, nativeQuery = true)
    List<String> findByDocumentAndSession(
            @Param("documentId") String documentId,
            @Param("sessionId") String sessionId,
            @Param("query") String query,
            @Param("k") int k);

    // Fallback within specific document
    @Query(value = """
    SELECT content FROM document_chunk
    WHERE document_id = :documentId
      AND session_id = :sessionId
    ORDER BY id ASC
    LIMIT :k
    """, nativeQuery = true)
    List<String> findLatestByDocument(
            @Param("documentId") String documentId,
            @Param("sessionId") String sessionId,
            @Param("k") int k);
}
