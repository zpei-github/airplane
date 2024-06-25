package ui;

import bullet.Bullet;
import plane.Aircraft;
import plane.BadAircraft;
import plane.BulletProp;
import tool.ImageTool;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * 游戏的面板
 * 1.继承JPanel
 */
public class GamePanel extends JPanel {
    // 背景图片
    private final BufferedImage backgroundImage;
    // 用户飞机
    private final Aircraft aircraft;
    // 敌机集合
    private final List<BadAircraft> badAircraftList = new CopyOnWriteArrayList<>();
    // 子弹集合
    private final List<Bullet> bulletList = new CopyOnWriteArrayList<>();
    //道具
    private BulletProp currentProp = null;
    private BulletProp myProp = null;

    //子弹发射频率
    private int bulletVelocity = 10;

    private int badAircraftCounter = 0;
    private int bulletCounter = 0;
    private int mediumBadAircraftCounter = 0;
    private int largeBadAircraftCounter = 0;
    // 分数
    private int score;
    // 倒计时
    private int countdown = 60; // 60秒倒计时

    private boolean isGameOver = false;
    private final GameFrame gameFrame;
    // 日志记录器
    private static final Logger logger = Logger.getLogger(GamePanel.class.getName());

    /**
     * 构造函数
     */
    public GamePanel(GameFrame frame) {
        new Thread(this::playMusic, "Background Music").start();
        this.gameFrame = frame;
        // 读取背景图片
        backgroundImage = ImageTool.getImg("/img/background.jpeg");

        // 初始化用户飞机
        aircraft = new Aircraft(ImageTool.getImg("/img/me1.png"));

        // 注册鼠标和键盘事件
        registerMouseEvents();
        registerKeyEvents(frame);
    }

    /**
     * 注册鼠标事件
     */
    private void registerMouseEvents() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isGameOver) {
                    aircraft.move(e);
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    /**
     * 注册键盘事件
     */
    private void registerKeyEvents(GameFrame frame) {
        KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    aircraft.move(e, 25);
                    repaint();
                }
            }
        };
        frame.addKeyListener(ka);
    }

    /**
     * 画图方法
     *
     * @param g 画笔
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(backgroundImage, 0, 0, null);
        drawAircraft(g);
        drawBullets(g);
        drawScore(g);
        drawCountdown(g);
        if (isGameOver) {
            drawGameOver(g);
        }
    }

    private void drawAircraft(Graphics g) {
        if(this.currentProp != null){
            g.drawImage(currentProp.getAircraftImg(), currentProp.getX(), currentProp.getY(), currentProp.getW(),currentProp.getH(),null);
        }
        g.drawImage(aircraft.getAircraftImg(), aircraft.getX(), aircraft.getY(), aircraft.getW(), aircraft.getH(), null);
        for (BadAircraft badAircraft : badAircraftList) {
            g.drawImage(badAircraft.getAircraftImg(), badAircraft.getX(), badAircraft.getY(), badAircraft.getW(), badAircraft.getH(), null);
        }
    }

    private void drawBullets(Graphics g) {
        for (Bullet bullet : bulletList) {
            g.drawImage(bullet.getAircraftImg(), bullet.getX(), bullet.getY(), bullet.getW(), bullet.getH(), null);
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));
        g.drawString("分数：" + score, 10, 30);
    }

    private void drawCountdown(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));
        g.drawString("倒计时：" + countdown, 750, 30);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("微软雅黑", Font.BOLD, 50));
        g.drawString("游戏结束", 450, 700);
    }

    /**
     * 游戏开始
     */
    public void action() {
        new Thread(() -> {
            while (!isGameOver) {
                generateBadAircraft();
                moveBadAircraft();
                generateBullet();
                moveBullet();
                checkCollisions();
                checkShoot();
                checkProp();
                try {
                    //刷新率控制
                    TimeUnit.MILLISECONDS.sleep(20);
                    repaint();
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread interrupted", e);
                }
            }
        }, "action").start();

        // 倒计时线程
        new Thread(() -> {
            while (countdown > 0  && !isGameOver) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    generateProp();
                    if(myProp != null){
                        myProp.setLeftTime(myProp.getLeftTime() - 1);
                    }
                    countdown--;
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread interrupted", e);
                }
            }
            isGameOver = true;
        }, "countdown").start();

        //
        /* 游戏检测线程
        * 在此处统一控制游戏界面刷新，并弹出游戏结束窗口
        * */
        new Thread(() -> {
            while (!isGameOver) {
                try {
                    //检测率控制
                    TimeUnit.MICROSECONDS.sleep(50);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread interrupted", e);
                }
            }
            repaint();
            showGameOverDialog();
        }, "countdown").start();
    }

    /**
     * 判断是否击中敌机，增加分数
     */
    private void checkShoot() {
        for (BadAircraft badAircraft : badAircraftList) {
            for (Bullet bullet : bulletList) {
                if (badAircraft.shootBy(bullet)) {
                    bulletList.remove(bullet);
                    if(badAircraft.getBlood() > 0){
                        badAircraft.setBlood(badAircraft.getBlood() - 1);
                    }
                    if(badAircraft.getBlood() <= 0){
                        badAircraftList.remove(badAircraft);
                        score += badAircraft.getScore();}
                }
            }
        }
    }

    /**
     * 判断是否与敌机碰撞
     */
    private void checkCollisions() {
        for (BadAircraft badAircraft : badAircraftList) {
            if (badAircraft.touch(aircraft)) {
                isGameOver = true;
            }
        }
    }

    /**
     * 判断道具捡拾与道具消除
     */
    private void checkProp() {
        if(currentProp != null) {
            if(aircraft.touch(currentProp)){
                myProp = currentProp;
                currentProp = null;
            }
        }

        if(myProp != null) {
            if(myProp.getLeftTime() <= 0){
                myProp = null;
            }
        }
    }

    /**
     * 移动子弹
     */
    private void moveBullet() {
        for (Bullet bullet : bulletList) {
            bullet.move();
        }
    }

    /**
     * 生成自己的子弹
     */
    private void generateBullet() {
        bulletCounter++;
        if(myProp != null){
            if (bulletCounter >= myProp.getBulletCount() && myProp.getType() == 5) {
                Bullet bullet1 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 45, aircraft.getY() - 20, 0);
                Bullet bullet2 = new Bullet(ImageTool.getImg("/img/bullet2.png"), aircraft.getX() + 45, aircraft.getY() - 20, 1);
                Bullet bullet3 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 45, aircraft.getY() - 20, 2);
                bulletList.add(bullet1);
                bulletList.add(bullet2);
                bulletList.add(bullet3);
                bulletCounter = 0;
            }
            return;
        }
        if (bulletCounter >= bulletVelocity) {
            Bullet bullet2 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 45, aircraft.getY() - 20, 1);
            bulletList.add(bullet2);
            bulletCounter = 0;
        }
    }



    /*音乐播放器
    *
    * */
    private void playMusic() {
        File backmusic = new File("src/main/resources/audio/Einswei.mid");
        try {
            AudioClip clip = Applet.newAudioClip(backmusic.toURL());
            clip.loop();
        } catch (Exception var3) {
            System.out.println(var3.fillInStackTrace());
        }
    }

    /**
     * 移动敌机
     */
    private void moveBadAircraft() {
        for (BadAircraft badAircraft : badAircraftList) {
            badAircraft.move();
        }
    }

    /**
     * 生成敌机
     */
    private void generateBadAircraft() {
        badAircraftCounter++;
        if (badAircraftCounter >= 10) {
            badAircraftList.add(new BadAircraft(ImageTool.getImg("/img/enemy1.png"), 1, 1));
            badAircraftCounter = 0;
            mediumBadAircraftCounter++;
        }
        if (mediumBadAircraftCounter >= 8) {
            badAircraftList.add(new BadAircraft(ImageTool.getImg("/img/enemy2.png"), 15, 5));
            mediumBadAircraftCounter = 0;
            largeBadAircraftCounter++;
        }
        if (largeBadAircraftCounter >= 15) {
            BadAircraft badAircraft = new BadAircraft(ImageTool.getImg("/img/enemy3.png"), 100, 25);
            badAircraft.setStep(1);
            badAircraftList.add(badAircraft);
            largeBadAircraftCounter = 0;
        }
    }

    /**
     * 生成道具
     */
    private void generateProp() {
        if(this.currentProp == null && myProp == null){
            this.currentProp = new BulletProp(ImageTool.getImg("/img/bullet_supply.png"), 5, 5, 15);
        }
    }

    /**
     * 显示游戏结束对话框
     */
    private void showGameOverDialog() {
        String message = String.format("游戏结束！你的分数是：%d。是否重新开始？", score);
        int option = JOptionPane.showOptionDialog(gameFrame, message, "游戏结束",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (option == JOptionPane.YES_OPTION) {
            // 重新开始游戏
            resetGame();
        } else {
            System.exit(0);
        }
    }

    /**
     * 重置游戏
     */
    private void resetGame() {
        score = 0;
        countdown = 60;
        badAircraftCounter = 0;
        bulletCounter = 0;
        bulletVelocity = 20;
        myProp = null;
        currentProp = null;
        mediumBadAircraftCounter = 0;
        largeBadAircraftCounter = 0;
        badAircraftList.clear();
        bulletList.clear();
        isGameOver = false;
        action();
    }
}
