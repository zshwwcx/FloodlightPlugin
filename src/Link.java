import java.util.*;

class Link{
	public String start_switch;
	public String end_switch;
	public String outport;
	public String inport;
	public int delay;
	public float bandwidth;
	boolean isAllocated;
	public HashMap<Flow_request,Float> allocated_bandwidth=new HashMap<Flow_request,Float>();
	
	public Link(String start_id,String out_port,String end_id,String in_port,int link_delay,int bandwidth){//��ʼ��Link��Ϣ
		this.start_switch=start_id;
		this.outport=out_port;
		this.end_switch=end_id;
		this.inport=in_port;
		this.delay=link_delay;
		this.bandwidth=bandwidth;
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
		this.isAllocated=false;
		allocated_bandwidth.clear();
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

	public String toString(){
		return this.start_switch+"=>"+this.end_switch;
	}
	
}
