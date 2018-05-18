package com.mail;
import com.util.MailUtil;

import java.io.*;
import java.net.Socket;

public class MailTemple {
    private Socket s = null;

    public Socket getSocket() {
        return s;
    }

    private PrintWriter output = null;
    private BufferedReader input = null;
    public String conn(String serverHost,int serverPort)
    {
        if(s==null)
        {
            try {
                s = new Socket(serverHost,serverPort);
                output = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                return input.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "-ERR connection is already exist";
    }
    private void close() throws IOException {
        s.close();
        output.close();
        input.close();
    }
    public String parseRequest(String command) throws IOException {
        String result = null;
        if(command.contains("retr")||command.contains("list")||command.contains("top"))
        {
            StringBuffer serverReply = new StringBuffer();
            output.println(command);
            output.flush();
            serverReply.append(input.readLine());
            while(true)
            {
                String tmp = input.readLine();
                serverReply.append("\r\n");
                serverReply.append(tmp);
                if(tmp.equals("."))
                {
                    break;
                }
            }
            if(command.contains("retr")||command.contains("top"))
            {
                return MailUtil.parseMail(serverReply.toString()).toString();
            }
            return serverReply.toString();
        }
        else
        {
            output.println(command);
            output.flush();
            result = input.readLine();
            if(command.equals("quit"))
            {
                close();
            }
        }
        return result;

    }
}
