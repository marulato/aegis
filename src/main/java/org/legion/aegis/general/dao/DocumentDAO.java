package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.general.entity.Document;
import org.legion.aegis.general.entity.EmailArchive;


@Mapper
public interface DocumentDAO {

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    Long create(Document document);

    Document getDocumentById(Long id);

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void createEmailArchive(EmailArchive archive);
}
