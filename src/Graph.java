import java.util.*;
import java.io.*;
import  java.text.DecimalFormat;
import java.util.Calendar;

/*
读取link以及clusters文件的信息，将原来的拓扑结构转化为外挂程序的拓扑邻接链表结构，以便我们执行外部的TE算法。
*/

/*目前亟待解决的问题
* （1）读取FLow Request文件的过程中，如果遇到有源节点、目的节点不在clusters文件中的情况下，程序运行会报错，需要添加异常捕捉信息
* （2）
* （3）
* */


public class Graph {
	ArrayList<Node> nodelist=new ArrayList<>();//存储图的所有节点信息
	ArrayList<Link> linklist=new ArrayList<>();//存储图的所有link信息
	ArrayList<Flow_request> flowRequestList=new ArrayList<>();//存储需要进行TE的所有流需求
	ArrayList<Flow_request> crossDomainFlowRequestList=new ArrayList<>();//存储在数据流请求收集之后，预处理过程中发现的跨域数据流请求
	ArrayList<Flow_request> InsideDomainFlowRequestList=new ArrayList<>();//存储在数据流请求收集之后，预处理过程中发现的域内数据刘请求

	public static int domain=0;//domain从0到4，在每一个不同的服务器程序中，采用当前topo所在的domain
	public static String MarginalNodesFilePath="F:\\java code\\src\\topo\\MarginalNodes";
	public static String MarginalLinksFilePath="F:\\java code\\src\\topo\\MarginalLinks";
    public static String GraphNodeFile="F:\\java code\\src\\topo\\clusters_domain5 (copy)";
    public static String GraphLinkFile="F:\\java code\\src\\topo\\links_domain5 (copy)";
    public static String GraphFlowReuqestListFile="F:\\java code\\src\\flow_request\\NewFlowRequest.txt";
    public static String crossDomainFlowRequestListFile="F:\\java code\\src\\flow_request\\crossDomainFlowRequest.txt";
    public static String InDomainFlowRequestListFile="F:\\java code\\src\\flow_request\\InDomainFlowRequest.txt";
    public static String flowBreakDownPath="F:\\java code\\src\\flow_request\\";
    public int max_delay=10000000;//表示延迟的最大值，随着实验的真实数值而改变，需要保证的是path的总的延迟（一条path中所有link的延迟之和要小于max_delay）
	public static int TE_count=0;
	public static String[][] MarginalSwitch={{"00:00:00:00:00:00:00:3a","00:00:00:00:00:00:00:6c"},{"00:00:00:00:00:00:00:9d","00:00:00:00:00:00:00:a0"},{"00:00:00:00:00:00:01:3f","00:00:00:00:00:00:01:43"},{"00:00:00:00:00:00:01:bd","00:00:00:00:00:00:02:12"},{"00:00:00:00:00:00:02:5a","00:00:00:00:00:00:02:5c"}};
	public String[] localMarginalSwitch={" "," "};
	public int[][] marginalSwitchDistance=new int[10][10];

	public Graph(String link_file_path,String clusters_file_path){//初始化图，想通过link和clusters两个文件来进行图的初始化，函数参数用文件的String路径来表示，便于后期修改。
		try{
			File file_in1=new File(clusters_file_path);
			if(file_in1.isFile()&&file_in1.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in1));
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					addNode(new Node(lineTxt));
					}
				read.close();
			}

			File file_in2=new File(link_file_path);
			if(file_in2.isFile()&&file_in2.exists()){
				InputStreamReader read2=new InputStreamReader(new FileInputStream(file_in2));
				BufferedReader bufferReader=new BufferedReader(read2);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					String[] info_temp=lineTxt.split(" ");

					String initial_id=info_temp[0];

					String out_port=info_temp[1];

					String adj_id=info_temp[2];

					String in_port=info_temp[3];

					int adj_delay=Integer.parseInt(info_temp[4].substring(2),16);

					int adj_bandwidth=Integer.parseInt(info_temp[5], 10);

					AdjInfo adj_new=new AdjInfo(adj_id,adj_bandwidth,adj_delay);

					for(Node tmp:this.nodelist){
						if(tmp.ID.equals(initial_id)){
							//int index=nodelist.indexOf(tmp);
							//nodelist.get(index).add_adj_node(adj_new);
							tmp.add_adj_node(adj_new);
						}
					}
					this.linklist.add(new Link(initial_id,out_port,adj_id,in_port,adj_delay,adj_bandwidth));
					}
				read2.close();
			}
		}catch(Exception e){
			System.out.println("Error:读取文件过程中出现错误！");
			e.printStackTrace();
		}

		switch(domain){
			case 0:
				localMarginalSwitch[0]="00:00:00:00:00:00:00:3a";
				localMarginalSwitch[1]="00:00:00:00:00:00:00:6c";
				break;
			case 1:
				localMarginalSwitch[0]="00:00:00:00:00:00:00:9d";
				localMarginalSwitch[1]="00:00:00:00:00:00:00:a0";
				break;
			case 2:
				localMarginalSwitch[0]="00:00:00:00:00:00:01:3f";
				localMarginalSwitch[1]="00:00:00:00:00:00:01:43";
				break;
			case 3:
				localMarginalSwitch[0]="00:00:00:00:00:00:01:bd";
				localMarginalSwitch[1]="00:00:00:00:00:00:02:12";
				break;
			case 4:
				localMarginalSwitch[0]="00:00:00:00:00:00:02:5a";
				localMarginalSwitch[1]="00:00:00:00:00:00:02:5c";
				break;
			default:
				break;
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
		int max_distance=max_delay;

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
				if(end!=-1) {//保证end是在link中的一条链路,当end=-1时，表示在nodelist中没有找到这个node
					matrix[start][end] = info.adj_delay;
				}
			}
		}
		return matrix;
	}

	/*此函数的输出为以start节点为起点，到途中所有可达节点的一个最短路径列表。输出结果为prev矩阵，前驱顶点数组。即，prev[i]的值是"顶点start"到"顶点i"的最短路径所经历的全部顶点中，位于"顶点i"之前的那个顶点。*/
//此函数debug完成，没有bug了。
	public int[] dijkstra_prototype(int start){//迪杰斯特拉算法原型，输入参数为起点在nodelist中的index，直接根据邻接矩阵计算起点到图中所有节点的最短路径。
		int[][] mat=this.constructDelayAdjMatrix();
		ArrayList<Integer> output=new ArrayList<>();

		int min;
		int[] dis=new int[mat.length];//标志从start节点到目的节点的距离
		int i,j,u=0;
		int[] v=new int[mat.length];//标志节点的可达性
		int[] prev=new int[mat.length];//前驱顶点数组。即，prev[i]的值是"顶点start"到"顶点i"的最短路径所经历的全部顶点中，位于"顶点i"之前的那个顶点。

		for(i=0;i<mat.length;i++){
			dis[i]=mat[start][i];
			v[i]=0;
			prev[i]=-1;
		}
		v[start]=1;//将初始节点start设置为可达
		dis[start]=0;

		output.add(start);//将初始结点start添加到输出列表中

		for(i=1;i<mat.length;i++){
			min=max_delay;//此处的min根据实验情况设置为100w，在真实情况中，最大延迟可能更加大，需要根据实际进行调节
			for(j=0;j<mat.length;j++){
				if((v[j]==0)&&(dis[j]<min)){
					min=dis[j];
					u=j;
				}
			}
			v[u]=1;
			/*此处似乎有问题。因为最短路径可能会重复用到某一个点，所以不应该有检测ouput是否含有u节点*/
			/*if(!output.contains(u)) {
				output.add(u);
			}*/
			for(j=0;j<mat.length;j++){
				int tmp = (mat[u][j]==max_delay ? max_delay : (min + mat[u][j]));
				if(v[j]==0&&dis[j]>tmp){
					dis[j]=dis[u]+mat[u][j];
					prev[j]=u;
				}
			}
		}
		return prev;
	}



	public void printPath(String src,String dst){//打印从src岛dst节点所经过的最短路径中的所有节点
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		int[] result=dijkstra_prototype(start);
		ArrayList<Integer> output_result=new ArrayList<>();
//		output_result.add(end);
		int i=end;
		int prev_index=0;
		while(result[i]!=-1){
			prev_index=result[i];
			output_result.add(prev_index);
			i=prev_index;
		}
		if(!output_result.isEmpty()) {
//			output_result.add(start);
			Collections.reverse(output_result);
			System.out.print(src+"==>");
			for (int j = 0; j < output_result.size(); j++) {
				System.out.print(getId(output_result.get(j)) + "==>");
			}
			System.out.print(dst+"||");
		}else{
			System.out.println("Can not find the path from "+src+" to the "+dst);
		}
		//result.add(getNum(dst));//此处似乎有错误，我们在这里是需要检查dst节点的可达性，而不是添加进去
		/*if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=0;i<=end_index;i++){
				output_result.add(result.get(i));
			}
			System.out.println("The path from "+"\""+src+"\""+" to \""+dst+"\""+" is:");
			for(int i=0;i<output_result.size();i++){
				System.out.print(getId(output_result.get(i))+"==>");
			}
			System.out.println("End");
		}
		else
			System.out.println("Can not find the path from "+src+" to the "+dst);;*/
	}

	/*public ArrayList<Integer> getIndexPath(String src,String dst){//得到从src到dst最短路径所需要经过的所有节点的index
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		ArrayList<Integer> result=dijkstra_prototype(start);
		//result.add(getNum(dst));
		if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=end_index+1;i<result.size();i++){
				result.remove(i);
			}
			return result;
		}
		else
			System.out.println("Can not find the path from "+src+" to the "+dst);
			return null;
	}*/

//	getStringPath函数已经完成，没有bug
	public ArrayList<String> getStringPath(String src,String dst){//得到从src到dst最短路径所需要经过的所有节点的String
		int start=getNum(src);//获取src在nodelist中的下标
		int end=getNum(dst);//获取dst在nodelist中的下标
		int[] result=dijkstra_prototype(start);
		ArrayList<Integer> output_result=new ArrayList<>();
		ArrayList<String> output_string=new ArrayList<>();
		int i=end;
		int prev_index=0;

		while(result[i]!=-1){
			prev_index=result[i];
			output_result.add(prev_index);
			i=prev_index;
		}
		if(!output_result.isEmpty()) {
			output_result.add(start);
			Collections.reverse(output_result);
			output_result.add(end);
			for (int j = 0; j < output_result.size(); j++) {
				output_string.add(getId(output_result.get(j)));
			}
			return output_string;
		}else{
			return null;
		}
	}


	public int getDelay(String src,String dst){//获取从src到达dst的路径的最短延迟
		int distance=0;
		ArrayList<String> path=this.getStringPath(src, dst);
		if(path!=null) {
			for (int i = 0; i < path.size() - 1; i++) {
				distance += this.getDirectDistance(path.get(i), path.get(i + 1));
			}
		}else
			distance=99999;
		return distance;
	}

	public void collectFlowRequest(String FlowRequestFilePath){//读取产生的流量请求，将其存入到图的flowRequestList列表中并初始化,暂定每5min一次
		//File的格式为每条流的请求为一行，分别为（1）源地址（2）目的地址（3）带宽要求（4）延迟要求（5）优先级,变量之间用空格符分隔开
		this.flowRequestList.clear();//执行列表的清理，防止上次收集过程中的数据残存在本次的列表中。
		this.crossDomainFlowRequestList.clear();
		this.InsideDomainFlowRequestList.clear();
		try{
			String encoding="utf-8";
			File file_in=new File(FlowRequestFilePath);
			if(file_in.isFile()&&file_in.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file_in),encoding);
				BufferedReader bufferReader=new BufferedReader(read);
				String lineTxt;
				while((lineTxt=bufferReader.readLine())!=null){
					String[] info_temp=lineTxt.split(" ");
					flowRequestList.add(new Flow_request(info_temp[0],info_temp[1],info_temp[2],Integer.parseInt(info_temp[3],10),Integer.parseInt(info_temp[4],16),Integer.parseInt(info_temp[5],10)));
				}
				read.close();
			}
		}catch(Exception e){
			System.out.println("Error:读取数据流请求文件出错！");
			e.printStackTrace();
		}
	}

	public void marginal_distance_init(){//跨域controller专用函数，用于初始化marginalSwitchDistance数组
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				marginalSwitchDistance[i][j]=this.getDelay(MarginalSwitch[(i/2)][(i%2)],MarginalSwitch[(j/2)][(j%2)]);
			}
		}
	}

	public void crossDomianRequestProcess(){//对跨域数据流和域内数据流请求进行一次划分，为下面的跨域数据流分割做准备,将跨域数据流请求写入crossDomainFlowRequestListFile文件中，将域内数据流请求写入InDomainFlowRequestListFile文件中
		for(Flow_request tmp_request:this.flowRequestList){
			if(tmp_request.cross_domain_flag==true){
				int[] dis=new int[4];
				dis[0]=this.getDelay(tmp_request.src_id,localMarginalSwitch[0])+marginalSwitchDistance[tmp_request.src_domain*2][tmp_request.dst_domain*2];
				dis[1]=this.getDelay(tmp_request.src_id,localMarginalSwitch[0])+marginalSwitchDistance[tmp_request.src_domain*2][tmp_request.dst_domain*2+1];
				dis[2]=this.getDelay(tmp_request.src_id,localMarginalSwitch[1])+marginalSwitchDistance[tmp_request.src_domain*2+1][tmp_request.dst_domain*2];
				dis[3]=this.getDelay(tmp_request.src_id,localMarginalSwitch[1])+marginalSwitchDistance[tmp_request.src_domain*2+1][tmp_request.dst_domain*2+1];
				int min_number=0;
				int min_value=dis[0];
				for(int i=1;i<4;i++){
					if(dis[i]<min_value){
						min_number=i;
						min_value=dis[i];
					}
				}
				switch(min_number){
					case 0:
						tmp_request.src_id=MarginalSwitch[tmp_request.src_domain][0];
						tmp_request.dst_id=MarginalSwitch[tmp_request.dst_domain][0];
						break;
					case 1:
						tmp_request.src_id=MarginalSwitch[tmp_request.src_domain][0];
						tmp_request.dst_id=MarginalSwitch[tmp_request.dst_domain][1];
						break;
					case 2:
						tmp_request.src_id=MarginalSwitch[tmp_request.src_domain][1];
						tmp_request.dst_id=MarginalSwitch[tmp_request.dst_domain][0];
						break;
					case 3:
						tmp_request.src_id=MarginalSwitch[tmp_request.src_domain][1];
						tmp_request.dst_id=MarginalSwitch[tmp_request.dst_domain][1];
						break;
					default:
						break;
				}
				this.crossDomainFlowRequestList.add(tmp_request);
			}else if(tmp_request.cross_domain_flag==false){
				this.InsideDomainFlowRequestList.add(tmp_request);
			}
		}

		try {
			File file_out = new File(crossDomainFlowRequestListFile);
			FileWriter file_write = new FileWriter(file_out, false);
			for(Flow_request tmp:this.crossDomainFlowRequestList) {
				String crossDomainFlowRequestString = tmp.fr_id+" "+tmp.src_id + " "+tmp.dst_id+" "+tmp.bandwidth_request+" "+tmp.delay_request+" "+tmp.priority+"\n";
				file_write.write(crossDomainFlowRequestString);
			}
			file_write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			File file_out_1=new File(InDomainFlowRequestListFile);
			FileWriter file_write_1=new FileWriter(file_out_1,false);
			for(Flow_request tmp_1:this.InsideDomainFlowRequestList){
				String InDomainFlowRequestString=tmp_1.fr_id+" "+ tmp_1.src_id+" "+tmp_1.dst_id+" "+tmp_1.bandwidth_request+" "+tmp_1.delay_request+" "+ tmp_1.priority+"\n";
				file_write_1.write(InDomainFlowRequestString);
			}
			file_write_1.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void dijkstra_flow_request_path_write(Flow_request fr){//为每一条数据流请求进行纯延迟最短路径计算，分配最短路径
		ArrayList<String> path_node=this.getStringPath(fr.src_id, fr.dst_id);
		Link tmp;
		if(path_node!=null) {
			for (int i = 0; i < (path_node.size() - 1); i++) {
				tmp = this.getLink(path_node.get(i), path_node.get(i + 1));//此处需要修改，getLink应该可以获取任意两个可达节点之间的链路，而此处使用的为两点直接直连的link
				if (tmp != null) {
					fr.AllocatedPath.add(tmp);
				}
				else{
					//此处需要添加从path_node.get(i)到path_node.get(i+1)的所有间接link
					//fr.AllocatedPath.add(添加所有从path_node(i)到path_node(i+1)的link);
				}
			}
		}
	}


	public void localTE() {//域内controller处理数据流请求的TE函数
		TE_count++;
		for (Flow_request temp_flow_request : this.InsideDomainFlowRequestList) {
			int min=9999999;
			this.dijkstra_flow_request_path_write(temp_flow_request);
			for (Link temp : temp_flow_request.AllocatedPath) {
				temp.allocated_bandwidth.put(temp_flow_request, temp_flow_request.bandwidth_request);
				if(temp.delay<min){
					min=temp.delay;
				}
			}
			temp_flow_request.min_delay=min;
		}
		for (Link tmp : this.linklist) {
			int sum_priority = 0;
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				sum_priority += key.priority;
			}
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				if(key.AllocatedPath.size()!=0) {
					float band_width_temp = tmp.bandwidth * ((float) key.priority / sum_priority);
					if (band_width_temp <= key.min_bandwidth&&band_width_temp<=key.bandwidth_request) {
						key.min_bandwidth = band_width_temp;
						tmp.isAllocated = true;
					}
					else if(band_width_temp<=key.min_bandwidth&&band_width_temp>=key.bandwidth_request){
						key.min_bandwidth=key.bandwidth_request;
					}
				}
			}
		}
	}

	public void TE(){//跨域controller用于处理跨域数据流的函数
		TE_count++;

		for (Flow_request temp_flow_request : this.flowRequestList) {
			int min=9999999;
			this.dijkstra_flow_request_path_write(temp_flow_request);
			for (Link temp : temp_flow_request.AllocatedPath) {
				temp.allocated_bandwidth.put(temp_flow_request, temp_flow_request.bandwidth_request);
				if(temp.delay<min){
					min=temp.delay;
				}
			}
			temp_flow_request.min_delay=min;
		}

		for (Link tmp : this.linklist) {
			int sum_priority = 0;
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				sum_priority += key.priority;
			}
			for (Flow_request key : tmp.allocated_bandwidth.keySet()) {
				if(key.AllocatedPath.size()!=0) {
					float band_width_temp = tmp.bandwidth * ((float) key.priority / sum_priority);
					if (band_width_temp <= key.min_bandwidth&&band_width_temp<=key.bandwidth_request) {
						key.min_bandwidth = band_width_temp;
						tmp.isAllocated = true;
					}
					else if(band_width_temp<=key.min_bandwidth&&band_width_temp>=key.bandwidth_request){
						key.min_bandwidth=key.bandwidth_request;
					}
				}
			}
		}
	}

	public void flowBreakDown(){//将跨域controller分配完成后的数据流，分解为多个域内数据流请求，按域名写入文件
		for(Flow_request tm:this.flowRequestList){
			if(tm.min_bandwidth==99999.0){
				tm.min_bandwidth=0;
			}
			ArrayList<String> out_for_write=getStringPath(tm.src_id,tm.dst_id);
			if(out_for_write!=null){
				for(int index_out=0;index_out<out_for_write.size()-1;index_out++) {
					Link tmp_out=getLink(out_for_write.get(index_out),out_for_write.get(index_out+1));
					if(tmp_out!=null){
						int src_num=Integer.parseInt(tmp_out.start_switch.substring(21));
						int src_domain=0;
						if(src_num>=1&&src_num<=150){
							src_domain=1;
						}else if(src_num>=151&&src_num<=286){
							src_domain=2;
						}else if(src_num>=287&&src_num<=427){
							src_domain=3;
						}else if(src_num>=428&&src_num<=568){
							src_domain=4;
						} else if(src_num>=569&&src_num<=717){
							src_domain=5;
						}else{
							src_domain=0;
						}

						int dst_num=Integer.parseInt(tmp_out.end_switch.substring(21));
						int dst_domain=0;
						if(dst_num>=1&&dst_num<=150){
							dst_domain=1;
						}else if(dst_num>=151&&dst_num<=286){
							dst_domain=2;
						}else if(dst_num>=287&&dst_num<=427){
							dst_domain=3;
						}else if(dst_num>=428&&dst_num<=568){
							dst_domain=4;
						} else if(dst_num>=569&&dst_num<=717){
							dst_domain=5;
						}else{
							dst_domain=0;
						}
						if(src_domain==dst_domain&&src_domain!=0){
							String local_file_path=flowBreakDownPath+"_"+src_domain;
							try{
								File file_out = new File(local_file_path);
								FileWriter file_write = new FileWriter(file_out, true);
								String fr_id=tm.fr_id+"_"+index_out+"#";
								String content_write=fr_id+" "+tmp_out.start_switch+" "+tmp_out.end_switch+" "+tm.min_bandwidth+" "+tm.delay_request+"\n";
								file_write.write(content_write);
								file_write.close();
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public void flowMerge(){
		try{
			File crossDomainFlowTEresult=new File("F:\\output\\2\\crossDomainFlowTEResult");//存储在域内进行TE之后的所有跨域数据流的结果,文件中写的是跨域数据流的序号以及所分配的带宽。
			FileWriter file_write = new FileWriter(crossDomainFlowTEresult, true);
			for(Flow_request tm:this.InsideDomainFlowRequestList) {
				if (tm.min_bandwidth == 99999.0) {
					tm.min_bandwidth = 0;
				}
			if(tm.fr_id.endsWith("#")){
					String content=tm.fr_id.substring(0,tm.fr_id.length()-2)+" "+tm.bandwidth_request+" "+tm.min_bandwidth+"\n";
				}
			}
			file_write.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}



	public void printResult(){//此函数用于打印TE输出结果，可以将函数改编为输出到文件,按照TE轮次，每一轮生成一个新的TE结果文件,TEoutput_n.txt
		for (Flow_request tm : this.flowRequestList) {
			if(tm.min_bandwidth==99999.0){
				tm.min_bandwidth=0;
			}
			//System.out.print(tm.src_id+" -> "+" "+tm.dst_id+" "+tm.AllocatedPath + " || Allocated Bandwidth: ");
			//System.out.println(tm.min_bandwidth);
//			printPath(tm.src_id,tm.dst_id);
			ArrayList<String> out_for_print=getStringPath(tm.src_id,tm.dst_id);
			System.out.println("The TE result for flow request from "+tm.src_id+" to "+tm.dst_id+" is: ");
			if(out_for_print!=null) {
				for(int index_out=0;index_out<out_for_print.size()-1;index_out++){
					Link tmp_out=getLink(out_for_print.get(index_out),out_for_print.get(index_out+1));
					if(tmp_out!=null){
						System.out.println(tmp_out.start_switch+" "+tmp_out.outport);
						System.out.println(tmp_out.end_switch+" "+tmp_out.inport);
//						System.out.println();
					}
				}
//				这三行用于打印在一轮分配结束之后，所有flow request分配到的带宽情况
				System.out.println("Flow request id: "+tm.fr_id);
				System.out.println("Request Bandwidth: "+tm.bandwidth_request);
				System.out.println("Allocated bandwidth: "+tm.min_bandwidth );
				System.out.println("Flowrequest priority: "+tm.priority);
				System.out.println("Delay : "+tm.min_delay);
				System.out.println("Not allocated bandwidth: "+(tm.bandwidth_request-tm.min_bandwidth));
				System.out.println();
			}else{
				System.out.println("TE failed:Can not find a valid path.");
				System.out.println();
			}
			/*int[] tmp=dijkstra_prototype(getNum(tm.src_id));
			System.out.println(getNum(tm.src_id)+"===");
			for(int i=0;i<tmp.length;i++){
				System.out.print(tmp[i]+" ");
			}
			System.out.println();*/
		}
	}

	public void printResult_1(){//新的TE结果输出函数，输出TE结果和同步信息的结果，TE结果按照每条数据流请求的TE结果输出一个文件,文件名按这种形式"/home/havne2/h123/TE/"+src+"-"+dst，同步信息输出到一个文件夹下面即可
		String file_syn_out_string="F:\\output\\1\\bandwithAllocation";

		//如果需要对同步文件进行清零，则执行下面的代码
		/*try {
			File file_out_2 = new File(file_syn_out_string);
			FileWriter file_write_2 = new FileWriter(file_out_2, false);
			file_write_2.close();
		}catch (IOException e) {
			e.printStackTrace();
		}*/

		for (Flow_request tm : this.flowRequestList) {
			if (tm.min_bandwidth == 99999.0) {
				tm.min_bandwidth = 0;
			}
			String file_TE_out_string="F:\\output\\"+tm.src_id.substring(21)+"-"+tm.dst_id.substring(21);

			ArrayList<String> out_for_print=getStringPath(tm.src_id,tm.dst_id);
			try {
				File file_out = new File(file_TE_out_string);
				FileWriter file_write=new FileWriter(file_out,false);
				String start_switch_str=tm.src_id+" 1\n";
				String end_switch_str=tm.dst_id+" 1\n";
				file_write.write(start_switch_str);
				if(out_for_print!=null) {
					for (int index_out = 0; index_out < out_for_print.size() - 1; index_out++) {
						Link tmp_out = getLink(out_for_print.get(index_out), out_for_print.get(index_out + 1));
						if (tmp_out != null) {
							String file_content=tmp_out.start_switch+" "+tmp_out.outport+"\n"+tmp_out.end_switch+" "+ tmp_out.inport+"\n";
							file_write.write(file_content);
						}
					}
				}
				file_write.write(end_switch_str);

				File file_out_1 = new File(file_syn_out_string);
				FileWriter file_write_1=new FileWriter(file_out_1,true);
				String file_syn_content=tm.fr_id+" "+tm.src_id+" "+tm.dst_id+" "+tm.bandwidth_request+" "+tm.min_bandwidth+" "+TE_count+"\n";
				file_write_1.write(file_syn_content);

				file_write.close();
				file_write_1.close();

			} catch (IOException e) {
			e.printStackTrace();
			}
		}
	}


	public void printSynchronizationInformation(){
		for (Flow_request tm : this.flowRequestList) {
			if(tm.min_bandwidth==99999.0){
				tm.min_bandwidth=0;
			}
			//System.out.print(tm.src_id+" -> "+" "+tm.dst_id+" "+tm.AllocatedPath + " || Allocated Bandwidth: ");
			//System.out.println(tm.min_bandwidth);
//			printPath(tm.src_id,tm.dst_id);
			ArrayList<String> out_for_print=getStringPath(tm.src_id,tm.dst_id);
			System.out.println(tm.fr_id+" "+tm.src_id+" "+tm.dst_id+" "+tm.bandwidth_request+" "+tm.min_bandwidth+" "+TE_count);
		}
	}

	public void calculate_utilization(){//计算整个网络中link的利用率，但是需要在每个controller同步link的带宽分配之后，才能计算。

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

		for (Iterator<Link> it1 = this.linklist.iterator(); it1.hasNext(); ) {
			Link link = (Link) it1.next();
			if(link.isAllocated==true){
				it1.remove();
			}
		}
	}

	public void flowrequestReGenerate(){
		try {
			File file_out = new File(GraphFlowReuqestListFile);
			FileWriter file_write=new FileWriter(file_out,false);
			for(Flow_request fl:this.flowRequestList){
				if(fl.min_bandwidth<fl.bandwidth_request){
					float bandwidth_regenerated=fl.bandwidth_request-fl.min_bandwidth;
					DecimalFormat fnum=new DecimalFormat("##0");//此处将最终的bandwidth取整，是因为在debug中，二次读取文件的flowrequest过程，代码329行对于0.00的字符串数字转化为int有bug.
					String bd=fnum.format(bandwidth_regenerated);
					String content=fl.fr_id+" "+fl.src_id+" "+fl.dst_id+" "+bd+" "+fl.delay_request+" "+fl.priority+"\n";
					file_write.write(content);
				}
			}
			file_write.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/*分配结果数据反馈函数
	* 用于反应此轮TE过后，所有数据流请求被分配的带宽情况以及还未分配，需要进入下一轮的请求
	* */
	public void allocateDataFeedback(){
		for(Flow_request fl:this.flowRequestList){
			System.out.println("FlowRequest from "+fl.src_id+" to "+fl.dst_id+" : ");
			System.out.println("Request Bandwidth: "+fl.bandwidth_request);
			System.out.println("Allocated bandwidth: "+fl.min_bandwidth );
			System.out.println("Not allocated bandwidth: "+(fl.bandwidth_request-fl.min_bandwidth));
		}
	}
	
	
	public void run(){//这个是域内controller需要运行的run()函数
		this.collectFlowRequest(GraphFlowReuqestListFile);//(1)
		this.crossDomianRequestProcess();//(2)在这一步结束，将crossDomainFlowRequestListFile文件中的内容传送给跨域controller，文件中存储的是所有的跨域数据流请求

		//域内controller接受到跨域controller发送来的分解的数据流请求文件后，添加到自己的域内数据流list中，进行域内TE
		this.localTE();//(6)运行域内的TE
		//this.printResult();
//		this.printSynchronizationInformation();
		//this.printResult_1();
		//this.topologyUpdate();
		//this.flowrequestReGenerate();
		this.flowMerge();//(7)localTE完成之后，将跨域数据流进行统计，写到F:\output\2\crossDomainFlowTEResult文件中，传送给跨域controller
	}

	public void run_1(){//跨域controller需要运行的run()函数
		this.collectFlowRequest(GraphFlowReuqestListFile);//(3)收集所有域内controller上传来的文件所整合而成的跨域数据刘请求
		this.TE();//(4)运行跨域数据流请求的TE
		this.flowBreakDown();//(5)将跨域数据流分解为一个个的域内数据流，在此函数运行完后，将flowBreakDownPath文件夹下的文件按照domain名称发送给每一个域内控制器

		//
	}

	public void test(){
		this.collectFlowRequest(GraphFlowReuqestListFile);
//		for(Flow_request tm:this.flowRequestList){
//			System.out.println(getStringPath(tm.src_id, tm.dst_id)+"  ||  "+tm.min_bandwidth);
//		}
		this.printResult();
	}

	/*
	* 数据流请求文件产生方式1：根据图的初始化结果，对于所有Node的邻接链表进行rand遍历，选择源节点和目的节点，这种产生方式保证了数据流请求是100%有效的。
	* */
//  此函数似乎有一些问题，在测试过程中，产生的结果全部是null，但是产生方式2运行效果很好。
	/*public void FlowRequestFileGenerate_1(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumber为数据流需求文件中需要产生的数据流数目
		Random rand=new Random(43);
		int FlowSize=this.nodelist.size();
		try {
            File file_out = new File(GraphFlowReuqestListFile);
            FileWriter file_write=new FileWriter(file_out,false);
            for (int i = 0; i < FlowRequestNumber; i++) {

                int nodeStartNumber=rand.nextInt(FlowSize);
                int nodeEndNumber=rand.nextInt(nodelist.get(nodeStartNumber).adjcent_list.size());
                String nodeEndId=this.nodelist.get(nodeStartNumber).adjcent_list.get(nodeEndNumber).adj_id;
                int bandwidthRequest=rand.nextInt(500);
                int delayRequest=rand.nextInt(500);
                int priorityRequest=rand.nextInt(10)+1;
                if(nodeStartNumber==nodeEndNumber){
                    nodeEndNumber=rand.nextInt(FlowSize);
                }
                String content=this.nodelist.get(nodeStartNumber).ID+" "+nodeEndId+" "+bandwidthRequest+" "+delayRequest+" "+priorityRequest+"\n";
                file_write.write(content);
            }
            file_write.close();
        }catch(Exception e){
		    e.printStackTrace();
        }

	}*/

	/*
	*数据流请求产生方式2：在图中随机选取两个点，作为源节点和目的节点，测试图的连通性和性能
	 *  */
	public void FlowRequestFileGenerate_2(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumber为数据流需求文件中需要产生的数据流数目
		Random rand=new Random(43);
		int FlowSize=this.nodelist.size();
		int count_num=0;
		try {
			File file_out = new File(GraphFlowReuqestListFile);
			FileWriter file_write=new FileWriter(file_out,false);
			for (int i = 0; i < FlowRequestNumber; i++) {
				count_num++;
				int nodeStartNumber=rand.nextInt(FlowSize);
				int nodeEndNumber=rand.nextInt(FlowSize);
				int bandwidthRequest=rand.nextInt(20)+1;
				int delayRequest=rand.nextInt(500);
				int priorityRequest=rand.nextInt(3)+1;
				if(nodeStartNumber==nodeEndNumber){
					nodeEndNumber=rand.nextInt(FlowSize);
				}
				String content=count_num+" "+this.nodelist.get(nodeStartNumber).ID+" "+this.nodelist.get(nodeEndNumber).ID+" "+bandwidthRequest+" "+delayRequest+" "+priorityRequest+"\n";
				file_write.write(content);
			}
			file_write.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	
	
	public static void main(String[] args) throws FileNotFoundException {

		Graph g_abstract=new Graph(MarginalLinksFilePath,MarginalNodesFilePath);//存储抽象后的节点和links,即跨域controller的topo信息
		g_abstract.marginal_distance_init();
		/*for(int i=0;i<g_abstract.marginalSwitchDistance.length;i++){
			for(int j=0;j<g_abstract.marginalSwitchDistance[i].length;j++){
				System.out.print(g_abstract.marginalSwitchDistance[i][j]+" ");
			}
			System.out.println();
		}*/
//		Graph g1=new Graph(GraphLinkFile,GraphNodeFile);
/*		long start=Calendar.getInstance().getTimeInMillis();//用于测试系统TE时间
		g1.FlowRequestFileGenerate_2(10000);//产生数据流文件的函数，如果希望沿用之前的数据流文件，则不需要运行此函数
		g1.run();
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("Run time :"+(double)(end-start)/1000);//用于测试系统TE时间*/


		//g_abstract.getDelay(MarginalSwitch[0][0],MarginalSwitch[2][0]);
/*		for(Node tmp:g1.nodelist){
			System.out.println(tmp.ID);
		}*/
//		int tmp=g_abstract.getDelay(MarginalSwitch[4][0],MarginalSwitch[2][1]);
//		System.out.println(tmp);
		/*int[][] tmp=g_abstract.constructDelayAdjMatrix();
		for(int i=0;i<tmp.length;i++){
			for(int j=0;j<tmp[i].length;j++){
				System.out.print(tmp[i][j]);
			}
			System.out.println();
		}*/
	}


}
