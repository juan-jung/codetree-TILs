import java.io.*;
import java.util.*;
public class Main {
    static int max;
    static int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};
    public static void main(String[] args) throws Exception {
        int ans = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");
        int R = Integer.parseInt(st.nextToken());
        int C = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());

        int[][] forest = new int[R+3][C];
        int[] exits = new int[K+1]; //출구방향 저장
        int[][] center = new int[K+1][2];
        for(int i=1;i<=K;i++) {
            st = new StringTokenizer(br.readLine(), " ");
            int r = 1;
            int c = Integer.parseInt(st.nextToken())-1;
            int exit = Integer.parseInt(st.nextToken()); //북동남서
            
            while(true) {
                if(southAvailable(r,c,forest,R,C)) {
                    r++;
                }
                else if(westAvailable(r,c,forest,R,C)) {
                    r++;
                    c--;
                    exit = exit-1 == -1 ? 3 : exit-1;
                }
                else if(eastAvailable(r,c,forest,R,C)) {
                    r++;
                    c++;
                    exit = (exit+1)%4;
                }
                else {
                    if(r<3) {
                        forest = new int[R+2][C];
                    }
                    else {
                        center[i][0] = r;
                        center[i][1] = c; 
                        forest[r][c] = forest[r+1][c] = forest[r-1][c] = forest[r][c+1] = forest[r][c-1] = i;
                        exits[i] = exit;

                        max = Integer.MIN_VALUE;
                        boolean[] v = new boolean[K+1];
                        v[i] = true;
                        findMax(r,c,forest,exits,v,center,R,C);
                        ans += max;
                    }
                    break;
                }
            }
        }

        System.out.println(ans);
    }

    static void findMax(int r, int c, int[][] forest, int[] exits, boolean[] v, int[][] center,int R, int C) {
        max = Math.max(max, r-1);

        for(int d=0;d<4;d++) {
            int nr = r+dx[exits[forest[r][c]]]+dx[d];
            int nc= c+dy[exits[forest[r][c]]]+dy[d];
            if(nr<0||nr>R+1||nc<0||nc>C-1||forest[nr][nc]==0||v[forest[nr][nc]]) continue;
            v[forest[nr][nc]] = true;
            findMax(center[forest[nr][nc]][0],center[forest[nr][nc]][1],forest,exits,v,center,R,C);
        }
    }

    static boolean southAvailable(int r, int c, int[][] forest, int R, int C) {
        if(r==R+1) return false;

        if(forest[r+1][c-1]!=0) return false;
        if(forest[r+1][c+1]!=0) return false;
        if(forest[r+2][c]!=0) return false;

        return true;
    }

    static boolean westAvailable(int r, int c, int[][] forest, int R, int C) {
        if(c<=1) return false;

        if(forest[r-1][c-1]!=0) return false;
        if(forest[r][c-2]!=0) return false;
        if(forest[r+1][c-1]!=0) return false;

        return southAvailable(r,c-1,forest,R,C);
    }

    static boolean eastAvailable(int r, int c, int[][] forest, int R, int C) {
        if(c>=C-2) return false;

        if(forest[r-1][c+1]!=0) return false;
        if(forest[r][c+2]!=0) return false;
        if(forest[r+1][c+1]!=0) return false;

        return southAvailable(r,c+1,forest,R,C);
    }
}