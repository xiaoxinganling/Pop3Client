package com.util;


import com.mail.Mail;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import sun.misc.BASE64Decoder;

import java.io.IOException;

public class MailUtil {
    private static String charset = "GBK";
    public static Mail parseMail(String mailText) throws IOException {
        Mail res = new Mail();
        res.setContent(getContent(mailText));
        res.setReceivedFrom(getReceivedFrom(mailText));
        res.setSubject(getSubject(mailText));
        return res;
    }
    private static String getSubject(String mailText) throws IOException {
        int start = mailText.indexOf("Subject: ")+9;
        int end = start + mailText.substring(start).indexOf("\n")-1;
        if(mailText.substring(start).contains("=?"))
        {
            start = start + mailText.substring(start).indexOf("B?")+2;
            end = start + mailText.substring(start).indexOf("?=");
            byte[] source = new BASE64Decoder().decodeBuffer(mailText.substring(start,end));
            return new String(source,charset);
        }
        return mailText.substring(start,end);
    }
    private static String getReceivedFrom(String mailText) {
        int fromStart = mailText.indexOf("From: ");
        int start = mailText.substring(fromStart).indexOf("<")+fromStart+1;
        int end = mailText.substring(fromStart).indexOf(">")+fromStart;
        return mailText.substring(start,end);
    }
    private static String getContent(String mailText) throws IOException {
        //System.out.println(mailText);
        //get boundary
        int boundaryStart = mailText.indexOf("boundary=");
        if(boundaryStart==-1)
        {
            int charSetStart = mailText.indexOf("charset")+8;
            int charSetEnd = mailText.substring(charSetStart).indexOf("\n")+charSetStart;
            charset = mailText.substring(charSetStart,charSetEnd-1);
            if(charset.contains("\""))
            {
                charset = charset.substring(1,charset.length()-1);
            }
            int start  = mailText.indexOf("base64");
            start = start + "base64".length()+2;
            byte[] res = new BASE64Decoder().decodeBuffer(mailText.substring(start));
            String result =  new String(res,charset);
            if(result.contains("<br>"))
            {
                result = parseHtml(result);
            }
            return result;
        }
        int boundaryEnd = mailText.substring(boundaryStart).indexOf("\n")+boundaryStart-1;
        //tmp is boundary="=-sinamail_alt_31fb791c459f106013c274ffa7b4d729"
        String tmp = mailText.substring(boundaryStart,boundaryEnd);
        boundaryStart = tmp.indexOf("\"");
        boundaryEnd = boundaryStart +1+ tmp.substring(boundaryStart+1).indexOf("\"");
        //bound is --=-sinamail_alt_31fb791c459f106013c274ffa7b4d729
        String bound = "--"+tmp.substring(boundaryStart+1,boundaryEnd);
        int boundLen = bound.length();
        //content text
        int newStart = mailText.indexOf(bound)+boundLen+1;
        int newEnd = mailText.substring(newStart).indexOf(bound)+newStart;
        String sourceText = mailText.substring(newStart,newEnd);
        String[] lines = sourceText.split("\n");
        StringBuffer source = new StringBuffer();
        for(String s : lines)
        {
            if(s.contains("charset"))
            {
                charset = s.substring(s.indexOf("charset")+8,s.length()-1);
                if(charset.contains("\""))
                {
                    charset = charset.substring(1,charset.length()-1);
                }
            }
            else if(s.toUpperCase().contains("CONTENT"))
            {
                continue;
            }
            else
            {
                source.append(s);
                source.append("\n");
            }
        }
        //decode
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] res = decoder.decodeBuffer(source.toString());
        String result =  new String(res,charset);
        if(result.contains("<br>"))
        {
            result = parseHtml(result);
        }
        return result;
    }
    private static String parseHtml(String html) {
        if (html == null)
        {
            return "";
        }

        final Document document = Jsoup.parse(html);
        final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        final String newHtml = document.html().replaceAll("\\\\n", "\n");
        final String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        final String result = StringEscapeUtils.unescapeHtml3(plainText.trim());
        return result;
    }
}
