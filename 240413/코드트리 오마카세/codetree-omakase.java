import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        int L = Integer.parseInt(st.nextToken());// 3 - 10억
        int Q = Integer.parseInt(st.nextToken());// 1-10만
        HashMap<String, ArrayList<int[]>> sushiMap = new HashMap<>();
        HashMap<String, int[]> guestMap = new HashMap<>();
        int total_sushi_cnt = 0;
        int total_guest_cnt = 0;
        StringBuilder sb = new StringBuilder();
        for(int q=0;q<Q;q++) {
            st = new StringTokenizer(br.readLine()," ");
            int op = Integer.parseInt(st.nextToken());
            int t = Integer.parseInt(st.nextToken());
            if(op==100) {
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                ArrayList<int[]> info = sushiMap.getOrDefault(name, new ArrayList<int[]>());
                info.add(new int[]{t,x});
                sushiMap.put(name, info);
                total_sushi_cnt++;
            }
            else if(op==200) {
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                int n = Integer.parseInt(st.nextToken());
                guestMap.put(name, new int[]{t,x,n});
                total_guest_cnt++;
            }
            else if(op==300) {
                //guest 순회하며 먹임
                Set<String> keytSet = guestMap.keySet();
                for(String key : keytSet) {
                    int[] guestInfo = guestMap.get(key);
                    int gtime = guestInfo[0];
                    int gX = guestInfo[1];
                    int leftSushi = guestInfo[2];
                    if(leftSushi == 0) continue;

                    ArrayList<int[]> sushis = sushiMap.getOrDefault(key, new ArrayList<int[]>());
                    Iterator iter = sushis.iterator();
                    while(iter.hasNext()) {
                        // t x
                        int[] sushi = (int[])iter.next();
                        //사람이먼저왔으면 x가 초기위치 , 아니면 시간차이만큼 이동한 자리가 초기위치
//                        long sushiStartX = (sushi[1] + (gtime > sushi[0] ? gtime-sushi[0] : 0))%L;
//                        long sushiMEndX = sushiStartX + t - (gtime > sushi[0] ? gtime : sushi[0]);

                        long sushiStartX = ((long) sushi[1] + (gtime > sushi[0] ? (long) gtime - (long) sushi[0] : 0)) % L;
                        long sushiMEndX = sushiStartX + (long) t - (gtime > sushi[0] ? (long) gtime : (long) sushi[0]);

                        if((gX>=sushiStartX && gX<=sushiMEndX) || gX<=(sushiMEndX%L) || sushiMEndX-sushiStartX >=L-1) {
                            total_sushi_cnt--;
                            iter.remove();
                            leftSushi--;
                        }
                    }
                    if(leftSushi == 0) total_guest_cnt--;
                    guestMap.put(key,new int[]{gtime, gX, leftSushi});
                }
                sb.append(total_guest_cnt + " " + total_sushi_cnt + "\n");
            }
        }
        System.out.println(sb);
    }
}