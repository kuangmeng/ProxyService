package uno.meng.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import uno.meng.httpheader.HttpHeader;
public class Request {
    public HttpHeader url;
    private Receive receive;
    public Request(HttpHeader url, Receive receive) {
        this.url = url;
        this.receive = receive;
    }
    //通过请求获取回应
    public Receive CreateReceive() throws IOException {
        Socket ServiceSocket = null;
		try {
			ServiceSocket = new Socket(url.getHost(), url.getPort());
		} catch (UnknownHostException e1) {
		} catch (IOException e1){
		}
        DataOutputStream outputStream = new DataOutputStream(ServiceSocket.getOutputStream());
        try {
            outputStream.writeBytes(url.getHeadersAsString());
            outputStream.flush();
        } catch (IOException ex) {
        }
        receive.CreateHTTP(ServiceSocket.getInputStream());
        try {
			ServiceSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		};
        return receive;
    }
}
