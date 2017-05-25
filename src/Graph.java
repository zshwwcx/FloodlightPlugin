import java.util.*;
import java.io.*;

/*
读取link以及clusters文件的信息，将原来的拓扑结构转化为外挂程序的拓扑邻接链表结构，以便我们执行外部的TE算法。
*/

/*目前亟待解决的问题
* （1）读取FLow Request文件的过程中，如果遇到有源节点、目的节点在clusters文件中的情况下，程序运行会报错，需要添加异常捕捉信息
* （2）
* （3）
* */


public class Graph {
	ArrayList<Node> nodelist=new ArrayList<>();//存储图的所有节点信息
	ArrayList<Link> linklist=new ArrayList<>();//存储图的所有link信息
	ArrayList<Flow_request> flowRequestList=new ArrayList<>();//存储需要进行TE的所有流需求

    public static String GraphNodeFile="E:\\代码\\java\\FloodlightPlugin\\src\\clusters_new_topo";
    public static String GraphLinkFile="E:\\代码\\java\\FloodlightPlugin\\src\\links_new_topo";
    public static String GraphFlowReuqestListFile="E:\\代码\\java\\FloodlightPlugin\\src\\NewFlowRequest.txt";

	public Graph(String link_file_path,String clusters_file_path){//初始化图，想通过link和clusters两个文件来进行图的初始化，函数参数用文件的String路径来表示，便于后期修改。
		try{
			String encoding="utf-8";
			File file_in1=new File(clusters_file_path);
			if(file_in1.isFile()&&file_in1.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in1),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					addNode(new Node(lineTxt));
					}
				read.close();
			}

			File file_in2=new File(link_file_path);
			if(file_in2.isFile()&&file_in2.exists()){
				InputStreamReader read2=new InputStreamReader(new FileInputStream(file_in2),encoding);
				BufferedReader bufferReader=new BufferedReader(read2);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					String[] info_temp=lineTxt.split(" ");

					String initial_id=info_temp[0];

					String adj_id=info_temp[1];

					int adj_delay=Integer.parseInt(info_temp[2].substring(2),16);

					int adj_bandwidth=Integer.parseInt(info_temp[3], 10);

					AdjInfo adj_new=new AdjInfo(adj_id,adj_bandwidth,adj_delay);

					for(Node tmp:this.nodelist){
						if(tmp.ID.equals(initial_id)){
							int index=nodelist.indexOf(tmp);
							nodelist.get(index).add_adj_node(adj_new);
						}
					}

					this.linklist.add(new Link(initial_id,adj_id,adj_delay,adj_bandwidth));

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
			for(AdjInfo temp:getNode(src).adjcent_list){
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
			int end;
			for(AdjInfo info:node.adjcent_list){
				end=nodelist.indexOf(getNode(info.adj_id));
				matrix[start][end]=info.adj_delay;
			}
		}
		return matrix;
	}

	public ArrayList<Integer> dijkstra_prototype(int start){//迪杰斯特拉算法原型，输入参数为起点在nodelist中的index，直接根据邻接矩阵计算起点到图中所有节点的最短路径。
		int[][] mat=this.constructDelayAdjMatrix();
		ArrayList<Integer> output=new ArrayList<>();

		int min;
		int[] dis=new int[mat.length];
		int i,j,u=0;
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
		ArrayList<Integer> result=dijkstra_prototype(start);
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
		ArrayList<Integer> result=dijkstra_prototype(start);
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
		ArrayList<Integer> result=dijkstra_prototype(start);
		ArrayList<String> StringResult= new ArrayList<>();
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


	public int getDistance(String src,String dst){//获取从src到达dst的路径的最短延迟
		int distance=0;
		ArrayList<String> path=this.getStringPath(src, dst);
		for(int i=0;i<path.size()-1;i++){
			distance+=this.getDirectDistance(path.get(i), path.get(i+1));
		}
		return distance;
	}

	public void collectFlowRequest(String FlowRequestFilePath){//读取产生的流量请求，将其存入到图的flowRequestList列表中并初始化,暂定每5min一次
		//File的格式为每条流的请求为一行，分别为（1）源地址（2）目的地址（3）带宽要求（4）延迟要求（5）优先级,变量之间用空格符分隔开
		this.flowRequestList.clear();//执行列表的清理，防止上次收集过程中的数据残存在本次的列表中。
		try{
			String encoding="utf-8";
			File file_in=new File(FlowRequestFilePath);
			if(file_in.isFile()&&file_in.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					String[] info_temp=lineTxt.split(" ");
					flowRequestList.add(new Flow_request(info_temp[0],info_temp[1],Integer.parseInt(info_temp[2]),Integer.parseInt(info_temp[3]),Integer.parseInt(info_temp[4])));
					}
				read.close();
			}
		}catch(Exception e){
			System.out.println("Error:读取数据流请求文件出错！");
			e.printStackTrace();
		}
	}

	public void dijkstra_flow_request_path_write(Flow_request fr){//为每一条数据流请求进行纯延迟最短路径计算，分配最短路径
		ArrayList<String> path_node=this.getStringPath(fr.src_id, fr.dst_id);
		Link tmp;
		for(int i=0;i<(path_node.size()-1);i++){
			tmp=this.getLink(path_node.get(i), path_node.get(i+1));
			if(tmp!=null){
				fr.AllocatedPath.add(tmp);
			}
		}
	}


	public void localTE() {

		for (Flow_request temp_flow_request : this.flowRequestList) {
			this.dijkstra_flow_request_path_write(temp_flow_request);
			for (Link temp : temp_flow_request.AllocatedPath) {
				temp.allocated_bandwidth.put(temp_flow_request, temp_flow_request.bandwidth_request);
			}
		}

		for (Link tmp : this.linklist) {
			int sum_priority = 0;
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				sum_priority += key.priority;
			}
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				float band_width_temp = tmp.bandwidth * ((float) key.priority / sum_priority);
				if (band_width_temp < key.min_bandwidth) {
					key.min_bandwidth = band_width_temp;
					tmp.isAllocated = true;
				}//else{//带宽更新操作转移到topoUpdate函数中进行
				//tmp.bandwidth-=band_width_temp;
				//if(tmp.bandwidth<=0){
				//	tmp.isAllocated=true;
				//}
			}
		}



			//Debug 用，打印分配完成后，每条数据流所获得的分配链路流量
		for (Flow_request tm : this.flowRequestList) {
			System.out.print(tm.AllocatedPath + " || Allocated Bandwidth: ");
			System.out.println(tm.min_bandwidth);
		}


	}

	
	
	public void addLink(String src_point,String dst_point,int delay,int bandwidth){//添加从src到dst的链路
		AdjInfo adj_to_add=new AdjInfo(dst_point,bandwidth,delay);
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_to_add);
			}
		}
	}
	
	public void addLink(String src_point,AdjInfo adj_info_to_add){//addEdge方法重载
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_info_to_add);
			}
		}
	}
	
	
	//删除link，用于次短路径的计算
	public void removeLink(String src_point,String dst_point){
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=tmp.has_link_to(dst_point);
				nodelist.remove(index);
			}else{
				System.out.println("Remove Link: "+src_point+"=>"+dst_point+" Failed.");
			}
		}
	}

	public void removeLink(Link link_to_remove){//removeLink函数重载
		if(this.linklist.contains(link_to_remove)) {
			this.linklist.remove(link_to_remove);
		}else{
			System.out.println("Remove Link: "+link_to_remove.start_switch+"=>"+link_to_remove.end_switch+" Failed.");
		}
	}

/*拓扑更新函数的作用主要有如下两个:
* (1)将带宽已经全部分配完成的link从图的topo中移除
* (2)将已经部分带宽已经分配出去的Link的剩余带宽进行数据更新
* */
	public void topologyUpdate(){
		for(Flow_request fl:this.flowRequestList){
			for(Link link_temp:fl.AllocatedPath){
				link_temp.bandwidth-=fl.min_bandwidth;
				if(link_temp.bandwidth<=0){
					link_temp.isAllocated=true;
				}
			}
		}

		for(Link link:this.linklist){
			if(link.isAllocated==true){
				this.removeLink(link);
			}
		}

	}
	
	
	public void run(){
		this.collectFlowRequest(GraphFlowReuqestListFile);
		this.localTE();
		//this.topologyUpdate();
	}


	public void FlowRequestFileGenerate(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumber为数据流需求文件中需要产生的数据流数目
		Random rand=new Random(47);
		int FlowSize=this.nodelist.size();
		try {
            File file_out = new File(GraphFlowReuqestListFile);
            FileWriter file_write=new FileWriter(file_out,false);
            for (int i = 0; i < FlowRequestNumber; i++) {
                int nodeStartNumber=rand.nextInt(FlowSize);
                int nodeEndNumber=rand.nextInt(FlowSize);
                int bandwidthRequest=rand.nextInt(500);
                int delayRequest=rand.nextInt(500);
                int priorityRequest=rand.nextInt(10);
                if(nodeStartNumber==nodeEndNumber){
                    nodeEndNumber=rand.nextInt(FlowSize);
                }
                String content=this.nodelist.get(nodeStartNumber).ID+" "+this.nodelist.get(nodeEndNumber).ID+" "+bandwidthRequest+" "+delayRequest+" "+priorityRequest+"\n";
                file_write.write(content);
            }
            file_write.close();
        }catch(Exception e){
		    e.printStackTrace();
        }

	}
	
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		Graph g1=new Graph(GraphLinkFile,GraphNodeFile);
		
		/*
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
		
		ArrayList<Integer> out=g1.dijkstra_prototype(3);//测试单域情况中，路由算法的有效性
		for(Integer i:out){
			System.out.print(i+"->");
			
		}
		System.out.println("End");
		
		g1.printPath("00:00:00:00:00:00:00:03", "00:00:da:c5:01:a3:44:48");
		
		System.out.println("");
		
		
		int t=g1.getDistance("00:00:00:00:00:00:00:03", "00:00:da:c5:01:a3:44:48");
		System.out.println("Distance from source to destination is:"+t);
		*/
		/*
		g1.collectFlowRequest("F:\\java code\\FL_PlugIn Project\\Floodlight_plugin\\src\\Flow Request.txt");
		for(Flow_request t:g1.flowRequestList){
			g1.dijkstra_flow_request_path_write(t);
			t.showAllocatedPath();
		}
		*/
		g1.FlowRequestFileGenerate(100);
		//g1.collectFlowRequest("E:\\代码\\java\\FloodlightPlugin\\src\\Flow Request.txt");
	 	//g1.localTE();
	 	g1.run();
	 	//System.out.println("END NOW");

	 	/*
		for(Flow_request t:g1.flowRequestList){
			System.out.println(t);
		}
		*/
		
		
		//g1.localTE();
	}
	
	
}
