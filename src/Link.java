
class Link{
	public String start_switch;
	public String end_switch;
	public int delay;
	
	public Link(String start_id,String end_id,int link_delay){//��ʼ��Link��Ϣ
		this.start_switch=start_id;
		this.end_switch=end_id;
		this.delay=link_delay;
	}
	
	public Link(){
		this.start_switch="";
		this.end_switch="";
		this.delay=0;
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
	
}
