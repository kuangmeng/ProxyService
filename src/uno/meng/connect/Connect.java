package uno.meng.connect;

import uno.meng.cache.Cache;
import uno.meng.filter.Filter;
import uno.meng.httpheader.HttpHeader;
import uno.meng.swing.ProxyServiceInfo;
import uno.meng.util.Receive;
import uno.meng.util.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connect extends Thread {
    private Socket client;
    public Connect(Socket client) {
        this.client = client;
    }
    @Override
    public void run() {
        HttpHeader url = new HttpHeader();
        Receive rb = new Receive();
        Request request = new Request(url, rb);
        try {
			request.url.CreateRequest(client.getInputStream());
		} catch (IOException e1) {
		}
        InetAddress addr = client.getInetAddress();
        if(Filter.NonUserShield(addr.getHostAddress())){
            //判断是否过滤该url
            if (Filter.NonUrlShield(url.getUrl()) && url.getUrl().length() < 100){
                //判断是否钓鱼
                if(Filter.NonFish(url.getUrl())){
                			System.out.println("收到链接："+request.url.getUrl());
                			ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\n收到链接："+request.url.getUrl());
				            Receive receive = null;
							try {
								receive = CacheReceive(request);
							} catch (IOException e) {
							}
				            try {
				                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
				                outputStream.write(receive.getReceive());
				            } catch (IOException e1) {
				            }
                }else{
                		url.setPath(Filter.getFinalUrl());
                		request = new Request(url, rb);
            			System.out.println("*********该链接已被钓鱼到："+request.url.getUrl());
            			ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\n*********该链接已被钓鱼到："+request.url.getUrl());
            			OutputStream OutputOfClient;
						try {
							OutputOfClient = client.getOutputStream();
							OutputOfClient.write(Filter.getRequest().getBytes());
		                     OutputOfClient.write(Filter.getFirst().getBytes());
		                     OutputOfClient.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
                }
            }else{
            		System.out.println("该URL："+url.getUrl()+"禁止访问！");
        			ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\n该URL："+url.getUrl()+"禁止访问！");
            }
        }else{
        		System.out.println("该IP："+addr.getHostAddress()+"被屏蔽！");
    			ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\n该IP："+addr.getHostAddress()+"被屏蔽！");
        }
        try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private Receive CacheReceive(Request request) throws IOException {
        Receive receive = null;
        if (Cache.isCached(request)){
        	  if(Cache.isNew(request)){
      		ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\n从Cache中取出："+request.url.getUrl());
        		  receive = Cache.get(request);
        		  System.out.println("从Cache中取出："+request.url.getUrl());
        	  }else{
        		  receive = request.CreateReceive();
               Cache.add(request, receive);
        		  System.out.println("Cache中存在，但不是最新，已经获取最新："+request.url.getUrl());
            	  ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\nCache中存在，但不是最新，已经获取最新："+request.url.getUrl());
        	  }
        } else {
            receive = request.CreateReceive();
            Cache.add(request, receive);
            System.out.println("Cache中没有此记录！已生成Cache文件："+Cache.hashStr(request));
    		    ProxyServiceInfo.text.setText(ProxyServiceInfo.text.getText()+"\r\nCache中没有此记录！已生成Cache文件："+Cache.hashStr(request));
        }
        return receive;
    }
    public void stopTask(){
        try {
            if(client!=null){
                client.close();
            }
        } catch (IOException e) {
        }
    }
}
