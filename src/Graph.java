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

public class Graph {
	ArrayList<Node> nodelist=new ArrayList<Node>();//�洢ͼ�����нڵ���Ϣ

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
	
	public ArrayList<Node> get_shortest_path(String src_point,String dst_point){//��ȡ��Դ�ڵ㵽Ŀ�Ľڵ���������·��������ķ������Ϳ�������һ�£�������Ҫ�������������·�
		Node src_node=getNode(src_point);
		ArrayList<Node> shortest_path=new ArrayList<Node>();
		ArrayList<adj_info> copy_adjcent_list=new ArrayList<adj_info>();
		copy_adjcent_list.addAll(src_node.adjcent_list);//����src_node���ڽ�������Ϣ������Dijkstra�㷨
		int min_distance=9999999;//��ʼ����С����
		
		if(src_node!=null){
			while(!copy_adjcent_list.isEmpty()){
				for(adj_info info:src_node.adjcent_list){
					if(info.adj_delay<min_distance){
						min_distance=info.adj_delay;
					}
				}
			}
		}
		
		return shortest_path;
	} 
	
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
		if(g1.getNode("00:00:00:00:00:00:00:07")!=null){//����getNode����������
			System.out.println("Find the node.");
		}
		else
			System.out.println("Didn't find the node");
	}
	
}
