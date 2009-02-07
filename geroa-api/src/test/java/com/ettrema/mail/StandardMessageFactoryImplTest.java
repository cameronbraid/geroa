/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ettrema.mail;

import com.bradmcevoy.io.ReadingException;
import com.bradmcevoy.io.StreamToStream;
import com.bradmcevoy.io.WritingException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class StandardMessageFactoryImplTest extends TestCase {

    StandardMessageFactoryImpl factory;

    public StandardMessageFactoryImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        factory = new StandardMessageFactoryImpl();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimpleText() throws MessagingException {
        InputStream in = this.getClass().getResourceAsStream("simple-text.smtp");
        assertNotNull(in);
        MimeMessage mm = new MimeMessage(null, in);
        StandardMessage sm = factory.toStandardMessage(mm);
        assertEquals("simple message", sm.getSubject());
        assertEquals("text content", sm.getText());
    }

    public void testSimpleHtml() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("simple-html.smtp");
        assertNotNull(in);
        MimeMessage mm = new MimeMessage(null, in);
        StandardMessage sm = factory.toStandardMessage(mm);
        assertEquals("html message", sm.getSubject());
        assertEquals("html content", sm.getText());
        System.out.println(sm.getHtml());
        assertTrue(sm.getHtml().contains("<STRONG>content</STRONG>"));
    }

    public void testForwardedWithAttachment() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("forward-with-attach.smtp");
        assertNotNull(in);
        MimeMessage mm = new MimeMessage(null, in);
        StandardMessage sm = factory.toStandardMessage(mm);
        assertEquals("Fw: test4", sm.getSubject());
        System.out.println("___");
        System.out.println(sm.getText());
        System.out.println("----------");
        System.out.println(sm.getHtml());
        System.out.println("-----------");
        assertEquals(1, sm.getAttachedMessages().size());
//        System.out.println("sub messages: " + sm.getAttachedMessages().size());
//        for( StandardMessage smChild : sm.getAttachedMessages() ) {
//            System.out.println("::html: " + smChild.getHtml());
//            System.out.println("::text: " + smChild.getText());
//            System.out.println("..");
//        }
//        System.out.println("-----------");
        System.out.println("binary attachments");
        for( Attachment att : sm.getAttachments() ) {
            System.out.println( att.getName() + " - " + att.size() );
            att.useData(new InputStreamConsumer() {

                public void execute(InputStream in) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    try {
                        StreamToStream.readTo(in, bout);
                    } catch (ReadingException ex) {
                        Logger.getLogger(StandardMessageFactoryImplTest.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WritingException ex) {
                        Logger.getLogger(StandardMessageFactoryImplTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //System.out.println(bout.toString());
                }
            });
        }
    }
}
