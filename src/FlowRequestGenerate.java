/**�������������ļ���ͨ�����ѡ��clusters�ļ��е����������ڵ���ΪĿ�ĺ��յ�ڵ㣬�ӳ�����ʹ���������������ɣ�
 * ���ΪFlow_request�ļ���������������ĸ�ʽ.
 * <Դ�ڵ��ַ Ŀ�Ľڵ��ַ �������� �ӳ����� ���ȼ�>
 * Created by haven on 2017/5/24.
 */

import java.util.*;
import java.io.*;

public class FlowRequestGenerate {
    public static String Flow_Request_File_path = "E:\\����\\java\\FloodlightPlugin\\src\\clusters_new_topo";//���˽ṹ�Ľڵ��ļ����������������������Դ�ڵ��Ŀ�Ľڵ�
    public static int Flow_Request_Number = 100;

    public static void main(String[] args) {
        int lineCount = 0;//��¼Flow_Request�ĵ���������������Ŀ

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
            FileOutputStream file_input = new FileOutputStream(new File("E:\\����\\java\\FloodlightPlugin\\src\\RandomFlowRequest"));
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
