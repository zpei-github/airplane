package ui;

import tool.ImageTool;
import javax.swing.*;

/**
 * 游戏的窗口
 * Java中的窗体类：JFrame
 * 使用窗体类继承JFrame
 */
public class GameFrame extends JFrame {
    public static void main(String[] args) {
        // 创建窗口对象
        GameFrame frame = new GameFrame();
        // 创建面板对象
        GamePanel panel = new GamePanel(frame);

        // 窗体初始化
        frame.add(panel); // 把面板添加到窗口对象中
        frame.setVisible(true); // 显示窗口

        // 游戏开始，生成敌机以及子弹
        panel.action();
        System.out.println("游戏运行中···");
    }

    /**
     * 构造函数设置属性
     */
    private GameFrame() {
        System.out.println("GameFrame正在初始化···");

        // 设置标题
        setTitle("飞机大战-like");
        setIconImage(ImageTool.getImg("/img/Favorite.png"));
        // 设置窗口界面大小
        setSize(900, 1400);
        // 设置居中显示
        setLocationRelativeTo(null);
        // 设置不允许改变界面大小
        setResizable(false);
        // 设置关闭窗口的时候退出程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("GameFrame初始化完成！");
        System.out.println("游戏即将开始");
    }
}
