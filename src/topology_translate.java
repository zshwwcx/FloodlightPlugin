import java.util.*;
import java.io.*;

public class topology_translate {
	public static void translate_topology(String file_path,int[][] matrix_topology){
		try{
			String encoding="utf-8";
			File file_in=new File(file_path);
			if(file_in.isFile()&&file_in.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt=null;
				//String process_string=new String();
				while((lineTxt=bufferReader.readLine())!=null){
					lineTxt=lineTxt.substring(1, lineTxt.length()-1);
					//System.out.println(Arrays.toString(lineTxt.split("]], \\[")));
					String[] temp=lineTxt.split("]]");
					for(String t:temp){
						if(t.charAt(0)==','){
							t=t.substring(1);
						}
						
						System.out.println(t.trim());
					}
					
				}
				read.close();
			}
		}catch(Exception e){
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		int[][] a=new int[5][4];
		translate_topology("C:\\Users\\haven\\OneDrive\\科研\\SDN\\拓扑结构\\topo\\links",a);
	}
	
}
