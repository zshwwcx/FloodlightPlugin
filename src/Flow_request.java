import java.util.*;

/*流量需求，用于读取流量需求文件并将其每一项转换为一个Flow_request对象*/
//Flow Request文件的格式为每条流的请求为一行，分别为（1）源地址（2）目的地址（3）带宽要求（4）延迟要求（5）优先级,变量之间用空格符分隔开


class Flow_request{
	public String fr_id;
	public String src_id;
	public int src_domain;//源节点所在的域
	public String dst_id;
	public int dst_domain;//目的节点所在的域
	public boolean cross_domain_flag;//是否是跨域数据流请求的标识
	public float bandwidth_request;
	public int delay_request;
	public int priority;
	public boolean isSatisfied;
	public ArrayList<Link> AllocatedPath= new ArrayList<>();//用来存储TE算法之后控制器分配的路径
	public float min_bandwidth;//用来存储实时分配的bottle-neck link所能提供的带宽
	public int min_delay;
	public static int default_priority=1;
	
	
	public Flow_request(String id,String src,String dst,float bandwidth,int delay,int priority){
		this.fr_id=id;
		this.src_id=src;
		this.dst_id=dst;
		this.bandwidth_request=bandwidth;
		this.delay_request=delay;
		this.isSatisfied=false;
		this.min_bandwidth=99999;
		this.min_delay=99999;
		this.priority=priority;

		String src_id_substring=src_id.substring(18,20)+src_id.substring(21);
		int temp1=Integer.parseInt(src_id_substring,16);
		if(temp1>=1&&temp1<=150){
			src_domain=1;
		}else if(temp1>=151&&temp1<=286){
			src_domain=2;
		}else if(temp1>=287&&temp1<=427){
			src_domain=3;
		}else if(temp1>=428&&temp1<=568){
			src_domain=4;
		} else if(temp1>=569&&temp1<=717){
			src_domain=5;
		}else{
			src_domain=0;
		}
		String dst_id_substring=dst_id.substring(18,20)+dst_id.substring(21);
		int temp2=Integer.parseInt(dst_id_substring,16);
		if(temp2>=1&&temp2<=150){
			dst_domain=1;
		}else if(temp2>=151&&temp2<=286){
			dst_domain=2;
		}else if(temp2>=287&&temp2<=427){
			dst_domain=3;
		}else if(temp2>=428&&temp2<=568){
			dst_domain=4;
		} else if(temp2>=569&&temp2<=717){
			dst_domain=5;
		}else{
			dst_domain=0;
		}

		if(src_domain==dst_domain){
			this.cross_domain_flag=false;
		}else{
			this.cross_domain_flag=true;
		}

	}
	
	public Flow_request(){
		this.fr_id="";
		this.src_id="";
		this.src_domain=0;
		this.dst_id="";
		this.dst_domain=0;
		this.cross_domain_flag=false;
		this.bandwidth_request=0;
		this.isSatisfied=false;
		this.delay_request=99999;
		this.min_bandwidth=99999;
		this.min_delay=99999;
		this.priority=default_priority;
	}

	public void setFrId(String id){

		this.fr_id=id;
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
	
	public void setIssatisfied(boolean bl)
	{
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
	
	/*public String toString(){
		String return_string;
		if(!this.AllocatedPath.isEmpty()) {
			return_string = this.src_id + "=>" + this.dst_id + " || Bandwidth Request:" + this.bandwidth_request + " || Allocated bandwidth:" + this.min_bandwidth;
		}else{
			return_string="Sorry: Can not find a valid path from the source node to the destination node.";
		}
		return return_string;
		
	}*/
}