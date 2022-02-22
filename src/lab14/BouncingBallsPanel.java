package lab14;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouncingBallsPanel extends JPanel {
    List<Ball> balls = new ArrayList<>();
    AnimationThread animationThread = new AnimationThread();

    BouncingBallsPanel() {
        setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
        this.animationThread.start();
        this.animationThread.safeSuspend();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Ball b : this.balls) {

            AffineTransform saveAT = g2d.getTransform();

            g2d.translate(b.x, b.y);

            g2d.setColor(b.color);
            g2d.fillOval(0, 0, Ball.size, Ball.size);
            g2d.setColor(Color.black);
            g2d.drawOval(0, 0, Ball.size, Ball.size);

            g2d.setTransform(saveAT);
        }
    }

    void onStart() {
        System.out.println("Start or resume animation thread");
        this.animationThread.wakeup();
    }

    void onStop() {
        System.out.println("Suspend animation thread");
        this.animationThread.safeSuspend();
    }

    void onPlus() {
        System.out.println("Add a ball");
        balls.add(Ball.generate(getWidth(), getHeight()));
    }

    void onMinus() {
        System.out.println("Remove a ball");
        balls.remove(balls.size() - 1);
    }

    static class Ball {
        static int size = 50;
        int x;
        int y;
        double vx;
        double vy;
        Color color;

        static Ball generate(int maxPositionX, int maxPositionY) {
            Random rand = new Random();
            float r = (float) (rand.nextFloat() / 2f + 0.5);
            float g = (float) (rand.nextFloat() / 2f + 0.5);
            float b = (float) (rand.nextFloat() / 2f + 0.5);

            Ball ball = new Ball();
            ball.x = rand.nextInt(maxPositionX - size);
            ball.y = rand.nextInt(maxPositionY - size);
            ball.vx = rand.nextDouble(20) - 10;
            ball.vy = rand.nextDouble(20) - 10;
            ball.color = new Color(r, g, b);

            return ball;
        }
    }

    class AnimationThread extends Thread {
        boolean suspend = false;
        boolean stop = false;

        synchronized void wakeup() {
            suspend = false;
            notify();
        }

        void safeSuspend() {
            suspend = true;
        }

        public void run() {
            while (!stop) {
                synchronized (this) {
                    try {
                        if (suspend) {
                            System.out.println("suspending");
                            wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (balls) {
                    // przesuń kulki
                    for (Ball b : balls) {
                        b.x += b.vx;
                        b.y += b.vy;
                    }

                    // wykonaj odbicia od ściany
                    for (Ball b : balls) {
                        if (b.x + Ball.size > getWidth() || b.x < 0) {
                            b.vx *= -1;
                        }
                        if (b.y + Ball.size > getHeight() || b.y < 0) {
                            b.vy *= -1;
                        }
                    }

                    // wykonaj odbicia od kulek
                    for (int i = 0; i < balls.size(); i++) {
                        for (int j = i + 1; j < balls.size(); j++) {
                            Ball b1 = balls.get(i);
                            Ball b2 = balls.get(j);
                            double dx = b1.x - b2.x;
                            double dy = b1.y - b2.y;

                            if (Math.sqrt(dx * dx + dy * dy) < Ball.size) {
//                                Trygonometryczne
//                                double angle = Math.atan2(dy, dx);
//                                double vx1 = b1.vx * Math.cos(angle) + b1.vy * Math.sin(angle);
//                                double vy1 = b1.vy * Math.cos(angle) - b1.vx * Math.sin(angle);
//                                double vx2 = b2.vx * Math.cos(angle) + b2.vy * Math.sin(angle);
//                                double vy2 = b2.vy * Math.cos(angle) - b2.vx * Math.sin(angle);
//                                b1.vx = vx2 * Math.cos(angle) - vy2 * Math.sin(angle);
//                                b1.vy = vx2 * Math.sin(angle) + vy2 * Math.cos(angle);
//                                b2.vx = vx1 * Math.cos(angle) - vy1 * Math.sin(angle);
//                                b2.vy = vx1 * Math.sin(angle) + vy1 * Math.cos(angle);

//                                Wektory
                                Vector x1 = new Vector(b1.x, b1.y);
                                Vector x2 = new Vector(b2.x, b2.y);
                                Vector v1 = new Vector(b1.vx, b1.vy);
                                Vector v2 = new Vector(b2.vx, b2.vy);
                                Vector vv1 = v1.subtract(x1.subtract(x2).multiply(v1.subtract(v2).dot(x1.subtract(x2)) /
                                        (x1.subtract(x2).length() * x1.subtract(x2).length())));
                                Vector vv2 = v2.subtract(x2.subtract(x1).multiply(v2.subtract(v1).dot(x2.subtract(x1)) /
                                        (x2.subtract(x1).length() * x2.subtract(x1).length())));
                                b1.vx = vv1.x;
                                b1.vy = vv1.y;
                                b2.vx = vv2.x;
                                b2.vy = vv2.y;
                            }
                        }
                    }

                    // wywołaj repaint
                    getRootPane().repaint();

                    // uśpij
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
