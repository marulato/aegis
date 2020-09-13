package org.legion.aegis.common.webmvc;

import org.legion.aegis.common.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NetworkFileTransfer {

    public static void upload(String path, MultipartFile file, String fileName) throws Exception {
        if (file != null && !file.isEmpty() && !StringUtils.isEmpty(path)) {
            File dest = new File(new File(path).getAbsolutePath()+ "/" + fileName);
            file.transferTo(dest);
        }
    }

    public static void download(String from, String fileName, HttpServletResponse response) throws Exception {
        if (!StringUtils.isEmpty(from) && response != null) {
            BufferedInputStream bufferedIn = null;
            OutputStream output = null;
            try{
                File file = new File(from);
                if (file.exists()) {
                    response.addHeader("content-type", "application/octet-stream");
                    response.addHeader("content-length", String.valueOf(file.length()));
                    response.setContentType("application/octet-stream");
                    response.addHeader("Content-Disposition", "attachment;filename=" +
                            URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
                    bufferedIn = new BufferedInputStream(new FileInputStream(file));
                    output = response.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = bufferedIn.read(buffer)) != -1) {
                        output.write(buffer, 0 ,len);
                    }
                }
            } finally {
                if (bufferedIn != null)
                    bufferedIn.close();
                if (output != null)
                    output.close();
            }

        }

    }

    public static void download(byte[] data, String fileName, HttpServletResponse response) throws Exception {
        if (response != null && data != null && !StringUtils.isEmpty(fileName)) {
            OutputStream output = null;
            try{
                response.addHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                output = response.getOutputStream();
                output.write(data);
            } finally {
                if (output != null) {
                    output.close();
                }
            }

        }
    }
}
