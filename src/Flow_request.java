import java.util.ArrayList;

/*流量需求，用于读取流量需求文件并将其每一项转换为一个Flow_request对象*/

class Flow_request{
	public String src_id;
	public String dst_id;
	public float bandwidth_request;
	public int delay_request;
	public int priority;
	public boolean isSatisfied;
	public ArrayList<Link> AllocatedPath=new ArrayList<Link>();//用来存储TE算法之后控制器分配的路径以及带宽
	
	public Flow_request(String src,String dst,int bandwidth,int delay,int priority){
		this.src_id=src;
		this.dst_id=dst;
		this.bandwidth_request=bandwidth;
		this.isSatisfied=false;
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
	
}