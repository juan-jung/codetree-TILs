import java.io.*;
import java.util.*;

public class Main {
    static int N,M,H,K;
    static int[] dx = {0,1,-1,0}, dy = {1,0,0,-1}; //우하상좌
    static int[] sdx = {-1,0,1,0}, sdy = {0,1,0,-1}; //상우하좌
    static class Runner {
        int d; //방향
        int last_moved_turn = 0;
        Runner next; //같은 칸 다른 사람

        public Runner(int d, int last_moved_turn, Runner next) {
            this.d = d;
            this.last_moved_turn = last_moved_turn;
            this.next = next;
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        N = Integer.parseInt(st.nextToken());//격자크기 5-99
        M = Integer.parseInt(st.nextToken());//도망자 수 1-n^2
        H = Integer.parseInt(st.nextToken());//나무 수 1-ㅜ^2
        K = Integer.parseInt(st.nextToken());//턴수 1-100

        int[] S = new int[4]; // 술래 정보 0:x, 1:y, 2:방향, 3: 0-(중앙)->(0,0) 1-(0,0)->중앙
        S[0] = N/2;
        S[1] = N/2;
        S[2] = 0;

        int[][] path = new int[N][N];
        makePath(S[0], S[1], 1, 0, path);

        Runner[][] map = new Runner[N][N];
        for(int m=1;m<=M;m++) {
            st = new StringTokenizer(br.readLine()," ");
            int x = Integer.parseInt(st.nextToken())-1;
            int y = Integer.parseInt(st.nextToken())-1;
            int d = Integer.parseInt(st.nextToken())-1; // 1: 좌우 2:상하 초기는 우하
            Runner runner = new Runner(d, 0,null);
            map[x][y] = runner;
        }

        boolean[][] treeMap = new boolean[N][N];
        for(int h=0;h<H;h++) {
            st = new StringTokenizer(br.readLine()," ");
            int x = Integer.parseInt(st.nextToken())-1;
            int y = Integer.parseInt(st.nextToken())-1;
            treeMap[x][y] = true;
        }

        int turn = 1;
        int ans = 0;
        while(turn <= K) {
            //도망자 이동
            runAll(S,map,turn);

            //술래 이동
            moveS(S,path);
            ans += caught(S,map,treeMap,turn);
            turn++;
        }

        System.out.println(ans);
        br.close();
    }

    static int caught(int[] S, Runner[][] map, boolean[][] treeMap, int turn) {
        int cnt = 0;
        for(int i=0;i<3;i++) {
            int x = S[0] + sdx[S[2]]*i;
            int y = S[1] + sdy[S[2]]*i;
            if(x<0||x>N-1||y<0||y>N-1) break;
            if(treeMap[x][y]) continue;
            while(map[x][y]!=null) {
                cnt++;
                map[x][y] = map[x][y].next;
            }
        }
        return cnt * turn;
    }
    static void moveS(int[] S, int[][] path) {
        //이동
        S[0] += sdx[S[2]];
        S[1] += sdy[S[2]];

        //방향 결정
        if(S[0]==0&&S[1]==0) {
            S[2] = 2;
            S[3] = 1;
        }
        else if(S[0]==N/2&&S[1]==N/2) {
            S[2] = 0;
            S[3] = 0;
        }
        else {
            int plusOne = -1;
            int minusOne = -1;
            for(int d=0;d<4;d++) {
                int nx = S[0] + sdx[d];
                int ny = S[1] + sdy[d];
                if(nx<0||nx>N-1||ny<0||ny>N-1) continue;
                if(path[S[0]][S[1]]-1==path[nx][ny]) minusOne = d;
                else if(path[S[0]][S[1]]+1==path[nx][ny]) plusOne = d;
            }
            S[2] = S[3] == 0 ? plusOne : minusOne;
        }

    }

    static int getDist(int x1, int y1, int x2, int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    static void runAll(int[] S, Runner[][] map, int turn) {
        //술래로 부터 거리3 이내 좌표 검사
        ArrayDeque<int[]> q = new ArrayDeque<>();
        boolean[][] v = new boolean[N][N];
        q.offer(new int[]{S[0],S[1]});
        while(!q.isEmpty()) {
            int[] cur = q.poll();

            Runner curRunner = map[cur[0]][cur[1]];
            while(curRunner!=null && curRunner.last_moved_turn < turn) {
                map[cur[0]][cur[1]] = curRunner.next;
                curRunner.next = null;
                curRunner.last_moved_turn = turn;

                int x = cur[0] + dx[curRunner.d];
                int y = cur[1] + dy[curRunner.d];
                //범위를 벗어난 경우 방향을 반대로 틀고, 한 칸 이동
                if(x<0||x>N-1||y<0||y>N-1) {
                    curRunner.d = 3-curRunner.d;
                    x = cur[0] + dx[curRunner.d];
                    y = cur[1] + dy[curRunner.d];
                }
                //술래가 있다면 제자리
                if(x==S[0]&&y==S[1]) {
                    x=cur[0];
                    y=cur[1];
                }

                //이동, 움직이려는 칸이 비어있으면 바로 삽입 아니라면 꼬리에 붙인다
                if(map[x][y]==null) map[x][y] = curRunner;
                else {
                    Runner tail = map[x][y];
                    while(tail.next!=null) {
                        tail = tail.next;
                    }
                    tail.next = curRunner;
                }

                curRunner = map[cur[0]][cur[1]];
            }

            for(int d=0;d<4;d++) {
                int nx = cur[0] + dx[d];
                int ny = cur[1] + dy[d];
                if(nx<0||nx>N-1||ny<0||ny>N-1||v[nx][ny]||getDist(nx,ny,S[0],S[1])>3) continue;
                q.offer(new int[]{nx,ny});
                v[nx][ny] = true;
            }
        }
    }

    static void makePath(int x, int y, int num, int dir, int[][] path) {
        if(x==-1 || path[x][y]!=0) return;

        path[x][y] = num;
        int nx = x + sdx[dir];
        int ny = y + sdy[dir];

        int cnt = 0 ;
        for(int d=0;d<4;d++) {
            int nnx = nx + dx[d];
            int nny = ny + dy[d];
            if(nnx<0||nnx>N-1||nny<0||nny>N-1||path[nnx][nny]==0) cnt++;
        }

        if(cnt==3) makePath(nx,ny,num+1,(dir+1)%4, path);
        else makePath(nx, ny, num+1, dir, path);
    }
}