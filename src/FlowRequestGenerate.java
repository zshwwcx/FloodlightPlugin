/**流量需求生成文件，通过随机选择clusters文件中的任意两个节点作为目的和终点节点，延迟需求和带宽需求都先随机生成，
 * 输出为Flow_request文件，保存流量需求的格式.
 * <源节点地址 目的节点地址 带宽需求 延迟需求 优先级>
 * Created by haven on 2017/5/24.
 */

import java.util.*;
import java.io.*;

public class FlowRequestGenerate {
    public static String Flow_Request_File_path = "E:\\代码\\java\\FloodlightPlugin\\src\\clusters_new_topo";//拓扑结构的节点文件，用来产生数据流需求的源节点和目的节点
    public static int Flow_Request_Number = 100;

    public static void main(String[] args) {
        int lineCount = 0;//记录Flow_Request文档的数据流的总数目

        Random rand = new Random(47);
        try {
            String encoding = "utf-8";
            File file_in1 = new File(Flow_Request_File_path);
            if (file_in1.isFile() && file_in1.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file_in1), encoding);
                BufferedReader bufferReader = new BufferedReader(read);
                while (bufferReader.readLine() != null) {
                    lineCount++;
                }
                read.close();
            }
            FileInputStream file_output = new FileInputStream(new File(Flow_Request_File_path));
            FileOutputStream file_input = new FileOutputStream(new File("E:\\代码\\java\\FloodlightPlugin\\src\\RandomFlowRequest"));
            for (int i = 0; i < Flow_Request_Number; i++) {
                int line_num = rand.nextInt(lineCount);
                for (int m = 0; m < line_num; m++) {
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file_in1), encoding);
                    BufferedReader bufferReader = new BufferedReader(read);
                    while (bufferReader.readLine() != null) {
                        m++;
                    }
                }

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
