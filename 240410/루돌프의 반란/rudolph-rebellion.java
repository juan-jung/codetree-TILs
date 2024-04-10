import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws  Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        int N = Integer.parseInt(st.nextToken()); // 게임판의 크기, 3<=N<=50
        int M = Integer.parseInt(st.nextToken()); // 턴수 1<=M=<1000
        int P = Integer.parseInt(st.nextToken()); // 산타의 수 1-30
        int C = Integer.parseInt(st.nextToken()); // 루돌프의 힘 1-N
        int D = Integer.parseInt(st.nextToken()); // 산타의 힘 1-N
        int[][] map = new int[N][N];
        int[] R = new int[2]; // 루돌프 위치
        st = new StringTokenizer(br.readLine()," ");
        R[0] = Integer.parseInt(st.nextToken())-1;
        R[1] = Integer.parseInt(st.nextToken())-1;
        int[][] S = new int[P+1][5]; // 0:산타r, 1:산타c, 2:점수, 3:기절턴수(2이면 2턴부터 활동가능), 4:탈락여부(0,1)
        for(int i=0;i<P;i++) {
            st = new StringTokenizer(br.readLine()," ");
            int num = Integer.parseInt(st.nextToken());
            int sr = Integer.parseInt(st.nextToken())-1;
            int sc = Integer.parseInt(st.nextToken())-1;
            S[num][0] = sr;
            S[num][1] = sc;
            map[sr][sc] = num;
            S[num][3] = -1;
        }

        int turn = 0;
        int santa_surv = P;
        //상우하좌 상좌 상우 하좌 하우
        int[] dx = {-1,0,1,0,-1,-1,1,1};
        int[] dy = {0,1,0,-1,-1,1,-1,1};

        while(turn < M) { // 240 * 120 * 1000 = 28,800,000

            //test
//            System.out.println((turn+1) + " : before");
//            for(int i=0;i<N;i++) {
//                for(int j=0;j<N;j++) {
//                    if(i==R[0] && j==R[1]) System.out.print("R ");
//                    else System.out.print(map[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println();
            //test

            //1. 루돌프 이동
            int dist = Integer.MAX_VALUE;
            int pr = -1;
            int pc = -1;
            for(int p=1;p<=P;p++) {
                if(S[p][4] == 1) continue;
                int cal_dist = getDist(R[0],R[1],S[p][0], S[p][1]);
                if(cal_dist < dist) {
                    dist = cal_dist;
                    pr = S[p][0];
                    pc = S[p][1];
                }
                else if(cal_dist == dist) {
                    if(S[p][0] > pr) {
                        dist = cal_dist;
                        pr = S[p][0];
                        pc = S[p][1];
                    }
                    else if(S[p][0]==pr) {
                        if(S[p][1] > pc) {
                            dist = cal_dist;
                            pr = S[p][0];
                            pc = S[p][1];
                        }
                    }
                }
            }

            int min_dist = Integer.MAX_VALUE;
            int dir = 0;
            int movex = 0;
            int movey = 0;
            for(int d=0;d<8;d++) {
                int nx = R[0] + dx[d];
                int ny = R[1] + dy[d];
                if(nx<0||nx>N-1||ny<0||ny>N-1) continue;
                int cal_dist = getDist(pr,pc,nx,ny);
                if(cal_dist < min_dist) {
                    min_dist = cal_dist;
                    movex = nx;
                    movey = ny;
                    dir = d;
                }
            }
            R[0] = movex;
            R[1] = movey;

            //산타 충돌 확인 ,루돌프가 산타를 밀 경우 한 방향으로만 밀리기 때문에 밀리거나 떨어지거나 둘 중 하나
            int santa_idx = map[R[0]][R[1]];
            if(santa_idx!=0) {
                //산타 점수 올리기
                S[santa_idx][2] += C;
                //산타 기절
                S[santa_idx][3] = turn + 1;
                //산타 밀기
                int nx = S[santa_idx][0] + dx[dir]*C;
                int ny = S[santa_idx][1] + dy[dir]*C;
                if(nx<0||nx>N-1||ny<0||ny>N-1) {
                    S[santa_idx][4] = 1;
                    S[santa_idx][2] += turn;
                    map[S[santa_idx][0]][S[santa_idx][1]] = 0;
                    santa_surv--;
                }
                else {
                    map[S[santa_idx][0]][S[santa_idx][1]] = 0;
                    S[santa_idx][0] = nx;
                    S[santa_idx][1] = ny;
                    //nx ny부터 한칸씩 다밀기
                    int next_santa_idx = map[S[santa_idx][0]][S[santa_idx][1]];
                    while(next_santa_idx!=0) {
                        nx += dx[dir];
                        ny += dy[dir];
                        if(nx<0||nx>N-1||ny<0||ny>N-1) {
                            S[next_santa_idx][4] = 1;
                            S[next_santa_idx][2] += turn;
                            santa_surv--;
                            break;
                        }
                        int temp = map[nx][ny];
                        map[nx][ny] = next_santa_idx;
                        S[next_santa_idx][0] = nx;
                        S[next_santa_idx][1] = ny;
                        next_santa_idx = temp;
                    }
                    //원래 밀린 산타 지도에 표시
                    map[S[santa_idx][0]][S[santa_idx][1]] = santa_idx;
                }


            }

            //2. 산타 이동 30*4 = 120
            for(int p=1;p<=P;p++) {
                if(S[p][3] >= turn || S[p][4]==1) continue;
                //현재 위치와 루돌프까지의 거리 구하기
                int cur_dist = getDist(S[p][0],S[p][1],R[0],R[1]);

                //상우하좌 방향마다 거리 비교
                dir = -1;
                for(int d=0;d<4;d++) {
                    int nx = S[p][0] + dx[d];
                    int ny = S[p][1] + dy[d];
                    if(nx<0||nx>N-1||ny<0||ny>N-1||map[nx][ny]!=0) continue;;
                    int cal_dist = getDist(nx,ny,R[0],R[1]);
                    if(cal_dist < cur_dist) {
                        cur_dist = cal_dist;
                        dir = d;
                    }
                }

                if(dir!=-1) {
                    map[S[p][0]][S[p][1]] = 0;
                    S[p][0] += dx[dir];
                    S[p][1] += dy[dir];
                    map[S[p][0]][S[p][1]] = p;
                }

                //루돌프 충돌 검사 // 0:산타r, 1:산타c, 2:점수, 3:기절턴수(2이면 2턴부터 활동가능), 4:탈락여부(0,1)
                if(S[p][0]==R[0] && S[p][1]==R[1]) {
                    //산타 점수 올리기
                    S[p][2] += D;

                    //산타 기절
                    S[p][3] = turn + 1;

                    //산타 밀기
                    int nx = S[p][0] + dx[dir]*-1*D;
                    int ny = S[p][1] + dy[dir]*-1*D;
                    if(nx<0||nx>N-1||ny<0||ny>N-1) {
                        S[p][4] = 1;
                        S[p][2] += turn;
                        map[S[p][0]][S[p][1]] = 0;
                        santa_surv--;
                    }
                    else {
                        map[S[p][0]][S[p][1]] = 0;
                        S[p][0] = nx;
                        S[p][1] = ny;
                        //nx ny부터 한칸씩 다밀기
                        int next_p = map[S[p][0]][S[p][1]];
                        while(next_p!=0) {
                            nx += (dx[dir]*-1);
                            ny += (dy[dir]*-1);
                            if(nx<0||nx>N-1||ny<0||ny>N-1) {
                                S[next_p][4] = 1;
                                S[next_p][2] += turn;
                                santa_surv--;
                                break;
                            }
                            int temp = map[nx][ny];
                            map[nx][ny] = next_p;
                            S[next_p][0] = nx;
                            S[next_p][1] = ny;
                            next_p = temp;
                        }
                        //원래 밀린 산타 지도에 표시
                        map[S[p][0]][S[p][1]] = p;
                    }

                }
            }

            //test
//            System.out.println((turn+1) + " : after");
//           for(int i=0;i<N;i++) {
//               for(int j=0;j<N;j++) {
//                   if(i==R[0] && j==R[1]) System.out.print("R ");
//                   else System.out.print(map[i][j] + " ");
//               }
//               System.out.println();
//           }
//            System.out.println();
//
//           for(int i=1;i<=P;i++) System.out.print(S[i][2]+" ");
//            System.out.println();
            //test


            //3. 졸료 조건 확인
            if(santa_surv == 0) break;
            turn++;
        }

        StringBuilder sb = new StringBuilder();
        for(int i=1;i<=P;i++) {
            int score = S[i][2];
            if(S[i][4]==0) score += turn;
            sb.append(score + " ");
        }
        System.out.println(sb);
        br.close();
    }

    static int getDist(int x1, int y1, int x2, int y2) {
        return (int)(Math.pow(Math.abs(x1-x2),2) + Math.pow(Math.abs(y1-y2),2));
    }
}