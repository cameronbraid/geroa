package com.ettrema.mail;

import com.bradmcevoy.io.ReadingException;
import com.bradmcevoy.io.StreamToStream;
import com.bradmcevoy.io.WritingException;
import com.ettrema.mail.receive.SmtpServer;
import com.ettrema.mail.receive.SubethaSmtpServer;
import com.ettrema.mail.send.MailSender;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import junit.framework.TestCase;

/**
 * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
 */
public class StandardMessageTest extends TestCase {

    private static final String HTML_TEXT = "<H1>Hello</H1><img src=\"cid:memememe\">";

    private MailSender mailSender;
    private SmtpServer server;

    private StandardMessage standardMessage;

    @Override
    protected void setUp() throws Exception {
//        super.setUp();
////        mailSender = new MockMailSender();
//
//        MemoryMailResourceFactory resourceFactory = new MemoryMailResourceFactory();
//        resourceFactory.addMailbox(new TestMemoryMailbox(), "testuser");
//        server = new SubethaSmtpServer(resourceFactory, mailSender);
//        server.start();
    }

    @Override
    protected void tearDown() throws Exception {
//        super.tearDown();
//        server.stop();
    }





    public void test() throws MessagingException, ReadingException, WritingException, IOException, InterruptedException {
//        String file = "";
//        String from = "standardmessagetest@localhost";
//        String to = "testuser@localhost";
//
//// Create the message
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "localhost");
//        props.put("mail.smtp.port", "25");
//        Session session = Session.getInstance(props);
//
//        MimeMessage message = new MimeMessage(session);
//
//// Fill its headers
//        message.setSubject("Embedded Image");
//        message.setFrom(new InternetAddress(from));
//        message.addRecipient(MimeMessage.RecipientType.TO,
//                new InternetAddress(to));
//
//// Create your new message part
//        BodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setContent(HTML_TEXT, "text/html");
//
//// Create a related multi-part to combine the parts
//        MimeMultipart multipart = new MimeMultipart("related");
//        multipart.addBodyPart(messageBodyPart);
//
//// Create part for the image
//        messageBodyPart = new MimeBodyPart();
//
//// Fetch the image and associate to part
//        InputStream in = this.getClass().getResourceAsStream("testImage.jpg");
//        if( in == null ) fail("no test data");
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        StreamToStream.readTo(in, out);
//        ByteArrayInputStream in2 = new ByteArrayInputStream(out.toByteArray());
//        DataSource fds = new ByteArrayDataSource(in2, "image/jpeg");
//        messageBodyPart.setDataHandler(new DataHandler(fds));
//        messageBodyPart.setHeader("Content-ID", "<a1>");
//        messageBodyPart.setFileName("testImage.jpg");
//
//// Add part to multi-part
//        multipart.addBodyPart(messageBodyPart);
//
//// Associate multi-part with message
//        message.setContent(multipart);
//
//        System.out.println("sending message..");
//        Transport.send(message);
//        System.out.println("done sending message");
//
//        Thread.sleep(1000);
//
//        assertNotNull(standardMessage);
//        assertEquals(HTML_TEXT, standardMessage.getHtmlContent());
//        Collection col = standardMessage.getAttachments();
//        assertEquals(1, col.size());
//
//        System.out.println("HTML: " + standardMessage.getHtmlContent());

    }

    class TestMemoryMailbox extends MemoryMailbox {

        @Override
        public void storeMail(MimeMessage mm) {
            System.out.println("*************** storeMail");
            try {
                standardMessage = StandardMessage.parse(mm);
                System.out.println("done storing");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
