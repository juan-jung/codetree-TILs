import java.io.*;
import java.util.*;

public class Main {
    static class Person {
        int x;
        int y;
        Person prev;
        Person next;

        public Person(int x, int y, Person prev, Person next) {
            this.x = x;
            this.y = y;
            this.prev = prev;
            this.next = next;
        }
    }

    static class Team {
        int team_num;

        int member_cnt;
        boolean order; // true: 머리부터, false : 꼬리부터
        Person head;
        Person tail;

        public Team(int team_num, int member_cnt, boolean order, Person head, Person tail) {
            this.team_num = team_num;
            this.member_cnt = member_cnt;
            this.order = order;
            this.head = head;
            this.tail = tail;
        }
    }
    static int n,m,k;
    static int[] dx = {-1,1,0,0}, dy = {0,0,-1,1};
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine()," ");
        n = Integer.parseInt(st.nextToken()); //격자 크기 3-20
        m = Integer.parseInt(st.nextToken()); // 팀 수 1-5
        k = Integer.parseInt(st.nextToken()); // 라운드 수 1-1000
        int[][] map = new int[n][n];
        for(int i=0;i<n;i++) {
            st = new StringTokenizer(br.readLine()," ");
            for(int j=0;j<n;j++) map[i][j] = Integer.parseInt(st.nextToken());
        }

        Team[] teams = new Team[m+1];

        int team_idx = 1;
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                if(map[i][j]==1) {
                   makeTeam(i,j,team_idx, teams, map);
                   team_idx++;
                }
            }
        }

        int turn = 0;
        int score = 0;
        while(turn < k) {
            //팀 이동
            for(int i=1;i<=m;i++) {
                moveTeam(teams, i, map);
            }

            //공 발사
            score += throwBall(turn, teams);
            turn++;


        }

        System.out.println(score);

        br.close();
    }

    static int throwBall(int turn, Team[] teams) {
        turn%=(4*n);
        int score = 0;
        int hit_team = -1;
        if(turn/n==0) {
            // x==turn%n 이면서 y값이 가장 작은 사람
            int x = turn%n;
            int minY = Integer.MAX_VALUE;
            for(int i=1;i<=m;i++) {
                int cnt = 1;
                Person cur = teams[i].head;
                while(true) {
                    if(cur == null) break;
                    if(cur.x==x && cur.y < minY) {
                        minY = cur.y;
                        score = (int)(teams[i].order ? Math.pow(teams[i].member_cnt-cnt+1,2) : Math.pow(cnt,2));
                        hit_team = i;
                    }
                    cnt++;
                    cur = cur.next;

                }
            }
        }
        else if(turn/n==1) {
            //y==turn%n 이면서 x값이 가장 큰 사람
            int y = turn%n;
            int maxX = Integer.MIN_VALUE;
            for(int i=1;i<=m;i++) {
                int cnt = 1;
                Person cur = teams[i].head;
                while(true) {
                    if(cur == null) break;
                    if(cur.y==y && cur.x > maxX) {
                        maxX = cur.x;
                        score = (int)(teams[i].order ? Math.pow(teams[i].member_cnt-cnt+1,2) : Math.pow(cnt,2));
                        hit_team = i;
                    }
                    cnt++;
                    cur = cur.next;
                }
            }

        }
        else if(turn/n==2) {
            //x==n-1-turn&n 이면서 y값이 가장 큰 사람
            int x = n-1-turn%n;
            int maxY = Integer.MIN_VALUE;
            for(int i=1;i<=m;i++) {
                int cnt = 1;
                Person cur = teams[i].head;
                while(true) {
                    if(cur == null) break;
                    if(cur.x==x && cur.y > maxY) {
                        maxY = cur.y;
                        score = (int)(teams[i].order ? Math.pow(teams[i].member_cnt-cnt+1,2) : Math.pow(cnt,2));
                        hit_team = i;
                    }
                    cnt++;
                    cur = cur.next;
                }
            }
        }
        else if(turn/n==3) {
            //y==n-1-turn%n 이면서 x값이 가장 작은 사람
            int y = n-1-turn%n;
            int minX = Integer.MAX_VALUE;
            for(int i=1;i<=m;i++) {
                int cnt = 1;
                Person cur = teams[i].head;
                while(true) {
                    if(cur == null) break;
                    if(cur.y==y && cur.x < minX) {
                        minX = cur.x;
                        score = (int)(teams[i].order ? Math.pow(teams[i].member_cnt-cnt+1,2) : Math.pow(cnt,2));
                        hit_team = i;
                    }
                    cnt++;
                    cur = cur.next;
                }
            }
        }
        if(hit_team!=-1) teams[hit_team].order = !teams[hit_team].order;

        //test
//        System.out.println(score);
        //test

        return score;
    }

    static void moveTeam(Team[] teams, int team_idx, int[][] map) {
        if(!teams[team_idx].order) {
            //map==4이고 head.next가 아닌 곳 찾아서 헤드로, 테일은 없애기
            for(int d=0;d<4;d++) {
                int x = teams[team_idx].head.x + dx[d];
                int y = teams[team_idx].head.y + dy[d];
                if(x<0||x>n-1||y<0||y>n-1||map[x][y]!=4) continue;
                if(teams[team_idx].head.next.x==x && teams[team_idx].head.next.y==y) continue;
                Person newHead = new Person(x,y,null,teams[team_idx].head);
                teams[team_idx].head.prev = newHead;
                teams[team_idx].head = newHead;
                teams[team_idx].tail = teams[team_idx].tail.prev;
                teams[team_idx].tail.next = null;
                break;
            }

        }
        else {
            //tail 1
            //map==4이고 tail.prev가 아닌 곳 찾아서 테일로, 헤드는 없애기
            for(int d=0;d<4;d++) {
                int x = teams[team_idx].tail.x + dx[d];
                int y = teams[team_idx].tail.y + dy[d];
                if(x<0||x>n-1||y<0||y>n-1||map[x][y]!=4) continue;
                if(teams[team_idx].tail.prev.x==x && teams[team_idx].tail.prev.y==y) continue;
                Person newTail = new Person(x,y,teams[team_idx].tail, null);
                teams[team_idx].tail.next = newTail;
                teams[team_idx].tail = newTail;
                teams[team_idx].head = teams[team_idx].head.next;
                teams[team_idx].head.prev = null;
                break;
            }
        }
    }

    static void makeTeam(int x, int y, int team_idx, Team[] teams, int[][] map) {
        Person cur = new Person(x,y,null,null);
        map[x][y] = 4;
        teams[team_idx] = new Team(team_idx, 2,false, cur, null);
        while(true) {
            boolean flag = false;
            for(int d=0;d<4;d++) {
                int nx = x + dx[d];
                int ny = y + dy[d];
                if(nx<0||nx>n-1||ny<0||ny>n-1||map[nx][ny]!=2) continue;
                Person next = new Person(nx,ny,cur,null);
                cur.next = next;
                cur = next;
                map[nx][ny] = 4;
                flag = true;
                x = nx;
                y = ny;
                teams[team_idx].member_cnt++;
                break;
            }
            if(!flag) break;
        }

        for(int d=0;d<4;d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if(nx<0||nx>n-1||ny<0||ny>n-1||map[nx][ny]!=3) continue;
            Person tail = new Person(nx, ny, cur, null);
            teams[team_idx].tail = tail;
            map[nx][ny] = 4;
            break;
        }
    }
}