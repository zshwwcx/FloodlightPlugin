import java.util.*;
import java.io.*;

/*
��ȡlink�Լ�clusters�ļ�����Ϣ����ԭ�������˽ṹת��Ϊ��ҳ���������ڽ�����ṹ���Ա�����ִ���ⲿ��TE�㷨��
*/


/*�ڽӽڵ�������Ϣ*/
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


/*ͼ�е�ÿ���ڵ�*/
class Node{
		public String ID;
		public ArrayList<adj_info> adjcent_list=new ArrayList<adj_info>();//�ڵ���ڽ�����
		
		public Node(String id){//��ʼ���ڵ���Ϣ
			this.ID=id;	
		}
		
		public void add_adj_node(adj_info id){//Ϊ�ڵ�����ڽӽڵ���Ϣ
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
	
	public Link(String start_id,String end_id,int link_delay){//��ʼ��Link��Ϣ
		this.start_switch=start_id;
		this.end_switch=end_id;
		this.delay=link_delay;
	}
	
	public Link(){
		this.start_switch="";
		this.end_switch="";
		this.delay=0;
	}
	
	public String getLinkStart(){//��ȡ��ǰLink��ǰ�ڵ㣬��Ϊÿ��link��������ģ���ĳ���ڵ㵽ĳ���ڵ㣬Link�Ŀ�ʼ����ǰ�ڵ㣬����ļ�Ϊ��ڵ�
		return this.start_switch;
	}
	
	public String getLinkEnd(){//��ȡ��ǰLink�ĺ�ڵ�
		return this.end_switch;
	}
	
	public int getLinkDelay(){
		return this.delay;
	}
	
	public void setStartSwitch(String str){//����ǰ�ڵ��id
		this.start_switch=str;
	}
	
	public void setEndSwitch(String str){//���ú�ڵ��id
		this.end_switch=str;
	}
	
	public void setLinkDelay(int delay_add){//����Link��delay
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
	ArrayList<Node> nodelist=new ArrayList<Node>();//�洢ͼ�����нڵ���Ϣ
	ArrayList<Link> linklist=new ArrayList<Link>();//�洢ͼ������link��Ϣ

	public Graph(String link_file_path,String clusters_file_path){//��ʼ��ͼ����ͨ��link��clusters�����ļ�������ͼ�ĳ�ʼ���������������ļ���String·������ʾ�����ں����޸ġ�
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
			for(adj_info temp:getNode(src).adjcent_list){
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
			int end=-1;
			for(adj_info info:node.adjcent_list){
				end=nodelist.indexOf(getNode(info.adj_id));
				matrix[start][end]=info.adj_delay;
			}
		}
		return matrix;
	}
	
	
	/*
	public int[][] constructBandwidthMatrix(){//����ͼ�Ĵ����������QoS�Լ�TE�㷨����û��д��
		int[][] bandwidth_matrix=new int[nodelist.size()][nodelist.size()];
			
		
		return bandwidth_matrix;
	}
	*/
	
	public ArrayList<Integer> Dijkstra_prototype(int start){//�Ͻ�˹�����㷨ԭ�ͣ��������Ϊ�����nodelist�е�index��ֱ�Ӹ����ڽӾ��������㵽ͼ�����нڵ�����·����
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
	
	public void printPath(String src,String dst){//��ӡ��src��dst�ڵ������������·���е����нڵ�
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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
	
	public ArrayList<Integer> getIndexPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��index
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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
	
	public ArrayList<String> getStringPath(String src,String dst){//�õ���src��dst���·������Ҫ���������нڵ��String
		int start=getNum(src);//��ȡsrc��nodelist�е��±�
		int end=getNum(dst);//��ȡdst��nodelist�е��±�
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
	
	
	public void DijkstarSP(Node src,Node dst){//������src�ڵ㵽dst�ڵ����̾���
		
	}
	
	public void DijkstarSP(String src,String dst){//�������·������������ֱ���ɽڵ���������������·��
		
	}
	
	public int getDistance(String src,String dst){//��ȡ��src����dst��·��������ӳ�
		int distance=0;
		ArrayList<String> path=this.getStringPath(src, dst);
		for(int i=0;i<path.size()-1;i++){
			distance+=this.getDirectDistance(path.get(i), path.get(i+1));
		}
		return distance;
	}
	
	
	public void allocateBandwidth(String src,String dst,int bandwidth_request,int delay_request){//������亯�����Դ�����ӳ���Ϊ�����׼
		
		
		this.getStringPath(src, dst);
		
	}
	
	public void TE(String FlowRequestFilePath){//File�ĸ�ʽΪÿ����������Ϊһ�У��ֱ�Ϊ��1��Դ��ַ��2��Ŀ�ĵ�ַ��3����������4������Ҫ��5���ӳ�Ҫ��6�����ȼ�,����֮���ÿո���ָ���
		
		
		
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
			System.out.println("Error:��ȡ�����������ļ�����");
			e.printStackTrace();
		}
	}
	
	/*
	public ArrayList<String> get_shortest_path(String src_point,String dst_point){//��ȡ��Դ�ڵ㵽Ŀ�Ľڵ���������·��(�����ӳ�������)������ķ������Ϳ�������һ�£�������Ҫ�������������·�
		Node src_node=getNode(src_point);
		ArrayList<String> shortest_path=new ArrayList<String>();//���ں��������src_point����dst_point��;�������нڵ�
		ArrayList<adj_info> copy_adjcent_list=new ArrayList<adj_info>();//����src_node���ڽ�������Ϣ�����ڴ洢��ǰ��֪��src�ɴ�ڵ��Լ������Ϣ
		copy_adjcent_list=(ArrayList<adj_info>)src_node.adjcent_list.clone();
		ArrayList<Node> copy_nodelist=new ArrayList<Node>();//����Graph�Ľڵ��б�����Dijkstra�㷨��ȫ��S����S����src����Ľڵ�����ɳڣ�relax��
		copy_nodelist=(ArrayList<Node>)this.nodelist.clone();
		Node temp=new Node("Null point");
		

		int min_distance=9999;//��ʼ����С����
		
		if((src_node!=null)&&copy_nodelist.contains(src_node)){
			copy_nodelist.remove(src_node);
			while(!copy_nodelist.isEmpty()){
				adj_info temp_info=new adj_info();
				for(adj_info info:src_node.adjcent_list){
					if(info.adj_delay<min_distance){
						min_distance=info.adj_delay;
						temp=getNode(info.adj_id);
						temp_info.adj_delay=info.adj_delay+getDirectDistance(src_point,temp_info.adj_id);//�ӳ�Ϊ��������link���е�delay֮��
						temp_info.adj_bandwidth=info.adj_bandwidth;//����Ӧ��Ϊ��������link���еĴ�����Сֵ�����˴�Ҫ��������·������ʱ��������
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
	
	
	public void addEdge(String src_point,String dst_point,int delay,int bandwidth){//��Ӵ�src��dst����·
		adj_info adj_to_add=new adj_info(dst_point,bandwidth,delay);
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_to_add);
			}
		}
	}
	
	public void addEdge(String src_point,adj_info adj_info_to_add){//addEdge��������
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=nodelist.indexOf(tmp);
				nodelist.get(index).add_adj_node(adj_info_to_add);
			}
		}
	}
	
	
	//ɾ��link�����ڴζ�·���ļ���
	public void RemoveLink(String src_point,String dst_point){
		for(Node tmp:this.nodelist){
			if(tmp.ID.equals(src_point)){
				int index=tmp.has_link_to(dst_point);
				nodelist.remove(index);
			}
		}
	}
	
	
	
	
	public static void main(String[] args){
		Graph g1=new Graph("C:\\Users\\haven\\OneDrive\\����\\SDN\\���˽ṹ\\links","C:\\Users\\haven\\OneDrive\\����\\SDN\\���˽ṹ\\clusters");
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
		
		ArrayList<Integer> out=g1.Dijkstra_prototype(3);//���Ե�������У�·���㷨����Ч��
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
