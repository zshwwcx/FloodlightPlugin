import java.util.*;

/*流量需求，用于读取流量需求文件并将其每一项转换为一个Flow_request对象*/
//Flow Request文件的格式为每条流的请求为一行，分别为（1）源地址（2）目的地址（3）带宽要求（4）延迟要求（5）优先级,变量之间用空格符分隔开


class Flow_request{
	public String src_id;
	public String dst_id;
	public float bandwidth_request;
	public int delay_request;
	public int priority;
	public boolean isSatisfied;
	public ArrayList<Link> AllocatedPath= new ArrayList<>();//用来存储TE算法之后控制器分配的路径
	public float min_bandwidth;//用来存储实时分配的bottle-neck link所能提供的带宽

	public static int default_priority=1;
	
	
	public Flow_request(String src,String dst,int bandwidth,int delay,int priority){
		this.src_id=src;
		this.dst_id=dst;
		this.bandwidth_request=bandwidth;
		this.isSatisfied=false;
		this.min_bandwidth=99999;
		this.priority=priority;
	}
	
	public Flow_request(){
		this.src_id="";
		this.dst_id="";
		this.bandwidth_request=0;
		this.isSatisfied=false;
		this.min_bandwidth=99999;
		this.priority=default_priority;
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
	
	public void setIssatisfied(boolean bl){
		this.isSatisfied=bl;
	}
	public String getSrcId(){
		return this.src_id;
	}
	
	public String getDstId(){
		return this.dst_id;
	}
	
	public float getBandwidthRequest(){
		return this.bandwidth_request;
	}
	
	public int getDelayRequest(){
		return this.delay_request;
	}
	
	public int getPriority(){
		return this.priority;
	}
	
	public void showAllocatedPath(){//打印localTE算法之后，数据流被分配的路径
		System.out.print(this.AllocatedPath.get(0).start_switch);
		for(Link tmp:this.AllocatedPath){
			System.out.print("=>"+tmp.end_switch);
			
		}
		System.out.println();
	}
	
	public String toString(){
		String return_string;
		if(!this.AllocatedPath.isEmpty()) {
			return_string = this.src_id + "=>" + this.dst_id + " || Bandwidth Request:" + this.bandwidth_request + " || Allocated bandwidth:" + this.min_bandwidth;
		}else{
			return_string="Sorry: Can not find a valid path from the source node to the destination node.";
		}
		return return_string;
		
	}
}