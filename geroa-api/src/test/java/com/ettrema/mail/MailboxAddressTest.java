/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ettrema.mail;

import junit.framework.TestCase;

/**
 *
 * @author brad
 */
public class MailboxAddressTest extends TestCase {
    
    public MailboxAddressTest(String testName) {
        super(testName);
    }

    public void testParse_NoPersonal() {
        MailboxAddress ma = MailboxAddress.parse("abc@def.com");
        assertEquals("abc", ma.user);
        assertEquals("def.com", ma.domain);
        assertNull(ma.personal);
    }

    // todo
//    public void testParse_WithPersonal() {
//        MailboxAddress ma = MailboxAddress.parse("\"joker\" <abc@def.com>");
//        assertEquals("abc", ma.user);
//        assertEquals("def.com", ma.domain);
//        assertEquals("joker", ma.personal);
//        assertNull(ma.personal);
//    }

    public void testToString() {
        MailboxAddress ma;
        ma = new MailboxAddress("abc", "def.com");
        assertEquals("abc@def.com", ma.toString());

        ma = new MailboxAddress("abc", "def.com", "joker");
        assertEquals("\"joker\" <abc@def.com>", ma.toString());

    }

}
