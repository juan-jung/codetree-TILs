import java.io.*;
import java.util.*;

public class Main {
    static int N;
    static int[] dx = {-1,0,1,0}, dy = {0,-1,0,1}; //상좌하우
    static int[] dx2 = {0,1,0,-1}, dy2 = {1,0,-1,0}; //우하좌상
    static class Group {
        int idx;
        int color;
        int size;
        List<int[]> boundary;

        public Group(int idx, int color, int size, List<int[]> boundary) {
            this.idx = idx;
            this.color = color;
            this.size = size;
            this.boundary = boundary;
        }
    }

    static class Groups {
        int[][] groupMap;
        List<Group> groups;

        boolean[][] v;

        public Groups(int[][] groupMap, List<Group> groups) {
            this.groupMap = groupMap;
            this.groups = groups;
            this.v = new boolean[groups.size()][groups.size()];
        }
    }
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        int[][] map = new int[N][N];
        for(int i=0;i<N;i++) {
            StringTokenizer st = new StringTokenizer(br.readLine()," ");
            for(int j=0;j<N;j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int ans = 0;
        for(int t=1;t<=4;t++) {
            //점수계산
            Groups groups = makeGroups(map);
            for(Group group : groups.groups) {
                //경계값 확인
                int[] touched = new int[groups.groups.size()];
                for(int[] xy : group.boundary) {
                    touched[groups.groupMap[xy[0]][xy[1]]]++;
                }
                for(int i=0;i<touched.length;i++) {
                    if(touched[i]==0 || groups.v[groups.groups.get(i).idx][group.idx] || groups.v[group.idx][groups.groups.get(i).idx]) continue;
                    //예술 점수 계산 : (.a칸수 + b칸수) * a color * b color * boundary
                    ans += ((group.size + groups.groups.get(i).size) * group.color * groups.groups.get(i).color * touched[i]);

                    //group 관게 표시
                    groups.v[group.idx][groups.groups.get(i).idx] = true;
                }
            }

            if(t==4) break;

            //배열 돌리기
            rotateCross(map);
            rotateEdge(map,0,0);
            rotateEdge(map,0,N/2+1);
            rotateEdge(map,N/2+1,0);
            rotateEdge(map,N/2+1,N/2+1);

        }


        System.out.println(ans);
        br.close();
    }

    static void rotateEach(int x, int y, int value, int dir, int minX, int minY, int maxX, int maxY, int stopX, int stopY, int[][] map) {
        int temp = map[x][y];
        map[x][y] = value;
        if(x==stopX && y==stopY) return;
        int nx = x+dx2[dir];
        int ny = y+dy2[dir];
        int tempDir = dir;
        if(nx<minX||nx>maxX||ny<minY||ny>maxY) {
            tempDir = (dir+1)%4;
            nx = x+dx2[tempDir];
            ny = y+dy2[tempDir];
        }
        rotateEach(nx, ny, temp, tempDir, minX, minY, maxX, maxY, stopX, stopY, map);

    }

    static void rotateEdge(int[][] map, int x, int y) {
        //시계방향 90도
        for(int i=0;i<N/2/2;i++) {
            rotateEach(x+i, y+i+1, map[x][y], 0, x, y, x+N/2-1, y+N/2-1, x, y, map);
        }
    }

    static void rotateCross(int[][] map) {
        //.반시계 방향 상좌하우
        //TODO : for문 하나로 합치기
        int midX = N/2;
        int midY = N/2;
        int[][] temp = new int[4][N/2];
        for(int d=0;d<4;d++) {
            for(int i=0;i<N/2;i++) {
                temp[d][i] = map[midX+dx[d]*(i+1)][midY+dy[d]*(i+1)];
            }
        }

        for(int d=0;d<4;d++) {
            for(int i=0;i<N/2;i++) {
                map[midX+dx[(d+1)%4]*(i+1)][midY+dy[(d+1)%4]*(i+1)] = temp[d][i];
            }
        }
    }

    static void calGroup(int i, int j, Group group, int[][] map, boolean[][] v, int[][] groupMap) {
        groupMap[i][j] = group.idx;
        v[i][j] = true;
        group.size++;
        for(int d=0;d<4;d++) {
            int ni = i + dx[d];
            int nj = j + dy[d];
            if(ni<0||ni>N-1||nj<0||nj>N-1||v[ni][nj]) continue;
            if(map[ni][nj] == group.color) calGroup(ni, nj, group, map, v, groupMap);
            else group.boundary.add(new int[]{ni,nj});
        }
    }

    static Groups makeGroups(int[][] map) {
        List<Group> groups = new ArrayList<>();
        int idx = 0;
        boolean[][] v= new boolean[N][N];
        int[][] groupMap = new int[N][N];
        for(int i=0;i<N;i++) {
            for(int j=0;j<N;j++) {
                if(v[i][j]) continue;
                Group group = new Group(idx++,map[i][j], 0, new ArrayList<int[]>());
                calGroup(i,j,group,map,v,groupMap);
                groups.add(group);
            }
        }

        return new Groups(groupMap, groups);
    }
}