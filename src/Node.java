import java.util.ArrayList;

/*图中的每个节点*/
class Node{
		public String ID;
		public ArrayList<AdjInfo> adjcent_list=new ArrayList<AdjInfo>();//节点的邻接链表
		
		public Node(String id){//初始化节点信息
			this.ID=id;	
		}
		
		public void add_adj_node(AdjInfo id){//为节点天界邻接节点信息
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