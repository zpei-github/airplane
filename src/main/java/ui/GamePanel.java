package ui;

import bullet.Bullet;
import plane.Aircraft;
import plane.BadAircraft;
import tool.ImageTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
        this.gameFrame = frame;
        // 读取背景图片
        backgroundImage = ImageTool.getImg("/img/background.png");

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
        g.drawString("倒计时：" + countdown, 350, 30);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("微软雅黑", Font.BOLD, 50));
        g.drawString("游戏结束", 120, 300);
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
                repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread interrupted", e);
                }
            }
        }, "action").start();

        // 倒计时线程
        new Thread(() -> {
            while (countdown > 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    countdown--;
                    repaint();
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread interrupted", e);
                }
            }
            isGameOver = true;
            repaint();
            showGameOverDialog();
        }, "countdown").start();
    }

    /**
     * 判断是否击中敌机，增加分数
     */
    private void checkCollisions() {
        for (Bullet bullet : bulletList) {
            for (BadAircraft badAircraft : badAircraftList) {
                if (badAircraft.shootBy(bullet)) {
                    badAircraftList.remove(badAircraft);
                    bulletList.remove(bullet);
                    score += badAircraft.getScore();
                }
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
     * 生成子弹
     */
    private void generateBullet() {
        bulletCounter++;
        if (bulletCounter >= 10) {
            Bullet bullet1 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 5, aircraft.getY(), 0);
            Bullet bullet2 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 45, aircraft.getY() - 20, 1);
            Bullet bullet3 = new Bullet(ImageTool.getImg("/img/bullet1.png"), aircraft.getX() + 90, aircraft.getY(), 2);
            bulletList.add(bullet1);
            bulletList.add(bullet2);
            bulletList.add(bullet3);
            bulletCounter = 0;
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
        if (badAircraftCounter >= 20) {
            badAircraftList.add(new BadAircraft(ImageTool.getImg("/img/enemy1.png"), 1));
            badAircraftCounter = 0;
            mediumBadAircraftCounter++;
        }
        if (mediumBadAircraftCounter >= 5) {
            badAircraftList.add(new BadAircraft(ImageTool.getImg("/img/enemy2.png"), 10));
            mediumBadAircraftCounter = 0;
            largeBadAircraftCounter++;
        }
        if (largeBadAircraftCounter >= 5) {
            BadAircraft badAircraft = new BadAircraft(ImageTool.getImg("/img/enemy3.png"), 100);
            badAircraft.setStep(1);
            badAircraftList.add(badAircraft);
            largeBadAircraftCounter = 0;
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
        mediumBadAircraftCounter = 0;
        largeBadAircraftCounter = 0;
        badAircraftList.clear();
        bulletList.clear();
        isGameOver = false;
        action();
    }
}
