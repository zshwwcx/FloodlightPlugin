
/*邻接节点链表信息*/
class AdjInfo{
		public String adj_id;
		public int adj_bandwidth;
		public int adj_delay;
		
		public AdjInfo(String id,int bw,int dl){
			this.adj_id=id;
			this.adj_bandwidth=bw;
			this.adj_delay=dl;
		}
		
		public AdjInfo(){
			this.adj_id="ff:ff:ff:ff:ff:ff:ff:ff";
			this.adj_bandwidth=99999;
			this.adj_delay=99999;
		}
		
}