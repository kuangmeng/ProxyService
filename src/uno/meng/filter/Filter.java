package uno.meng.filter;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class Filter {
	private static List<String> ShieldList = new ArrayList<String>();
	private static List<String> FishList = new ArrayList<String>();
	private static List<String> UserList = new ArrayList<String>();
	private static String finalUrl = "http://www.meng.uno";
	private static String Request = "HTTP/1.1 302 Moved Temporarily\r\n";
	private String path = "/Users/kuangmeng/Documents/Eclipse/ProxyService/src/uno/meng/filter/filter.json";
	public Filter(){
		InitData(Filter.ShieldList,Filter.FishList,Filter.UserList);
	}
	//加载过滤数据
	public void InitData(List<String> shieldList, List<String> fishList, List<String> UserList){
		File file = new File(path);
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e){       
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        try {
			JSONObject jsonObject = new JSONObject(buffer.toString());
			JSONArray likes=jsonObject.getJSONArray("hosts"); //得到likes数组
			for(int i = 0;i< likes.length();i++){
				String str = likes.getString(i);
				shieldList.add(str);
			}
			likes=jsonObject.getJSONArray("fishs"); //得到likes数组
			for(int i = 0;i< likes.length();i++){
				String str = likes.getString(i);
				fishList.add(str);
			}
			likes=jsonObject.getJSONArray("users"); //得到likes数组
			for(int i = 0;i< likes.length();i++){
				String str = likes.getString(i);
				UserList.add(str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//判断URL是否过滤
	public static boolean NonUrlShield(String url){
        for (int i = 0; i < ShieldList.size(); i++){
            if(url.contains(ShieldList.get(i)) || url.matches( ShieldList.get(i))){
                return false;
            }
        }
        return true;
    }
    //钓鱼响应报文首部
    public static String getFirst(){
        String first = new String();
        first = "Location: "+getFinalUrl()+"\r\n\r\n";
        return first;
    }
    //判断是否钓鱼
    public static boolean NonFish(String url){
        for(int i = 0; i < FishList.size(); i++){
            if(url.contains(FishList.get(i)) || url.matches(FishList.get(i))){
                return false;
            }
        }
        return true;
    }
    //判断是否用户过滤
    public static boolean NonUserShield(String ip){
    	try {
			InetAddress address=InetAddress.getLocalHost();
			 for (int i = 0; i < UserList.size(); i++){
		            if(ip.contains(UserList.get(i)) || address.equals(UserList.get(i))){
		                return false;
		            }
		        }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        return true;
    }
	public static String getFinalUrl() {
		return finalUrl;
	}
	public static String getRequest() {
		return Request;
	}
}
