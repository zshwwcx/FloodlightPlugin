import java.util.ArrayList;

/*�����������ڶ�ȡ���������ļ�������ÿһ��ת��Ϊһ��Flow_request����*/

class Flow_request{
	private String src_id;
	private String dst_id;
	private int bandwidth_request;
	private int delay_request;
	private int priority;
	private ArrayList<Node> AllocatedPath=new ArrayList<Node>();//�����洢TE�㷨֮������������·���Լ�����
	
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