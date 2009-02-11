package com.ettrema.mail;

import com.bradmcevoy.io.StreamToStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

/**
 *
 */
public class FileSystemAttachment implements Attachment, Serializable {

    private static final long serialVersionUID = 1L;
    String name;
    String contentType;
    String disposition;
    String contentId;
    File file;

    public static FileSystemAttachment parse(BodyPart bp) {
        InputStream in = null;
        try {
            String name = bp.getFileName();
            if (name == null) {
                name = System.currentTimeMillis() + "";
            }
            String ct = bp.getContentType();
            String[] contentIdArr = bp.getHeader("Content-ID");
            String contentId = null;
            if (contentIdArr != null && contentIdArr.length > 0) {
                contentId = contentIdArr[0];
            }
            in = bp.getInputStream();
            File outFile = File.createTempFile(name, "attachment");
            FileOutputStream fout = new FileOutputStream(outFile);
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            StreamToStream.readTo(in, bout);
            bout.flush();
            fout.flush();
            Utils.close(bout);
            Utils.close(fout);
            return new FileSystemAttachment(name, ct, outFile, contentId);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        } finally {
            Utils.close(in);
        }
    }

    public FileSystemAttachment(String name, String contentType, File file, String contentId) {
        this.name = name;
        this.contentType = contentType;
        this.file = file;
        this.contentId = contentId;
    }

    public String getName() {
        return name;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDisposition() {
        return disposition;
    }

    public InputStream getInputStream() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            return fin;
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
    }




    public void useData(InputStreamConsumer exec) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            exec.execute(fin);
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        } finally {
            Utils.close(fin);
        }
    }

    public int size() {
        long l = file.length();
        return (int) l;
    }
}
