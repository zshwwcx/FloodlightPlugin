
class Link{
	public String start_switch;
	public String end_switch;
	public int delay;
	
	public Link(String start_id,String end_id,int link_delay){//初始化Link信息
		this.start_switch=start_id;
		this.end_switch=end_id;
		this.delay=link_delay;
	}
	
	public Link(){
		this.start_switch="";
		this.end_switch="";
		this.delay=0;
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
	
}
