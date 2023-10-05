import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Graphics;

public class TankBattleModifiedP2 {
    public static void main(String[] args) throws Exception {
        // ------------------------------------------------------------------------------------
        // NETWORKING PART

        System.out.println("Player 2 section started");
        String ip = "localhost";
        int port = 7345;
        Socket s = new Socket(ip, port);

        // ------------------------------------------------------------------------

        JFrame window = new JFrame();

        GameControl gameControl = new GameControl();
        window.addKeyListener(gameControl);
        window.add(gameControl);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("--Tank Battle-- player 2");
        window.setVisible(true);
        window.pack();

        while (true) {
            sleep(10);

            // NETWORKING PART-----------------------------------------------
            decrypt(recieveSignal(s), gameControl);
            sendSignal(s, incrypt(gameControl));

            // CHECKING FOR GAMEOVER----------------------------------

            if (gameControl.gameOver()) {
                break;
            }

            // PLAYER 1 FIREING MOVEMENT IN
            // GAMECONTROL-------------------------------------------------------

            if (gameControl.p1firing_up) {
                gameControl.p1fy -= gameControl.fire_speed;
            } else if (gameControl.p1firing_down) {
                gameControl.p1fy += gameControl.fire_speed;
            } else if (gameControl.p1firing_left) {
                gameControl.p1fx -= gameControl.fire_speed;
            } else if (gameControl.p1firing_right) {
                gameControl.p1fx += gameControl.fire_speed;
            }

            if (gameControl.p1firing && (gameControl.p1fx < 0 || gameControl.p1fx > gameControl.screenWidth
                    || gameControl.p1fy < 0 || gameControl.p1fy > gameControl.screenHeight)) {
                gameControl.p1firing_up = false;
                gameControl.p1firing_down = false;
                gameControl.p1firing_left = false;
                gameControl.p1firing_right = false;
                gameControl.p1firing = false;
            }
            // ----------------------------------------------------------------------------------------

            gameControl.repaint();
        }
    }

    public static String incrypt(GameControl gameControl) {
        String encodedString = "";
        String tempStr = "";

        for (int i = 0; i <= 5; i++) {
            if (i == 0) {
                tempStr = Integer.toString(gameControl.p1x);
            } else if (i == 1) {
                tempStr = Integer.toString(gameControl.p1y);
            } else if (i == 2) {
                if (gameControl.p1firing) {
                    tempStr = "1";
                } else {
                    tempStr = "0";
                }
            } else if (i == 3) {
                tempStr = Integer.toString(gameControl.p1fx);
            } else if (i == 4) {
                tempStr = Integer.toString(gameControl.p1fy);
            } else if (i == 5) {
                if (gameControl.p1up) {
                    tempStr = "1";
                }
                if (gameControl.p1right) {
                    tempStr = "2";
                }
                if (gameControl.p1down) {
                    tempStr = "3";
                }
                if (gameControl.p1left) {
                    tempStr = "4";
                }
            }

            encodedString += tempStr;
            encodedString += ",";
        }

        return encodedString;
    }

    public static void decrypt(String str, GameControl gameControl) {
        int counter = 0;
        String tempStr = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i)==',') {
                counter++;
                tempStr = "";
            } else if (counter == 0) {
                tempStr += str.charAt(i);
                gameControl.p2x = Integer.valueOf(tempStr);
            } else if (counter == 1) {
                tempStr += str.charAt(i);
                gameControl.p2y = Integer.valueOf(tempStr);
            } else if (counter == 2) {
                tempStr += str.charAt(i);
                if (tempStr.equals("1")) {
                    gameControl.p2firing = true;
                } else {
                    gameControl.p2firing = false;
                }
            } else if (counter == 3) {
                tempStr += str.charAt(i);
                gameControl.p2fx = Integer.valueOf(tempStr);
            } else if (counter == 4) {
                tempStr += str.charAt(i);
                gameControl.p2fy = Integer.valueOf(tempStr);
            } else if (counter == 5) {
                tempStr += str.charAt(i);
                if (tempStr.equals("1")) {
                    gameControl.p2up = true;
                    gameControl.p2right = false;
                    gameControl.p2down = false;
                    gameControl.p2left = false;
                } else if (tempStr.equals("2")) {
                    gameControl.p2up = false;
                    gameControl.p2right = true;
                    gameControl.p2down = false;
                    gameControl.p2left = false;
                } else if (tempStr.equals("3")) {
                    gameControl.p2up = false;
                    gameControl.p2right = false;
                    gameControl.p2down = true;
                    gameControl.p2left = false;
                } else if (tempStr.equals("4")) {
                    gameControl.p2up = false;
                    gameControl.p2right = false;
                    gameControl.p2down = false;
                    gameControl.p2left = true;
                }
            }
        }
    }

    public static String recieveSignal(Socket s) throws Exception {
        String recievedStr;
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        recievedStr = br.readLine();

        return recievedStr;
    }

    public static void sendSignal(Socket s, String sendingStr) throws Exception {
        PrintWriter out = new PrintWriter(s.getOutputStream());
        out.println(sendingStr);
        out.flush();
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }
}

class GameControl extends JPanel implements KeyListener {

    final int screenWidth = 1200;
    final int screenHeight = 700;

    final int playerSize = 40;

    // PLAYER 1 FIRE AND FIRE DIRECTION-----------------------------------------
    boolean p1firing = false;
    boolean p1firing_up = false;
    boolean p1firing_down = false;
    boolean p1firing_left = false;
    boolean p1firing_right = false;

    // PLAYER 2 FIRE AND FIRE DIRECTION-----------------------------------------
    boolean p2firing = false;
    boolean p2firing_up = false;
    boolean p2firing_down = false;
    boolean p2firing_left = false;
    boolean p2firing_right = false;

    // PLAYER 1 TANK FACING DIRECTION--------------------------------------
    boolean p1up = true;
    boolean p1down = false;
    boolean p1left = false;
    boolean p1right = false;

    // WINNING STATUS--------------------------------------------
    boolean youWin=false;
    boolean youLose=false;

    // PLAYER 2 TANK FACING DIRECTION--------------------------------------
    boolean p2up;
    boolean p2down;
    boolean p2left;
    boolean p2right;

    // PLAYER 1 COORDINATES------------------------------
    int p1x = 100;
    int p1y = 100;

    // OPPONENT PLAYER 2 COORDINATES---------------------------
    int p2x;
    int p2y;

    // PLAYER 1 FIRE BALL COORDINATES---------------------------------------
    int p1fx;
    int p1fy;

    // PLAYER 2 FIRE BALL COORDINATES---------------------------------------
    int p2fx;
    int p2fy;

    // FIRE BALL SIZE AND ITS SPEED--------------------------------------------
    int fire_size = 10;
    int fire_speed=8;

    GameControl() {

        setBackground(Color.black);
        setPreferredSize(new Dimension(screenWidth, screenHeight));

    }

    boolean gameOver(){
        if((p1fx>=p2x && p1fx <= p2x+playerSize) && (p1fy>=p2y && p1fy<=p2y+playerSize)){
            youWin=true;
            return true;
        }
        else if((p2fx>=p1x && p2fx <= p1x+playerSize) && (p2fy>=p1y && p2fy<=p1y+playerSize)){
            youLose=true;
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        // MAKER'S TAG----------------------------
        g.setColor(Color.white);
        g.drawString("--------Ajay Creations------------------", screenWidth/2, 10);

        g.setColor(Color.orange);
        g.fill3DRect(p1x, p1y, playerSize, playerSize, true);

        g.setColor(Color.blue);
        g.fill3DRect(p2x, p2y, playerSize, playerSize, true);
        g.setColor(Color.gray);

        // PLAYER 1 DIRECTION
        // MOVEMENTS---------------------------------------------------

        if (p1up) {
            g.fill3DRect(p1x + 5 * playerSize / 12, p1y - playerSize, playerSize / 6, playerSize, true);
            if (!p1firing) {
                p1fx = p1x + 5 * playerSize / 12;
                p1fy = p1y - playerSize - fire_size;
            }

        } else if (p1down) {
            g.fill3DRect(p1x + 5 * playerSize / 12, p1y + playerSize, playerSize / 6, playerSize, true);
            if (!p1firing) {
                p1fx = p1x + 5 * playerSize / 12;
                p1fy = p1y + 2 * playerSize;
            }

        } else if (p1left) {
            g.fill3DRect(p1x - playerSize, p1y + 5 * playerSize / 12, playerSize, playerSize / 6, true);
            if (!p1firing) {
                p1fx = p1x - playerSize - fire_size;
                p1fy = p1y + 5 * playerSize / 12;
            }

        } else if (p1right) {
            g.fill3DRect(p1x + playerSize, p1y + 5 * playerSize / 12, playerSize, playerSize / 6, true);
            if (!p1firing) {
                p1fx = p1x + 2 * playerSize;
                p1fy = p1y + 5 * playerSize / 12;
            }

        }

        // PLAYER 2 DIRECTION
        // MOVEMENTS---------------------------------------------------

        if (p2up) {
            g.fill3DRect(p2x + 5 * playerSize / 12, p2y - playerSize, playerSize / 6, playerSize, true);

        } else if (p2down) {
            g.fill3DRect(p2x + 5 * playerSize / 12, p2y + playerSize, playerSize / 6, playerSize, true);

        } else if (p2left) {
            g.fill3DRect(p2x - playerSize, p2y + 5 * playerSize / 12, playerSize, playerSize / 6, true);

        } else if (p2right) {
            g.fill3DRect(p2x + playerSize, p2y + 5 * playerSize / 12, playerSize, playerSize / 6, true);

        }

        // PLAYER 1 FIRE GRAPHICS
        if (p1firing) {
            g.setColor(Color.red);
            g.fillArc(p1fx, p1fy, fire_size, fire_size, 0, 360);
        }

        // PLAYER 2 FIRE GRAPHICS
        if (p2firing) {
            g.setColor(Color.red);
            g.fillArc(p2fx, p2fy, fire_size, fire_size, 0, 360);
        }
        // --------------------------------------------------------------------

        if(youWin){
            g.drawString("----YOU WIN----", screenWidth/2, screenHeight/2);
        }
        else if(youLose){
            g.drawString("----YOU LOSE----", screenWidth/2, screenHeight/2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            p1y -= 4;

            p1up = true;
            p1down = false;
            p1left = false;
            p1right = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_S) {
            p1y += 4;

            p1up = false;
            p1down = true;
            p1left = false;
            p1right = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_A) {
            p1x -= 4;

            p1up = false;
            p1down = false;
            p1left = true;
            p1right = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_D) {
            p1x += 4;

            p1up = false;
            p1down = false;
            p1left = false;
            p1right = true;
        }

        else if (e.getKeyCode() == KeyEvent.VK_F) {
            if (p1up && !p1firing) {
                p1firing_up = true;
            } else if (p1down && !p1firing) {
                p1firing_down = true;
            } else if (p1left && !p1firing) {
                p1firing_left = true;
            } else if (p1right && !p1firing) {
                p1firing_right = true;
            }

            p1firing = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

// SIGNAL FORMAT--------------------------------
// playerCoordinateX, playerCoordinateY, playerFiring, playerFireCordinateX,
// playerFireCordinateY, playerDirection,