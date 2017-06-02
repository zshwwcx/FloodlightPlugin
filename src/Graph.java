import java.util.*;
import java.io.*;

/*
��ȡlink�Լ�clusters�ļ�����Ϣ����ԭ�������˽ṹת��Ϊ��ҳ���������ڽ�����ṹ���Ա�����ִ���ⲿ��TE�㷨��
*/

/*Ŀǰؽ�����������
* ��1����ȡFLow Request�ļ��Ĺ����У����������Դ�ڵ㡢Ŀ�Ľڵ���clusters�ļ��е�����£��������лᱨ����Ҫ����쳣��׽��Ϣ
* ��2��
* ��3��
* */


public class Graph {
	ArrayList<Node> nodelist=new ArrayList<>();//�洢ͼ�����нڵ���Ϣ
	ArrayList<Link> linklist=new ArrayList<>();//�洢ͼ������link��Ϣ
	ArrayList<Flow_request> flowRequestList=new ArrayList<>();//�洢��Ҫ����TE������������

    public static String GraphNodeFile="F:\\java code\\src\\topo\\domain4";
    public static String GraphLinkFile="F:\\java code\\src\\topo\\links_tuple";
    public static String GraphFlowReuqestListFile="F:\\java code\\src\\flow_request\\NewFlowRequest.txt";
    int max_delay=10000000;//��ʾ�ӳٵ����ֵ������ʵ�����ʵ��ֵ���ı䣬��Ҫ��֤����path���ܵ��ӳ٣�һ��path������link���ӳ�֮��ҪС��max_delay��

	public Graph(String link_file_path,String clusters_file_path){//��ʼ��ͼ����ͨ��link��clusters�����ļ�������ͼ�ĳ�ʼ���������������ļ���String·������ʾ�����ں����޸ġ�
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
							//int index=nodelist.indexOf(tmp);
							//nodelist.get(index).add_adj_node(adj_new);
							tmp.add_adj_node(adj_new);
						}
					}
					this.linklist.add(new Link(initial_id,adj_id,adj_delay,adj_bandwidth));
					}
				read2.close();
			}
		}catch(Exception e){
			System.out.println("Error:��ȡ�ļ������г��ִ���");
			e.printStackTrace();
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
				matrix[start][end]=info.adj_delay;
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

		/*int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
		ArrayList<Integer> result=dijkstra_prototype(start);
		ArrayList<String> StringResult= new ArrayList<>();
		StringResult.clear();
		//result.add(getNum(dst));
		if(result.contains(end)){
			int end_index=result.indexOf(end);
			//result.add(0, start);
			for(int i=0;i<=end_index;i++){
				StringResult.add(getId(result.get(i)));
			}
			return StringResult;
		}
		else
			System.out.println("Can not find the path from "+src+" to the "+dst);
			return null;*/
	}


	public int getDistance(String src,String dst){//��ȡ��src����dst��·��������ӳ�
		int distance=0;
		ArrayList<String> path=this.getStringPath(src, dst);
		for(int i=0;i<path.size()-1;i++){
			distance+=this.getDirectDistance(path.get(i), path.get(i+1));
		}
		return distance;
	}

	public void collectFlowRequest(String FlowRequestFilePath){//��ȡ�������������󣬽�����뵽ͼ��flowRequestList�б��в���ʼ��,�ݶ�ÿ5minһ��
		//File�ĸ�ʽΪÿ����������Ϊһ�У��ֱ�Ϊ��1��Դ��ַ��2��Ŀ�ĵ�ַ��3������Ҫ��4���ӳ�Ҫ��5�����ȼ�,����֮���ÿո���ָ���
		this.flowRequestList.clear();//ִ���б��������ֹ�ϴ��ռ������е����ݲд��ڱ��ε��б��С�
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
			System.out.println("Error:��ȡ�����������ļ�����");
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
				if(key.AllocatedPath.size()!=0) {
					float band_width_temp = tmp.bandwidth * ((float) key.priority / sum_priority);
					if (band_width_temp <= key.min_bandwidth&&band_width_temp<=key.bandwidth_request) {
						key.min_bandwidth = band_width_temp;
						tmp.isAllocated = true;
					}
					else if(band_width_temp<=key.min_bandwidth&&band_width_temp>=key.bandwidth_request){
						key.min_bandwidth=key.bandwidth_request;
					}
				}//else{//������²���ת�Ƶ�topoUpdate�����н���
				//tmp.bandwidth-=band_width_temp;
				//if(tmp.bandwidth<=0){
				//	tmp.isAllocated=true;
				//}
			}
		}
		//Debug �ã���ӡ������ɺ�ÿ������������õķ�����·����

	}

	public void printResult(){
		for (Flow_request tm : this.flowRequestList) {
			if(tm.min_bandwidth==99999.0){
				tm.min_bandwidth=0;
			}
			//System.out.print(tm.src_id+" -> "+" "+tm.dst_id+" "+tm.AllocatedPath + " || Allocated Bandwidth: ");
			//System.out.println(tm.min_bandwidth);
//			printPath(tm.src_id,tm.dst_id);
			if(getStringPath(tm.src_id,tm.dst_id)!=null) {
				System.out.println(getStringPath(tm.src_id, tm.dst_id) + " || " + tm.min_bandwidth);
			}else{
				System.out.println("TE failed:Can not find a path from "+tm.src_id+" to "+tm.dst_id);
			}
			/*int[] tmp=dijkstra_prototype(getNum(tm.src_id));
			System.out.println(getNum(tm.src_id)+"===");
			for(int i=0;i<tmp.length;i++){
				System.out.print(tmp[i]+" ");
			}
			System.out.println();*/
		}
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

		for(Link link:this.linklist){
			if(link.isAllocated==true){
				this.removeLink(link);
			}
		}

	}
	
	
	public void run(){
		this.collectFlowRequest(GraphFlowReuqestListFile);
		this.localTE();
		this.printResult();
		//this.topologyUpdate();
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
	public void FlowRequestFileGenerate_1(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumberΪ�����������ļ�����Ҫ��������������Ŀ
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

	}

	/*
	*���������������ʽ2����ͼ�����ѡȡ�����㣬��ΪԴ�ڵ��Ŀ�Ľڵ㣬����ͼ����ͨ�Ժ�����
	 *  */
	public void FlowRequestFileGenerate_2(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumberΪ�����������ļ�����Ҫ��������������Ŀ
		Random rand=new Random(43);
		int FlowSize=this.nodelist.size();
		try {
			File file_out = new File(GraphFlowReuqestListFile);
			FileWriter file_write=new FileWriter(file_out,false);
			for (int i = 0; i < FlowRequestNumber; i++) {
				int nodeStartNumber=rand.nextInt(FlowSize);
				int nodeEndNumber=rand.nextInt(FlowSize);
				int bandwidthRequest=rand.nextInt(500);
				int delayRequest=rand.nextInt(500);
				int priorityRequest=rand.nextInt(10)+1;
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
		if(g1.getNode("00:00:00:00:00:00:00:0a")!=null){//����getNode����������
			System.out.println("Find the node.");
		}
		else
			System.out.println("Didn't find the node.\n\n");
		
		
		int[][] output_matrix=g1.constructDelayAdjMatrix();//�����ڽӾ���Ľ���������ɹ�
		for(int m=0;m<output_matrix.length;m++){
			System.out.print("[");
			for(int n=0;n<output_matrix[m].length;n++){
				System.out.print(output_matrix[m][n]+" ");
			}
			System.out.println("]\n");
		}
		
		ArrayList<Integer> out=g1.dijkstra_prototype(3);//���Ե�������У�·���㷨����Ч��
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
//		g1.FlowRequestFileGenerate_1(100);
		g1.FlowRequestFileGenerate_2(100);
		//g1.collectFlowRequest("E:\\����\\java\\FloodlightPlugin\\src\\Flow Request.txt");
	 	//g1.localTE();
	 	g1.run();
//		g1.test();
	 	//System.out.println("END NOW");

	 	/*
		for(Flow_request t:g1.flowRequestList){
			System.out.println(t);
		}
		*/
		
		
		//g1.localTE();
	}
	
	
}
