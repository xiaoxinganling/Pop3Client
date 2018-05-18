import com.config.GlobalVar;
import com.mail.MailTemple;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("please input server host and server port(d for default sina mail):");
        String serverHost;
        int serverPort;
        String tmp = in.nextLine();
        if(tmp.equals("d"))
        {
            serverHost = GlobalVar.serverHost;
            serverPort = GlobalVar.POP3port;
        }
        else
        {
            serverHost = tmp;
            serverPort = Integer.valueOf(in.nextLine());
        }
        MailTemple mt = new MailTemple();
        System.out.println("connecting "+serverHost+" "+serverPort);
        System.out.println(mt.conn(serverHost,serverPort));
        while(!(mt.getSocket().isClosed()))
        {
            try {
                String result = mt.parseRequest(in.nextLine());
                if(result==null)
                {
                    break;
                }
                System.out.println(result);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
