package plane;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author like
 * @since 2020-10-15  15:38
 * 敌人的道具
 */
public class BulletProp extends Plane {
    // 道具移动的速度
    private int step;
    // 这个道具的类型
    private int type;
    // 子弹发射速度
    private int bulletCount;
    // 持续时间
    private int leftTime;


    public BulletProp(BufferedImage aircraftImg, int type, int bulletCount, int leftTime) {
        Random random = new Random();
        // 定义道具的图片
        this.aircraftImg = aircraftImg;

        // 定义道具的宽和高
        w = aircraftImg.getWidth();
        h = aircraftImg.getHeight();

        // 定义道具的初始位置
        this.x = random.nextInt(900 - w);
        this.y = random.nextInt(1400 - h);;

        // 设置道具移动的速度
        step = 0;

        this.type = type;
        this.bulletCount = bulletCount;
        this.leftTime = leftTime;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBulletCount() {
        return bulletCount;
    }

    public void setBulletCount(int bulletCount) {
        this.bulletCount = bulletCount;
    }

    public int getLeftTime() {
        return leftTime;
    }
    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }
}
