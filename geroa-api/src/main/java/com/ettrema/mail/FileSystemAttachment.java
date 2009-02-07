package com.ettrema.mail;

import com.bradmcevoy.io.StreamToStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

/**
 *
 */
public class FileSystemAttachment implements Attachment {

    String name;
    String contentType;
    File file;

    public static FileSystemAttachment parse(BodyPart bp) {
        InputStream in = null;
        try {
            String name = bp.getFileName();
            if( name == null ) name = System.currentTimeMillis() + "";
            String ct = bp.getContentType();
            in = bp.getInputStream();
            File outFile = File.createTempFile(name, "attachment");
            FileOutputStream fout = new FileOutputStream(outFile);
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            StreamToStream.readTo(in, bout);
            bout.flush();
            fout.flush();
            Utils.close(bout);
            Utils.close(fout);
            return new FileSystemAttachment(name, ct, outFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        } finally {
            Utils.close(in);
        }
    }

    public FileSystemAttachment(String name, String contentType, File file) {
        this.name = name;
        this.contentType = contentType;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void useData(InputStreamConsumer exec) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            exec.execute(fin);
        } catch(IOException e) {
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
