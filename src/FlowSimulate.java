/**数据流仿真所需的转换函数，输入文件input_file为数据流请求文件，输出文件为mininet中添加的函数所需要的输入文件，输入文件的格式为ping原node的名字，目的node的名字，Ping所需要的带宽
 * 带宽部分还需进行处理，将其转换为ping指令中-c后面的次数。
 * Created by haven on 2017/7/8.
 */
import java.util.*;
import java.io.*;


public class FlowSimulate {
    public void filetranslate(){
        try{
            File input_file=new File("F:\\java code\\src\\flow_request\\NewFlowRequest.txt");
            File output_file = new File("F:\\java code\\src\\flow_request\\FlowSimulate.txt");
            FileWriter file_write=new FileWriter(output_file,false);
            if(input_file.isFile()&&input_file.exists()){
                InputStreamReader read=new InputStreamReader(new FileInputStream(input_file));
                BufferedReader bufferReader=new BufferedReader(read);
                String lineTxt;
                while((lineTxt=bufferReader.readLine())!=null){
                    String[] info_temp=lineTxt.split(" ");
                    String str1=info_temp[1].substring(18,20)+info_temp[1].substring(21);
                    int node1=Integer.parseInt(str1,16);
                    String str2=info_temp[2].substring(18,20)+info_temp[2].substring(21);
                    int node2=Integer.parseInt(str2,16);
                    String node1_name="s"+node1;
                    String node2_name="s"+node2;
                    String bd=info_temp[3];
                    String contents=node1_name+" "+node2_name+" "+bd+"\n";
                    file_write.write(contents);
                }
                read.close();
            }
            file_write.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
        FlowSimulate t=new FlowSimulate();
        t.filetranslate();
    }

}
