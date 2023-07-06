public class Main {
    private static final int row = 12;
    private static final int col = 12;
    private static final int wall = -1;
    private static final int goal = 0;
    private static final int p1 = 1;
    private static final int p2 = 2;
    private static final int back = -3;
    private static final int freeze = -2;
    private static final int restore = 0;
    private static final int doublePoints = -4;

    private static int[][] maze = {
            {-1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, -1},
            {-1, 2, -1, 20, -1, -4, 10, -3, -1, -1, -1, -1},
            {-1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, 50, -1, -1, -2, 50, 0, 0, -1, -1, -1, -1},
            {-1, 10, -1, 0, -1, -2, -1, -1, -1, -1, -1, -1},
            {-1, -4, -1, -1, -1, 10, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -3, -1, 0, 10, -1, -1, -1, -1, -1},
            {0, -4, 10, -1, -1, -1, -1, 40, -1, -1, -1, -1},
            {-1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1},
            {-1, 10, -1, -1, -1, -1, -1, -4, -1, -1, -1, -1},
            {-1, 50, -1, 10, 10, 50, -2, 1, -1, -1, -1, -1},
            {-1, 20, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };

    private static int p1PosX;
    private static int p1PosY;
    private static int p2PosX;
    private static int p2PosY;
    private static int goalX;
    private static int goalY;
    private static int p1score = 0;
    private static int p2score = 0;

    public static void main(String[] args) {
        FindPos();
        printMaze();

        Thread p1Thread = new Thread(() -> {
            moveP(1, p1PosX, p1PosY);
            System.out.println("player 1 ended with " + p1score + " score.");
        });

        Thread p2Thread = new Thread(() -> {
            moveP(2, p2PosX, p2PosY);
            System.out.println("player 2 ended with " + p2score + " score.");
        });

        p1Thread.start();
        p2Thread.start();

        try {
            p1Thread.join();
            p2Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (p1score > p2score) {
            System.out.println("player 1 has won!");
        } else if (p2score > p1score) {
            System.out.println("player 2 has won!");
        } else {
            System.out.println("Tie!");
        }
    }

    private static void FindPos() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (maze[i][j] == p1) {
                    p1PosX = i;
                    p1PosY = j;
                } else if (maze[i][j] == p2) {
                    p2PosX = i;
                    p2PosY = j;
                } else if (maze[i][j] == goal) {
                    goalX = i;
                    goalY = j;
                }
            }
        }
    }

    private static void moveP(int player, int posX, int posY) {
        while (true) {
            int movement = randomMov();
            int newPosX = posX;
            int newPosY = posY;

            if (movement == 0) {
                newPosX--; // Move up
            } else if (movement == 1) {
                newPosX++; // Mover down
            } else if (movement == 2) {
                newPosY--; // Mover left
            } else if (movement == 3) {
                newPosY++; // Mover right
            }

            if (validMove(newPosX, newPosY)) {
                int space = maze[newPosX][newPosY];

                if (space == goal) {
                    break; // player achieved goal
                } else if (space > 0) {
                    addScore(player, space); // add score
                } else if (space == freeze) {
                    freezePlayer(player); // freeze player
                } else if (space == back) {
                    goBack(player); // go back
                } else if (space == doublePoints) {
                    add2Score(player); // double score
                } else if (space == restore) {
                    less0Score(player); // restart score
                }

                maze[posX][posY] = restore; // space known

                // Change pos player
                if (player == 1) {
                    p1PosX = newPosX;
                    p1PosY = newPosY;
                } else if (player == 2) {
                    p2PosX = newPosX;
                    p2PosY = newPosY;
                }

                posX = newPosX;
                posY = newPosY;

                printMaze();
            }
        }
    }

    private static int randomMov() {
        return (int) (Math.random() * 4); // 0: up, 1: down, 2: left, 3: right
    }

    private static boolean validMove(int posX, int posY) {
        return posX >= 0 && posX < row && posY >= 0 && posY < col && maze[posX][posY] != wall;
    }

    private static synchronized void addScore(int player, int score) {
        if (player == 1) {
            p1score += score;
        } else if (player == 2) {
            p2score += score;
        }
    }

    private static synchronized void freezePlayer(int player) {
        System.out.println("player " + player + " has been frozen for 3 seconds.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("player " + player + " has been unfrozen.");
    }

    private static synchronized void goBack(int player) {
        if (player == 1) {
            p1PosX = goalX;
            p1PosY = goalY;
        } else if (player == 2) {
            p2PosX = goalX;
            p2PosY = goalY;
        }
        System.out.println("player " + player + " has gone back.");
    }

    private static synchronized void add2Score(int player) {
        if (player == 1) {
            p1score *= 2;
        } else if (player == 2) {
            p2score *= 2;
        }
        System.out.println("player " + player + " has doubled his score.");
    }

    private static synchronized void less0Score(int player) {
        if (player == 1) {
            p1score = 0;
        } else if (player == 2) {
            p2score = 0;
        }
        System.out.println("player " + player + " has restarted his score.");
    }

    private static void printMaze() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == p1PosX && j == p1PosY) {
                    System.out.print("1 ");
                } else if (i == p2PosX && j == p2PosY) {
                    System.out.print("2 ");
                } else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}