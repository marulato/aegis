package org.legion.aegis.common.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.legion.aegis.common.consts.SystemConsts;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompressUtils {

    public static byte[] compressZip(Map<String, byte[]> dataMap) throws Exception {
        if (dataMap != null && !dataMap.isEmpty()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipArchiveOutputStream zipStream = new ZipArchiveOutputStream(bos);
            Set<String> fileNames = dataMap.keySet();
            for (String fileName : fileNames) {
                File file = new File(FileNameGenerator.getUserTempPath());
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new FileNotFoundException();
                    }
                }
                byte[] data = dataMap.get(fileName);
                try (FileOutputStream outputStream = new FileOutputStream(FileNameGenerator.getUserTempPath() + fileName)) {
                    outputStream.write(data);
                }
                File archivalFile = new File(FileNameGenerator.getUserTempPath() + fileName);
                if (archivalFile.isFile()) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(archivalFile, fileName);
                    try (FileInputStream fis = new FileInputStream(archivalFile);
                         BufferedInputStream inputStream = new BufferedInputStream(fis)) {
                        zipStream.putArchiveEntry(entry);
                        zipStream.write(inputStream.readAllBytes());
                        zipStream.closeArchiveEntry();
                    }
                }
            }
            zipStream.finish();
            zipStream.close();
            return bos.toByteArray();
        }
        return new byte[0];
    }
}
