package uno.meng.cache;

import uno.meng.util.Receive;
import uno.meng.util.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class Cache{
	private static Receive receive;
	static Socket ServiceSocket = new Socket();  //与目标服务器的socket
    public Cache(Receive receive) {
        Cache.receive = receive;
    }
    //判断是否缓存过
    public static boolean isCached(Request request) {
        String filePath = hashStr(request);
        return new File(filePath).exists();
    }
    //判断是否最新
    public static boolean isNew(Request request){
    			String filePath = hashStr(request);
    			String time = new String();
    			InputStreamReader read = null;
    			try {
    				read = new InputStreamReader(new FileInputStream(filePath),"UTF-8");
    				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
    				String lineTxt = new String();
    		 		 try {
    					while((lineTxt = bufferedReader.readLine()) != null && lineTxt.length() > 6){
    						if(lineTxt.substring(0,5).equals("Date:")){
    							time = lineTxt.substring(6);
                             ServiceSocket = new Socket(request.url.getIP(),request.url.getPort());
    							String header = "If-Modified-Since: "+time;
    							OutputStream OutputOfService = ServiceSocket.getOutputStream();
                             OutputOfService.write(header.getBytes());  // 转发请求头
                             OutputOfService.flush();
                             receive.CreateHTTP(ServiceSocket.getInputStream());
                            if(new String(receive.receive).contains("HTTP/1.1 304 Not Modified")){
                            			return true;
                            }
    						}
    					  }
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			} catch (UnsupportedEncodingException e1) {
    				e1.printStackTrace();
    			} catch (FileNotFoundException e1) {
    				e1.printStackTrace();
    			}finally{
    				try {
    					read.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    			return false;
    }
    //hash函数
    public static String hashStr(Request request){ 
    		String key = request.url.getUrl();
	    int arraySize = 65535;            
	    int hashCode = 0;  
	    for(int i=0;i<key.length();i++){        //从字符串的左边开始计算  
	        int letterValue = key.charAt(i) - 10;  
	        hashCode = ((hashCode << 5) + letterValue) % arraySize;//防止编码溢出，对每步结果都进行取模运算  
	    }  
	    return "cache/"+String.valueOf(hashCode)+".txt";  
	}  
//从缓存中获取回应
    public static Receive get(Request request) {
        String filePath = hashStr(request);
        File file = new File(filePath);
        try {
          	receive.CreateHTTP(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
        }
        return receive;
    }
//将新的请求回应加入缓存
    public static void add(Request request, Receive receive) {
        String filePath = hashStr(request);
        try {
            @SuppressWarnings("resource")
			FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(receive.getReceive());
        } catch (IOException ex) {
        }
    }

}
