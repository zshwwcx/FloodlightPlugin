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
	//private final int V;//ͼ�Ľڵ���Ŀ
	//private int E;//ͼ�ı���Ŀ
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
	
	/*
	public int getV(){//��ȡ�ڵ���Ŀ
		return V;
	}
	
	public int getE(){//��ȡ�ߵ���Ŀ
		return E;
	}
	*/
	
	public void addNode(Node temp){//��ͼ����ӽڵ�
		this.nodelist.add(temp);
	}
	
	public void get_shortest_path(String src_point,String dst_point){//��ȡ��Դ�ڵ㵽Ŀ�Ľڵ���������·��������ķ������Ϳ�������һ�£�������Ҫ�������������·�
		
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
	public void deleteEdge(String src_point,String dst_point){
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
	}
	
}
