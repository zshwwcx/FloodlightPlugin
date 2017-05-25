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

    public static String GraphNodeFile="E:\\����\\java\\FloodlightPlugin\\src\\clusters_new_topo";
    public static String GraphLinkFile="E:\\����\\java\\FloodlightPlugin\\src\\links_new_topo";
    public static String GraphFlowReuqestListFile="E:\\����\\java\\FloodlightPlugin\\src\\NewFlowRequest.txt";

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
							int index=nodelist.indexOf(tmp);
							nodelist.get(index).add_adj_node(adj_new);
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
		int max_distance=100000;

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

	public ArrayList<Integer> dijkstra_prototype(int start){//�Ͻ�˹�����㷨ԭ�ͣ��������Ϊ�����nodelist�е�index��ֱ�Ӹ����ڽӾ��������㵽ͼ�����нڵ�����·����
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



	public void printPath(String src,String dst){//��ӡ��src��dst�ڵ������������·���е����нڵ�
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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

	public ArrayList<Integer> getIndexPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��index
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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

	public ArrayList<String> getStringPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��String
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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
				}//else{//������²���ת�Ƶ�topoUpdate�����н���
				//tmp.bandwidth-=band_width_temp;
				//if(tmp.bandwidth<=0){
				//	tmp.isAllocated=true;
				//}
			}
		}



			//Debug �ã���ӡ������ɺ�ÿ������������õķ�����·����
		for (Flow_request tm : this.flowRequestList) {
			System.out.print(tm.AllocatedPath + " || Allocated Bandwidth: ");
			System.out.println(tm.min_bandwidth);
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
		//this.topologyUpdate();
	}


	public void FlowRequestFileGenerate(int FlowRequestNumber) throws FileNotFoundException {//FlowRequestNumberΪ�����������ļ�����Ҫ��������������Ŀ
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
		g1.FlowRequestFileGenerate(100);
		//g1.collectFlowRequest("E:\\����\\java\\FloodlightPlugin\\src\\Flow Request.txt");
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
