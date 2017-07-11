import java.util.*;
import java.io.*;
import  java.text.DecimalFormat;
import java.util.Calendar;

/*
��ȡlink�Լ�clusters�ļ�����Ϣ����ԭ�������˽ṹת��Ϊ��ҳ���������ڽ�����ṹ���Ա�����ִ���ⲿ��TE�㷨��
*/

/*Ŀǰؽ�����������
* ��1����ȡFLow Request�ļ��Ĺ����У����������Դ�ڵ㡢Ŀ�Ľڵ㲻��clusters�ļ��е�����£��������лᱨ����Ҫ����쳣��׽��Ϣ
* ��2��
* ��3��
* */


public class Graph {
	ArrayList<Node> nodelist=new ArrayList<>();//�洢ͼ�����нڵ���Ϣ
	ArrayList<Link> linklist=new ArrayList<>();//�洢ͼ������link��Ϣ
	ArrayList<Flow_request> flowRequestList=new ArrayList<>();//�洢��Ҫ����TE������������
	ArrayList<Flow_request> crossDomainFlowRequestList=new ArrayList<>();//�洢�������������ռ�֮��Ԥ��������з��ֵĿ�������������
	ArrayList<Flow_request> InsideDomainFlowRequestList=new ArrayList<>();//�洢�������������ռ�֮��Ԥ��������з��ֵ���������������

	public static int domain=0;//domain��0��4����ÿһ����ͬ�ķ����������У����õ�ǰtopo���ڵ�domain
	public static String MarginalNodesFilePath="F:\\java code\\src\\topo\\MarginalNodes";
	public static String MarginalLinksFilePath="F:\\java code\\src\\topo\\MarginalLinks";
    public static String GraphNodeFile="F:\\java code\\src\\topo\\clusters_domain5 (copy)";
    public static String GraphLinkFile="F:\\java code\\src\\topo\\links_domain5 (copy)";
    public static String GraphFlowReuqestListFile="F:\\java code\\src\\flow_request\\NewFlowRequest.txt";
    public static String crossDomainFlowRequestListFile="F:\\java code\\src\\flow_request\\crossDomainFlowRequest.txt";
    public static String InDomainFlowRequestListFile="F:\\java code\\src\\flow_request\\InDomainFlowRequest.txt";
    public static String flowBreakDownPath="F:\\java code\\src\\flow_request\\";
    public int max_delay=10000000;//��ʾ�ӳٵ����ֵ������ʵ�����ʵ��ֵ���ı䣬��Ҫ��֤����path���ܵ��ӳ٣�һ��path������link���ӳ�֮��ҪС��max_delay��
	public static int TE_count=0;
	public static String[][] MarginalSwitch={{"00:00:00:00:00:00:00:3a","00:00:00:00:00:00:00:6c"},{"00:00:00:00:00:00:00:9d","00:00:00:00:00:00:00:a0"},{"00:00:00:00:00:00:01:3f","00:00:00:00:00:00:01:43"},{"00:00:00:00:00:00:01:bd","00:00:00:00:00:00:02:12"},{"00:00:00:00:00:00:02:5a","00:00:00:00:00:00:02:5c"}};
	public String[] localMarginalSwitch={" "," "};
	public int[][] marginalSwitchDistance=new int[10][10];

	public Graph(String link_file_path,String clusters_file_path){//��ʼ��ͼ����ͨ��link��clusters�����ļ�������ͼ�ĳ�ʼ���������������ļ���String·������ʾ�����ں����޸ġ�
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
			System.out.println("Error:��ȡ�ļ������г��ִ���");
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

	public Node getNode(String node_id){//ͨ���ڵ����Ʋ��ҽڵ��Ƿ���ͼ�ڣ�����ڣ����ظýڵ㣬���򷵻�null.
		for(Node node:this.nodelist){
			if(node.ID.equals(node_id))
				return node;
		}
		return null;
	}

	public Link getLink(String start_switch,String end_switch){//���������ڵ��ȡLink
		for(Link tmp:this.linklist){
			if(tmp.getLinkStart().equals(start_switch)&&tmp.getLinkEnd().equals(end_switch)){
				return tmp;
			}

		}
		return null;
	}

	public void addNode(Node temp){//��ͼ����ӽڵ�
		this.nodelist.add(temp);
	}

	public int getDirectDistance(String src,String dst){//��ȡ������Linkֱ�������Ľڵ�֮��ľ���,��������ڵ�û��ֱ��link���������صľ���Ϊ-1
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

	public String getId(int id_num){//ͨ��int���͵������±������ҽڵ��String����Id
		return nodelist.get(id_num).ID;
	}

	public int getNum(String id){//ͨ���ڵ��String����Id�������ڽӾ����int�����±�
		int index=-1;

		for(Node temp:nodelist){
			if(temp.ID.equals(id)){
				index=nodelist.indexOf(temp);
				return index;
			}
		}
		return index;
	}


	public int[][] constructDelayAdjMatrix(){//����ͼ���ڽӾ���ͨ��Map<String id,int id_num>���������±�ͽڵ��ID�໥ӳ��
		int[][] matrix=new int[nodelist.size()][nodelist.size()];
		int max_distance=max_delay;

		for(int m=0;m<matrix.length;m++){//��ʼ���ڽӾ���-1��ʾ���ɴ�
			for(int n=0;n<matrix[m].length;n++){
				matrix[m][n]=max_distance;
			}
			matrix[m][m]=0;//����������ľ�������Ϊ0
		}

		for(Node node:nodelist){
			int start=nodelist.indexOf(node);
			int end;
			for(AdjInfo info:node.adjcent_list){
				end=nodelist.indexOf(getNode(info.adj_id));
				if(end!=-1) {//��֤end����link�е�һ����·,��end=-1ʱ����ʾ��nodelist��û���ҵ����node
					matrix[start][end] = info.adj_delay;
				}
			}
		}
		return matrix;
	}

	/*�˺��������Ϊ��start�ڵ�Ϊ��㣬��;�����пɴ�ڵ��һ�����·���б�������Ϊprev����ǰ���������顣����prev[i]��ֵ��"����start"��"����i"�����·����������ȫ�������У�λ��"����i"֮ǰ���Ǹ����㡣*/
//�˺���debug��ɣ�û��bug�ˡ�
	public int[] dijkstra_prototype(int start){//�Ͻ�˹�����㷨ԭ�ͣ��������Ϊ�����nodelist�е�index��ֱ�Ӹ����ڽӾ��������㵽ͼ�����нڵ�����·����
		int[][] mat=this.constructDelayAdjMatrix();
		ArrayList<Integer> output=new ArrayList<>();

		int min;
		int[] dis=new int[mat.length];//��־��start�ڵ㵽Ŀ�Ľڵ�ľ���
		int i,j,u=0;
		int[] v=new int[mat.length];//��־�ڵ�Ŀɴ���
		int[] prev=new int[mat.length];//ǰ���������顣����prev[i]��ֵ��"����start"��"����i"�����·����������ȫ�������У�λ��"����i"֮ǰ���Ǹ����㡣

		for(i=0;i<mat.length;i++){
			dis[i]=mat[start][i];
			v[i]=0;
			prev[i]=-1;
		}
		v[start]=1;//����ʼ�ڵ�start����Ϊ�ɴ�
		dis[start]=0;

		output.add(start);//����ʼ���start��ӵ�����б���

		for(i=1;i<mat.length;i++){
			min=max_delay;//�˴���min����ʵ���������Ϊ100w������ʵ����У�����ӳٿ��ܸ��Ӵ���Ҫ����ʵ�ʽ��е���
			for(j=0;j<mat.length;j++){
				if((v[j]==0)&&(dis[j]<min)){
					min=dis[j];
					u=j;
				}
			}
			v[u]=1;
			/*�˴��ƺ������⡣��Ϊ���·�����ܻ��ظ��õ�ĳһ���㣬���Բ�Ӧ���м��ouput�Ƿ���u�ڵ�*/
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



	public void printPath(String src,String dst){//��ӡ��src��dst�ڵ������������·���е����нڵ�
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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
		//result.add(getNum(dst));//�˴��ƺ��д�����������������Ҫ���dst�ڵ�Ŀɴ��ԣ���������ӽ�ȥ
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

	/*public ArrayList<Integer> getIndexPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��index
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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

//	getStringPath�����Ѿ���ɣ�û��bug
	public ArrayList<String> getStringPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��String
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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


	public int getDelay(String src,String dst){//��ȡ��src����dst��·��������ӳ�
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

	public void collectFlowRequest(String FlowRequestFilePath){//��ȡ�������������󣬽�����뵽ͼ��flowRequestList�б��в���ʼ��,�ݶ�ÿ5minһ��
		//File�ĸ�ʽΪÿ����������Ϊһ�У��ֱ�Ϊ��1��Դ��ַ��2��Ŀ�ĵ�ַ��3������Ҫ��4���ӳ�Ҫ��5�����ȼ�,����֮���ÿո���ָ���
		this.flowRequestList.clear();//ִ���б��������ֹ�ϴ��ռ������е����ݲд��ڱ��ε��б��С�
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
			System.out.println("Error:��ȡ�����������ļ�����");
			e.printStackTrace();
		}
	}

	public void marginal_distance_init(){//����controllerר�ú��������ڳ�ʼ��marginalSwitchDistance����
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				marginalSwitchDistance[i][j]=this.getDelay(MarginalSwitch[(i/2)][(i%2)],MarginalSwitch[(j/2)][(j%2)]);
			}
		}
	}

	public void crossDomianRequestProcess(){//�Կ����������������������������һ�λ��֣�Ϊ����Ŀ����������ָ���׼��,����������������д��crossDomainFlowRequestListFile�ļ��У�����������������д��InDomainFlowRequestListFile�ļ���
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

	public void dijkstra_flow_request_path_write(Flow_request fr){//Ϊÿһ��������������д��ӳ����·�����㣬�������·��
		ArrayList<String> path_node=this.getStringPath(fr.src_id, fr.dst_id);
		Link tmp;
		if(path_node!=null) {
			for (int i = 0; i < (path_node.size() - 1); i++) {
				tmp = this.getLink(path_node.get(i), path_node.get(i + 1));//�˴���Ҫ�޸ģ�getLinkӦ�ÿ��Ի�ȡ���������ɴ�ڵ�֮�����·�����˴�ʹ�õ�Ϊ����ֱ��ֱ����link
				if (tmp != null) {
					fr.AllocatedPath.add(tmp);
				}
				else{
					//�˴���Ҫ��Ӵ�path_node.get(i)��path_node.get(i+1)�����м��link
					//fr.AllocatedPath.add(������д�path_node(i)��path_node(i+1)��link);
				}
			}
		}
	}


	public void localTE() {//����controller���������������TE����
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

	public void TE(){//����controller���ڴ�������������ĺ���
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

	public void flowBreakDown(){//������controller������ɺ�����������ֽ�Ϊ����������������󣬰�����д���ļ�
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
			File crossDomainFlowTEresult=new File("F:\\output\\2\\crossDomainFlowTEResult");//�洢�����ڽ���TE֮������п����������Ľ��,�ļ���д���ǿ���������������Լ�������Ĵ���
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



	public void printResult(){//�˺������ڴ�ӡTE�����������Խ������ı�Ϊ������ļ�,����TE�ִΣ�ÿһ������һ���µ�TE����ļ�,TEoutput_n.txt
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
//				���������ڴ�ӡ��һ�ַ������֮������flow request���䵽�Ĵ������
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

	public void printResult_1(){//�µ�TE���������������TE�����ͬ����Ϣ�Ľ����TE�������ÿ�������������TE������һ���ļ�,�ļ�����������ʽ"/home/havne2/h123/TE/"+src+"-"+dst��ͬ����Ϣ�����һ���ļ������漴��
		String file_syn_out_string="F:\\output\\1\\bandwithAllocation";

		//�����Ҫ��ͬ���ļ��������㣬��ִ������Ĵ���
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

	public void calculate_utilization(){//��������������link�������ʣ�������Ҫ��ÿ��controllerͬ��link�Ĵ������֮�󣬲��ܼ��㡣

	}

	
	
	public void addLink(String src_point,String dst_point,int delay,int bandwidth){//��Ӵ�src��dst����·
		AdjInfo adj_to_add=new AdjInfo(dst_point,bandwidth,delay);
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_to_add);
			}
		}
	}
	
	public void addLink(String src_point,AdjInfo adj_info_to_add){//addEdge��������
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_info_to_add);
			}
		}
	}
	
	
	//ɾ��link�����ڴζ�·���ļ���
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

	public void removeLink(Link link_to_remove){//removeLink��������
		if(this.linklist.contains(link_to_remove)) {
			this.linklist.remove(link_to_remove);
		}else{
			System.out.println("Remove Link: "+link_to_remove.start_switch+"=>"+link_to_remove.end_switch+" Failed.");
		}
	}

/*���˸��º�����������Ҫ����������:
* (1)�������Ѿ�ȫ��������ɵ�link��ͼ��topo���Ƴ�
* (2)���Ѿ����ִ����Ѿ������ȥ��Link��ʣ�����������ݸ���
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
					DecimalFormat fnum=new DecimalFormat("##0");//�˴������յ�bandwidthȡ��������Ϊ��debug�У����ζ�ȡ�ļ���flowrequest���̣�����329�ж���0.00���ַ�������ת��Ϊint��bug.
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

	/*���������ݷ�������
	* ���ڷ�Ӧ����TE�����������������󱻷���Ĵ�������Լ���δ���䣬��Ҫ������һ�ֵ�����
	* */
	public void allocateDataFeedback(){
		for(Flow_request fl:this.flowRequestList){
			System.out.println("FlowRequest from "+fl.src_id+" to "+fl.dst_id+" : ");
			System.out.println("Request Bandwidth: "+fl.bandwidth_request);
			System.out.println("Allocated bandwidth: "+fl.min_bandwidth );
			System.out.println("Not allocated bandwidth: "+(fl.bandwidth_request-fl.min_bandwidth));
		}
	}
	
	
	public void run(){//���������controller��Ҫ���е�run()����
		this.collectFlowRequest(GraphFlowReuqestListFile);//(1)
		this.crossDomianRequestProcess();//(2)����һ����������crossDomainFlowRequestListFile�ļ��е����ݴ��͸�����controller���ļ��д洢�������еĿ�������������

		//����controller���ܵ�����controller�������ķֽ�������������ļ�����ӵ��Լ�������������list�У���������TE
		this.localTE();//(6)�������ڵ�TE
		//this.printResult();
//		this.printSynchronizationInformation();
		//this.printResult_1();
		//this.topologyUpdate();
		//this.flowrequestReGenerate();
		this.flowMerge();//(7)localTE���֮�󣬽���������������ͳ�ƣ�д��F:\output\2\crossDomainFlowTEResult�ļ��У����͸�����controller
	}

	public void run_1(){//����controller��Ҫ���е�run()����
		this.collectFlowRequest(GraphFlowReuqestListFile);//(3)�ռ���������controller�ϴ������ļ������϶��ɵĿ�������������
		this.TE();//(4)���п��������������TE
		this.flowBreakDown();//(5)�������������ֽ�Ϊһ�������������������ڴ˺���������󣬽�flowBreakDownPath�ļ����µ��ļ�����domain���Ʒ��͸�ÿһ�����ڿ�����

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
	* �����������ļ�������ʽ1������ͼ�ĳ�ʼ���������������Node���ڽ��������rand������ѡ��Դ�ڵ��Ŀ�Ľڵ㣬���ֲ�����ʽ��֤��������������100%��Ч�ġ�
	* */
//  �˺����ƺ���һЩ���⣬�ڲ��Թ����У������Ľ��ȫ����null�����ǲ�����ʽ2����Ч���ܺá�
	/*public void FlowRequestFileGenerate_1(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumberΪ�����������ļ�����Ҫ��������������Ŀ
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
	*���������������ʽ2����ͼ�����ѡȡ�����㣬��ΪԴ�ڵ��Ŀ�Ľڵ㣬����ͼ����ͨ�Ժ�����
	 *  */
	public void FlowRequestFileGenerate_2(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumberΪ�����������ļ�����Ҫ��������������Ŀ
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

		Graph g_abstract=new Graph(MarginalLinksFilePath,MarginalNodesFilePath);//�洢�����Ľڵ��links,������controller��topo��Ϣ
		g_abstract.marginal_distance_init();
		/*for(int i=0;i<g_abstract.marginalSwitchDistance.length;i++){
			for(int j=0;j<g_abstract.marginalSwitchDistance[i].length;j++){
				System.out.print(g_abstract.marginalSwitchDistance[i][j]+" ");
			}
			System.out.println();
		}*/
//		Graph g1=new Graph(GraphLinkFile,GraphNodeFile);
/*		long start=Calendar.getInstance().getTimeInMillis();//���ڲ���ϵͳTEʱ��
		g1.FlowRequestFileGenerate_2(10000);//�����������ļ��ĺ��������ϣ������֮ǰ���������ļ�������Ҫ���д˺���
		g1.run();
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("Run time :"+(double)(end-start)/1000);//���ڲ���ϵͳTEʱ��*/


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
