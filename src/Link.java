import java.util.*;

class Link{
	public String start_switch;
	public String end_switch;
	public String outport;
	public String inport;
	public int delay;
	public float bandwidth;
	public float initial_bandwidth;
	public boolean isAllocated;
	public HashMap<Flow_request,Float> allocated_bandwidth=new HashMap<Flow_request,Float>();
	
	public Link(String start_id,String out_port,String end_id,String in_port,int link_delay,int bandwidth){//初始化Link信息
		this.start_switch=start_id;
		this.outport=out_port;
		this.end_switch=end_id;
		this.inport=in_port;
		this.delay=link_delay;
		this.bandwidth=bandwidth;
		this.initial_bandwidth=bandwidth;
		this.isAllocated=false;
		allocated_bandwidth.clear();
	}
	
	public Link(){
		this.start_switch="";
		this.outport="0";
		this.end_switch="";
		this.inport="0";
		this.delay=0;
		this.bandwidth=0;
		this.initial_bandwidth=0;
		this.isAllocated=false;
		allocated_bandwidth.clear();
	}
	
	public String getLinkStart(){//获取当前Link的前节点，因为每条link都是有序的，从某个节点到某个节点，Link的开始就是前节点，后面的即为后节点
		return this.start_switch;
	}
	
	public String getLinkEnd(){//获取当前Link的后节点
		return this.end_switch;
	}
	
	public int getLinkDelay(){
		return this.delay;
	}
	
	public void setStartSwitch(String str){//设置前节点的id
		this.start_switch=str;
	}
	
	public void setEndSwitch(String str){//设置后节点的id
		this.end_switch=str;
	}
	
	public void setLinkDelay(int delay_add){//设置Link的delay
		this.delay=delay_add;
	}

	public String toString(){
		return this.start_switch+" "+this.outport+" "+this.end_switch+" "+this.inport+" "+this.delay+" "+this.bandwidth+"\n";
	}
	
}
