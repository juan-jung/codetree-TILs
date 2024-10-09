import java.io.*;
import java.util.*;
public class Main {
    static class Node {
        int x;
        int y;
        int cnt;
        int d;
        List<int[]> path;

        Node(int x, int y, int cnt) {
            this.x = x;
            this.y = y;
            this.cnt = cnt;
            this.d = 0;
            this.path = new ArrayList<int[]>();
        }

        Node(int x, int y, int cnt, int d, List<int[]> path) {
            this.x = x;
            this.y = y;
            this.cnt = cnt;
            this.d = d;
            this.path = new ArrayList<int[]>();
            for(int[] p : path) {
                this.path.add(new int[]{p[0],p[1]});
            }
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());

        int[][] turrets = new int[N][M];
        for(int i=0;i<N;i++) {
            st = new StringTokenizer(br.readLine()," ");
            for(int j=0;j<M;j++) {
                turrets[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int[][] recentAttack = new int[N][M];
        //우하상좌
        int[] dx = {0,1,0,-1,-1,-1,1,1}, dy = {1,0,-1,0,-1,1,-1,1};

        for(int k=1;k<=K;k++) {
            int turretCnt = 0;
            int[] attacker = new int[3];
            attacker[2] = Integer.MAX_VALUE;
            int[] defender = new int[2];

            for(int i=0;i<N;i++) {
                for(int j=0;j<M;j++) {
                    if(turrets[i][j]==0) continue;
                    turretCnt++;
                    if(attacker[2] > turrets[i][j]) {
                        attacker[0] = i;
                        attacker[1] = j;
                        attacker[2] = turrets[i][j];
                    }
                    else if(attacker[2] == turrets[i][j]) {
                        if(recentAttack[attacker[0]][attacker[1]] < recentAttack[i][j]) {
                            attacker[0] = i;
                            attacker[1] = j;
                            attacker[2] = turrets[i][j];
                        }
                        else if(recentAttack[attacker[0]][attacker[1]] == recentAttack[i][j]) {
                            if(attacker[0] + attacker[1] < i + j) {
                                attacker[0] = i;
                                attacker[1] = j;
                                attacker[2] = turrets[i][j];
                            }
                            else if(attacker[0] + attacker[1] == i + j) {
                                if(attacker[1] < j) {
                                    attacker[0] = i;
                                    attacker[1] = j;
                                    attacker[2] = turrets[i][j];
                                }
                            }
                        }
                    }

                    //공격대상 선정
                    if(turrets[defender[0]][defender[1]] < turrets[i][j]) {
                        defender[0] = i;
                        defender[1] = j;
                    }
                    else if(turrets[defender[0]][defender[1]] == turrets[i][j]) {
                        if(recentAttack[defender[0]][defender[1]] > recentAttack[i][j]) {
                            defender[0] = i;
                            defender[1] = j;
                        }
                        else if(recentAttack[defender[0]][defender[1]] == recentAttack[i][j]) {
                            if(defender[0] + defender[1] > i + j) {
                                defender[0] = i;
                                defender[1] = j;
                            }
                            else if(defender[0] + defender[1] == i + j) {
                                if(defender[1] > j) {
                                    defender[0] = i;
                                    defender[1] = j;
                                }
                            }
                        }
                    }
                }
            }

            
            

            if(turretCnt == 1) {
                // System.out.println("k : " + k);
                break;
            }

            turrets[attacker[0]][attacker[1]] += N+M;
            recentAttack[attacker[0]][attacker[1]] = k;

            ArrayDeque<Node> q = new ArrayDeque<>();
            boolean[][] v = new boolean[N][M];
            q.offer(new Node(attacker[0], attacker[1], 0));
            boolean isReachable = false;

            boolean[][] attacked = new boolean[N][M];

            while(!q.isEmpty()) {
                Node cur = q.poll();

                if(v[cur.x][cur.y]) continue;
                v[cur.x][cur.y] = true;

                if(cur.x==defender[0] && cur.y==defender[1]) {
                    isReachable = true;

                    turrets[defender[0]][defender[1]] -= turrets[attacker[0]][attacker[1]];
                    if(turrets[defender[0]][defender[1]]<0) turrets[defender[0]][defender[1]] = 0;
                    attacked[defender[0]][defender[1]] = true;

                    for(int i=1;i<cur.path.size();i++) {
                        int ax = cur.path.get(i)[0];
                        int ay = cur.path.get(i)[1];
                        turrets[ax][ay] -= turrets[attacker[0]][attacker[1]]/2;
                        if(turrets[ax][ay] < 0) turrets[ax][ay] = 0;
                        attacked[ax][ay] = true;
                    }
                    break;
                }

                cur.path.add(new int[]{cur.x, cur.y});

                for(int d=0;d<4;d++) {
                    int nx = cur.x + dx[d];
                    int ny = cur.y + dy[d];
                    if(nx < 0) nx = N-1;
                    if(nx > N-1) nx = 0;
                    if(ny < 0) ny = M-1;
                    if(ny > M-1) ny = 0;
                    if(turrets[nx][ny]==0 || v[nx][ny]) continue;
                    q.offer(new Node(nx,ny,cur.cnt+1,d,cur.path));
                }
            }

            if(!isReachable) {
                turrets[defender[0]][defender[1]] -= turrets[attacker[0]][attacker[1]];
                if(turrets[defender[0]][defender[1]]<0) turrets[defender[0]][defender[1]] = 0;
                attacked[defender[0]][defender[1]] = true;

                for(int d=0;d<8;d++) {
                    int nx = defender[0] + dx[d];
                    int ny = defender[1] + dy[d];
                    if(nx < 0) nx = N-1;
                    if(nx > N-1) nx = 0;
                    if(ny < 0) ny = M-1;
                    if(ny > M-1) ny = 0;
                    if(turrets[nx][ny]==0 || (nx==attacker[0]&&ny==attacker[1])) continue;
                    turrets[nx][ny] -= turrets[attacker[0]][attacker[1]]/2;
                    if(turrets[nx][ny] < 0) turrets[nx][ny] = 0;
                    attacked[nx][ny] = true;
                }
            }

            for(int i=0;i<N;i++) {
                for(int j=0;j<M;j++) {
                    if(turrets[i][j]==0 || attacked[i][j] || (i==attacker[0]&&j==attacker[1])) continue;
                    turrets[i][j]++;
                }
            }

            // if(k >= 0) {
            //     System.out.println("after turn" + k + " attack to " + Arrays.toString(defender) + " from " + Arrays.toString(attacker));
            //     for(int[] a : turrets) System.out.println(Arrays.toString(a));
            //     System.out.println();
            // }
        }

        int ans = 0;
        for(int i=0;i<N;i++) {
            for(int j=0;j<M;j++) {
                ans = Math.max(ans, turrets[i][j]);
            }
        }

        System.out.println(ans);
    }
}