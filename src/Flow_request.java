import java.util.*;

/*�����������ڶ�ȡ���������ļ�������ÿһ��ת��Ϊһ��Flow_request����*/
//Flow Request�ļ��ĸ�ʽΪÿ����������Ϊһ�У��ֱ�Ϊ��1��Դ��ַ��2��Ŀ�ĵ�ַ��3������Ҫ��4���ӳ�Ҫ��5�����ȼ�,����֮���ÿո���ָ���


class Flow_request{
	public String src_id;
	public String dst_id;
	public float bandwidth_request;
	public int delay_request;
	public int priority;
	public boolean isSatisfied;
	public ArrayList<Link> AllocatedPath= new ArrayList<>();//�����洢TE�㷨֮������������·��
	public float min_bandwidth;//�����洢ʵʱ�����bottle-neck link�����ṩ�Ĵ���

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
	
	public void showAllocatedPath(){//��ӡlocalTE�㷨֮���������������·��
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