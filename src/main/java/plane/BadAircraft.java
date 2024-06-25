package plane;

import bullet.Bullet;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author like
 * @since 2020-10-15  15:38
 * 敌人的飞机
 */
public class BadAircraft extends Plane {
    // 飞机移动的速度
    private int step;
    // 这个飞机的分数
    private int score;

    //血量
    private int blood;

    public BadAircraft(BufferedImage aircraftImg, int score) {
        Random random = new Random();
        // 定义飞机的图片
        this.aircraftImg = aircraftImg;

        // 定义飞机的宽和高
        w = aircraftImg.getWidth();
        h = aircraftImg.getHeight();

        // 定义飞机的初始位置
        this.x = random.nextInt(900 - w);
        this.y = -h;

        // 设置敌对飞机移动的速度
        step = random.nextInt(16);
        this.score = score;
    }
    public BadAircraft(BufferedImage aircraftImg, int score, int blood) {
        Random random = new Random();
        // 定义飞机的图片
        this.aircraftImg = aircraftImg;

        // 定义飞机的宽和高
        w = aircraftImg.getWidth();
        h = aircraftImg.getHeight();

        // 定义飞机的初始位置
        this.x = random.nextInt(900 - w);
        this.y = -h;

        // 设置敌对飞机移动的速度
        step = random.nextInt(16);
        this.score = score;
    }

    @Override
    public void move() {
        y += step;
    }


    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBlood() {
        return blood;
    }
    public void setBlood(int blood) {
        this.blood = blood;
    }
    /**
     * 判断是否碰撞到了飞机
     *
     * @param bullet 打击的子弹
     *
     * @return 是否打击到了子弹
     */
    public boolean shootBy(Bullet bullet) {
        return this.x <= bullet.x + bullet.w && this.x >= bullet.x - this.w && this.y <= bullet.y + bullet.h &&
                this.y > bullet.y - this.h;
    }
}
