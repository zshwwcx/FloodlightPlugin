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
		
		public adj_info(){
			this.adj_id="ff:ff:ff:ff:ff:ff:ff:ff";
			this.adj_bandwidth=99999;
			this.adj_delay=99999;
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

class Link{
	public String start_switch;
	public String end_switch;
	public int delay;
	
	public Link(String start_id,String end_id,int link_delay){//初始化Link信息
		this.start_switch=start_id;
		this.end_switch=end_id;
		this.delay=link_delay;
	}
	
	public Link(){
		this.start_switch="";
		this.end_switch="";
		this.delay=0;
	}
	
	public String getLinkStart(){//获取当前Link的前节点，因为每条link都是有序的，从某个节点到某个节点，Link的开始就是前节点，后面的即为后节点
		return this.start_switch;
	}
	
	public String getLinkEnd(){//获取当前Link的后节点
		return this.end_switch;
	}
	
	public int getLinkDelay(){
		return this.delay;
	}
	
	public void setStartSwitch(String str){//设置前节点的id
		this.start_switch=str;
	}
	
	public void setEndSwitch(String str){//设置后节点的id
		this.end_switch=str;
	}
	
	public void setLinkDelay(int delay_add){//设置Link的delay
		this.delay=delay_add;
	}
	
}

class Flow_request{
	private String src_id;
	private String dst_id;
	int bandwidth_request;
	private int delay_request;
	private int priority;
	
	public Flow_request(String src,String dst,int bandwidth,int delay,int priority){
		this.src_id=src;
		this.dst_id=dst;
		this.bandwidth_request=bandwidth;
		
	}
	
	public Flow_request(){
		
	}
	
	public void setSrcId(String src){
		this.src_id=src;
	}
	
	public void setDstId(String dst){
		this.dst_id=dst;
	}
	
	public void setBandwidthRequest(int bd){
		this.bandwidth_request=bd;
	}
	
	public void setDelayRequest(int dl){
		this.delay_request=dl;
	}
	
	public void setPriority(int pr){
		this.priority=pr;
	}
	
	public String getSrcId(){
		return this.src_id;
	}
	
	public String getDstId(){
		return this.dst_id;
	}
	
	public int getBandwidthRequest(){
		return this.bandwidth_request;
	}
	
	public int getDelayRequest(){
		return this.delay_request;
	}
	
	public int getPriority(){
		return this.priority;
	}
	
}


public class Graph {
	ArrayList<Node> nodelist=new ArrayList<Node>();//存储图的所有节点信息
	ArrayList<Link> linklist=new ArrayList<Link>();//存储图的所有link信息

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
					
					this.linklist.add(new Link(info_temp[0],info_temp[1],Integer.parseInt(info_temp[2],16)));
					
					}
				read2.close();
			}	
		}catch(Exception e){
			System.out.println("Error:读取文件过程中出现错误！");
			e.printStackTrace();
		}
	}
	
	public Node getNode(String node_id){//通过节点名称查找节点是否在图内，如果在，返回该节点，否则返回null.
		for(Node node:this.nodelist){
			if(node.ID.equals(node_id))
				return node;
		}
		return null;
	}
	
	public Link getLink(String start_switch,String end_switch){//根据两个节点获取Link
		for(Link tmp:this.linklist){
			if(tmp.getLinkStart().equals(start_switch)&&tmp.getLinkEnd().equals(end_switch)){
				return tmp;
			}
		}
		return null;
	}
	
	public void addNode(Node temp){//向图中添加节点
		this.nodelist.add(temp);
	}
	
	public int getDirectDistance(String src,String dst){//获取两个由Link直接相连的节点之间的距离,如果两个节点没有直接link相连，返回的距离为-1
		int distance=-1;
		if(getNode(src)!=null){
			for(adj_info temp:getNode(src).adjcent_list){
				if(temp.adj_id.equals(dst)){
					distance=temp.adj_delay;
				}
			}
		}
		return distance;
	}
	
	public String getId(int id_num){//通过int类型的数组下标来查找节点的String类型Id
		return nodelist.get(id_num).ID;
	}
	
	public int getNum(String id){//通过节点的String类型Id来查找邻接矩阵的int类型下标
		int index=-1;
		
		for(Node temp:nodelist){
			if(temp.ID.equals(id)){
				index=nodelist.indexOf(temp);
				return index;
			}
		}
		return index;
	}
	
	
	public int[][] constructDelayAdjMatrix(){//构造图的邻接矩阵，通过Map<String id,int id_num>来将数组下标和节点的ID相互映射
		int[][] matrix=new int[nodelist.size()][nodelist.size()];
		int max_distance=100000;
		
		for(int m=0;m<matrix.length;m++){//初始化邻接矩阵，-1表示不可达
			for(int n=0;n<matrix[m].length;n++){
				matrix[m][n]=max_distance;
			}
			matrix[m][m]=0;//自身到达自身的距离设置为0
		}
		
		for(Node node:nodelist){
			int start=nodelist.indexOf(node);
			int end=-1;
			for(adj_info info:node.adjcent_list){
				end=nodelist.indexOf(getNode(info.adj_id));
				matrix[start][end]=info.adj_delay;
			}
		}
		return matrix;
	}
	
	
	/*
	public int[][] constructBandwidthMatrix(){//构造图的带宽矩阵，用于QoS以及TE算法，还没有写好
		int[][] bandwidth_matrix=new int[nodelist.size()][nodelist.size()];
			
		
		return bandwidth_matrix;
	}
	*/
	
	public ArrayList<Integer> Dijkstra_prototype(int start){//迪杰斯特拉算法原型，输入参数为起点在nodelist中的index，直接根据邻接矩阵计算起点到图中所有节点的最短路径。
		int[][] mat=this.constructDelayAdjMatrix();
		ArrayList<Integer> output=new ArrayList<Integer>();
		
		int min=100000;
		int[] dis=new int[mat.length];
		int i=0,j=0,u=0;
		int[] v=new int[mat.length];
		
		for(i=0;i<mat.length;i++){
			dis[i]=mat[start][i];
			v[i]=0;
		}
		v[start]=1;
		
		output.add(start);
		
		for(i=1;i<mat.length;i++){
			min=100000;
			for(j=0;j<mat.length;j++){
				if((v[j]==0)&&(dis[j]<min)){
					min=dis[j];
					u=j;
				}
			}
			v[u]=1;
			if(!output.contains(u)){
				output.add(u);
			}
			for(j=0;j<mat.length;j++){
				int tmp = (mat[u][j]==100000 ? 100000 : (min + mat[u][j]));
				if(v[j]==0&&dis[j]>tmp){
					dis[j]=dis[u]+mat[u][j];
				}
			}
		}
		return output;
	}
	
	public void printPath(String src,String dst){//打印从src岛dst节点所经过的最短路径中的所有节点
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		ArrayList<Integer> result=Dijkstra_prototype(start);
		result.add(getNum(dst));
		if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=end_index+1;i<result.size();i++){
				result.remove(i);
			}
			System.out.println("The path from "+"\""+src+"\""+" to \""+dst+"\""+" is:");
			for(int i=0;i<result.size();i++){
				System.out.print(getId(result.get(i))+"==>");	
			}
			System.out.print("end");
		} 
	}
	
	public ArrayList<Integer> getIndexPath(String src,String dst){//得到从src到dst最短路径所需要经过的所有节点的index
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		ArrayList<Integer> result=Dijkstra_prototype(start);
		result.add(getNum(dst));
		if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=end_index+1;i<result.size();i++){
				result.remove(i);
			}
			return result;
		}
		else
			return null;
	}
	
	public ArrayList<String> getStringPath(String src,String dst){//得到从src到dst最短路径所需要经过的所有节点的String
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		ArrayList<Integer> result=Dijkstra_prototype(start);
		ArrayList<String> StringResult=new ArrayList<String>();
		result.add(getNum(dst));
		if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=0;i<=end_index;i++){
				StringResult.add(getId(result.get(i)));
			}
			return StringResult;
		}
		else
			return null;
	}
	
	
	public void DijkstarSP(Node src,Node dst){//计算由src节点到dst节点的最短距离
		
	}
	
	public void DijkstarSP(String src,String dst){//重载最短路径函数，可以直接由节点的名称来计算最短路径
		
	}
	
	public int getDistance(String src,String dst){//获取从src到达dst的路径的最短延迟
		int distance=0;
		ArrayList<String> path=this.getStringPath(src, dst);
		for(int i=0;i<path.size()-1;i++){
			distance+=this.getDirectDistance(path.get(i), path.get(i+1));
		}
		return distance;
	}
	
	
	public void allocateBandwidth(String src,String dst,int bandwidth_request,int delay_request){//带宽分配函数，以带宽和延迟作为分配标准
		
		
		this.getStringPath(src, dst);
		
	}
	
	public void TE(String FlowRequestFilePath){//File的格式为每条流的请求为一行，分别为（1）源地址（2）目的地址（3）流量请求（4）带宽要求（5）延迟要求（6）优先级,变量之间用空格符分隔开
		
		
		
		try{
			String encoding="utf-8";
			File file_in=new File(FlowRequestFilePath);
			if(file_in.isFile()&&file_in.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt=null;
				while((lineTxt=bufferReader.readLine())!=null){				
					addNode(new Node((String)lineTxt));
					}
				read.close();
			}
		}catch(Exception e){
			System.out.println("Error:读取数据流请求文件出错！");
			e.printStackTrace();
		}
	}
	
	/*
	public ArrayList<String> get_shortest_path(String src_point,String dst_point){//获取从源节点到目的节点的最短最优路径(按照延迟来计算)，这里的返回类型可以讨论一下，后续需要用于流表规则的下发
		Node src_node=getNode(src_point);
		ArrayList<String> shortest_path=new ArrayList<String>();//用于函数输出，src_point到达dst_point所途径的所有节点
		ArrayList<adj_info> copy_adjcent_list=new ArrayList<adj_info>();//复制src_node的邻接链表信息，用于存储当前已知的src可达节点以及相关信息
		copy_adjcent_list=(ArrayList<adj_info>)src_node.adjcent_list.clone();
		ArrayList<Node> copy_nodelist=new ArrayList<Node>();//复制Graph的节点列表，用于Dijkstra算法的全集S，将S中离src最近的节点进行松弛（relax）
		copy_nodelist=(ArrayList<Node>)this.nodelist.clone();
		Node temp=new Node("Null point");
		

		int min_distance=9999;//初始化最小距离
		
		if((src_node!=null)&&copy_nodelist.contains(src_node)){
			copy_nodelist.remove(src_node);
			while(!copy_nodelist.isEmpty()){
				adj_info temp_info=new adj_info();
				for(adj_info info:src_node.adjcent_list){
					if(info.adj_delay<min_distance){
						min_distance=info.adj_delay;
						temp=getNode(info.adj_id);
						temp_info.adj_delay=info.adj_delay+getDirectDistance(src_point,temp_info.adj_id);//延迟为所经过的link所有的delay之和
						temp_info.adj_bandwidth=info.adj_bandwidth;//带宽应该为所经过的link所有的带宽最小值，但此处要求的是最短路径，暂时不做处理
					}	
				}
				if((copy_nodelist.contains(temp))&&(copy_nodelist.remove(temp))){
					copy_adjcent_list.add(temp_info);
					shortest_path.add(temp_info.adj_id);
					System.out.println(temp_info.adj_id+"\n");
				}
			}
		}
		
		return shortest_path;
	} 
	*/
	
	
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
	public void RemoveLink(String src_point,String dst_point){
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
		if(g1.getNode("00:00:00:00:00:00:00:0a")!=null){//测试getNode函数的性能
			System.out.println("Find the node.");
		}
		else
			System.out.println("Didn't find the node.\n\n");
		
		
		int[][] output_matrix=g1.constructDelayAdjMatrix();//测试邻接矩阵的建立情况，成功
		for(int m=0;m<output_matrix.length;m++){
			System.out.print("[");
			for(int n=0;n<output_matrix[m].length;n++){
				System.out.print(output_matrix[m][n]+" ");
			}
			System.out.println("]\n");
		}
		
		ArrayList<Integer> out=g1.Dijkstra_prototype(3);//测试单域情况中，路由算法的有效性
		for(Integer i:out){
			System.out.print(i+"->");
			
		}
		System.out.println("End");
		
		g1.printPath("00:00:00:00:00:00:00:03", "00:00:da:c5:01:a3:44:48");
		
		System.out.println("");
		
		
		int t=g1.getDistance("00:00:00:00:00:00:00:03", "00:00:da:c5:01:a3:44:48");
		System.out.println("Distance from source to destination is:"+t);

	}
	
	
}
