import java.util.*;
import java.io.*;

/*
读取link以及clusters文件的信息，将原来的拓扑结构转化为外挂程序的拓扑邻接链表结构，以便我们执行外部的TE算法。
*/


/*邻接节点链表信息*/
class adj_info{
		public String adj_id;
		public int adj_bandwidth;
		public int adj_delay;
		
		public adj_info(String id,int bw,int dl){
			this.adj_id=id;
			this.adj_bandwidth=bw;
			this.adj_delay=dl;
		}
		
}


/*图中的每个节点*/
class Node{
		public String ID;
		public ArrayList<adj_info> adjcent_list=new ArrayList<adj_info>();//节点的邻接链表
		
		public Node(String id){//初始化节点信息
			this.ID=id;	
		}
		
		public void add_adj_node(adj_info id){//为节点天界邻接节点信息
			this.adjcent_list.add(id);
		}
		
		public int has_link_to(String dst_node){
			for(adj_info node_temp :adjcent_list){
				if(node_temp.adj_id.equals(dst_node)){
					return adjcent_list.indexOf(node_temp);
				}
			}
			return 0;
		}
		
		
}

public class Graph {
	//private final int V;//图的节点数目
	//private int E;//图的边数目
	ArrayList<Node> nodelist=new ArrayList<Node>();//存储图的所有节点信息
	
	
	public Graph(String link_file_path,String clusters_file_path){//初始化图，想通过link和clusters两个文件来进行图的初始化，函数参数用文件的String路径来表示，便于后期修改。
		try{
			String encoding="utf-8";
			File file_in1=new File(clusters_file_path);
			if(file_in1.isFile()&&file_in1.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in1),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt=null;
				while((lineTxt=bufferReader.readLine())!=null){				
					addNode(new Node((String)lineTxt));
					}
				read.close();
			}
			
			File file_in2=new File(link_file_path);
			if(file_in2.isFile()&&file_in2.exists()){
				InputStreamReader read2=new InputStreamReader(new FileInputStream(file_in2),encoding);
				BufferedReader bufferReader=new BufferedReader(read2);
				String lineTxt=null;
				while((lineTxt=bufferReader.readLine())!=null){
					String[] info_temp=lineTxt.split(" ");

					String initial_id=info_temp[0];

					String adj_id=info_temp[1];
					
					int adj_delay=Integer.parseInt(info_temp[2].substring(2),16);

					int adj_bandwidth=Integer.parseInt(info_temp[3], 10);
					
					adj_info adj_new=new adj_info(adj_id,adj_bandwidth,adj_delay);
					
					for(Node tmp:this.nodelist){
						if(tmp.ID.equals(initial_id)){
							int index=nodelist.indexOf(tmp);
							nodelist.get(index).add_adj_node(adj_new);
						}
					}
					
					}
				read2.close();
			}	
		}catch(Exception e){
			System.out.println("Error:读取文件过程中出现错误！");
			e.printStackTrace();
		}
	}
	
	/*
	public int getV(){//获取节点数目
		return V;
	}
	
	public int getE(){//获取边的数目
		return E;
	}
	*/
	
	public void addNode(Node temp){//向图中添加节点
		this.nodelist.add(temp);
	}
	
	public void get_shortest_path(String src_point,String dst_point){//获取从源节点到目的节点的最短最优路径，这里的返回类型可以讨论一下，后续需要用于流表规则的下发
		
	} 
	
	public void addEdge(String src_point,String dst_point,int delay,int bandwidth){//添加从src到dst的链路
		adj_info adj_to_add=new adj_info(dst_point,bandwidth,delay);
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_to_add);
			}
		}
	}
	
	public void addEdge(String src_point,adj_info adj_info_to_add){//addEdge方法重载
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_info_to_add);
			}
		}
	}
	
	
	//删除link，用于次短路径的计算
	public void deleteEdge(String src_point,String dst_point){
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=tmp.has_link_to(dst_point);
				nodelist.remove(index);
			}
		}
	}
	
	public static void main(String[] args){
		Graph g1=new Graph("C:\\Users\\haven\\OneDrive\\科研\\SDN\\拓扑结构\\links","C:\\Users\\haven\\OneDrive\\科研\\SDN\\拓扑结构\\clusters");
		for(Node node_print:g1.nodelist){
			System.out.println("Node id:"+node_print.ID+"  "+"Adjcant_Node_Number:"+node_print.adjcent_list.size()+"\n");
		}
	}
	
}
