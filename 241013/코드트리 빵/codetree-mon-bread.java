import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        int n = Integer.parseInt(st.nextToken()); //격자
        int m = Integer.parseInt(st.nextToken()); //빵구하고자하는인원
        boolean[][] map = new boolean[n][n];
        int[][] baseCamp = new int[n][n];
        for(int i=0;i<n;i++) {
            st = new StringTokenizer(br.readLine(), " ");
            for(int j=0;j<n;j++) {
                baseCamp[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int[][] store = new int[m][2];
        for(int i=0;i<m;i++) {
            st = new StringTokenizer(br.readLine(), " ");
            store[i][0] = Integer.parseInt(st.nextToken())-1;
            store[i][1] = Integer.parseInt(st.nextToken())-1;
        }

        int time = -1;
        boolean[] arrived = new boolean[m];
        int[][] people = new int[m][2];
        int[] dx = {-1,0,0,1}, dy = {0,-1,1,0};
        int arrivedCnt = 0;
        while(++time>=0) {
            //1. 격자안 사람들 이동
            // 가고 싶은 편의점 방향. 최단거리 > 상좌우하
            for(int i=0;i<Math.min(time,m);i++) {
                if(arrived[i]) continue;
                ArrayDeque<int[]> q = new ArrayDeque<>();
                boolean[][] v= new boolean[n][n];
                v[people[i][0]][people[i][1]] = true;
                for(int d=0;d<4;d++) {
                    int nx = people[i][0] + dx[d];
                    int ny = people[i][1] + dy[d];
                    if(nx<0||nx>n-1||ny<0||ny>n-1||map[nx][ny]) continue;
                    q.offer(new int[]{nx,ny,d});
                }
                
                while(!q.isEmpty()) {
                    int[] cur = q.poll();

                    if(cur[0]==store[i][0] && cur[1]==store[i][1]) {
                        people[i][0] += dx[cur[2]];
                        people[i][1] += dy[cur[2]];
                        break;
                    }

                    if(v[cur[0]][cur[1]]) continue;
                    v[cur[0]][cur[1]] = true;

                    for(int d=0;d<4;d++) {
                        int nx = cur[0] + dx[d];
                        int ny = cur[1] + dy[d];
                        if(nx<0||nx>n-1||ny<0||ny>n-1||map[nx][ny]) continue;
                        q.offer(new int[]{nx,ny,cur[2]});
                    }
                }
            }

            //2. 편의점 도착 확인
            for(int i=0;i<Math.min(m,time);i++) {
                if(arrived[i]) continue;
                if(people[i][0]==store[i][0] && people[i][1]==store[i][1]) {
                    arrivedCnt++;
                    map[people[i][0]][people[i][1]] = true;
                    arrived[i] = true;
                }
            }

            //도착한 사람 수 확인
            if(arrivedCnt == m) break;

            //3. 베이스캠프 떨구기
            if(time < m) {
                //편의점과 가장 가까운 베이스 캠프 찾기
                ArrayDeque<int[]> q = new ArrayDeque<>();
                q.offer(new int[]{store[time][0], store[time][1],0});
                boolean[][] v = new boolean[n][n];

                while(!q.isEmpty()) {
                    int[] cur = q.poll();

                    if(baseCamp[cur[0]][cur[1]]==1) {
                        while(!q.isEmpty()) {
                            int[] temp = q.poll();
                            if(temp[2]!=cur[2]) break;
                            if(baseCamp[temp[0]][temp[1]]!=1) continue;
                            if((temp[0] < cur[0]) || (temp[0]==cur[0] && temp[1] < cur[1])) cur = temp;
                         }
                        people[time][0] = cur[0];
                        people[time][1] = cur[1];
                        baseCamp[cur[0]][cur[1]] = 0;
                        map[cur[0]][cur[1]] = true;
                        break;
                    }

                    if(v[cur[0]][cur[1]]) continue;
                    v[cur[0]][cur[1]] = true;

                    for(int d=0;d<4;d++) {
                        int nx = cur[0] + dx[d];
                        int ny = cur[1] + dy[d];
                        if(nx<0||nx>n-1||ny<0||ny>n-1||map[nx][ny]) continue;
                        q.offer(new int[]{nx,ny,cur[2]+1});
                    }
                }

            }

        }

        System.out.println(time+1);
     }
}