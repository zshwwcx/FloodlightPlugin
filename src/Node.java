import java.util.ArrayList;

/*ͼ�е�ÿ���ڵ�*/
class Node{
		public String ID;
		public ArrayList<AdjInfo> adjcent_list=new ArrayList<AdjInfo>();//�ڵ���ڽ�����
		
		public Node(String id){//��ʼ���ڵ���Ϣ
			this.ID=id;	
		}
		
		public void add_adj_node(AdjInfo id){//Ϊ�ڵ�����ڽӽڵ���Ϣ
			this.adjcent_list.add(id);
		}
		
		public int has_link_to(String dst_node){
			for(AdjInfo node_temp :adjcent_list){
				if(node_temp.adj_id.equals(dst_node)){
					return adjcent_list.indexOf(node_temp);
				}
			}
			return 0;
		}
		
		
}