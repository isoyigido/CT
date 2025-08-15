package main;

import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Project implements Runnable{
    private ProjectPanel projectPanel;
    private Thread projectThread;

    private final int FPS_SET = Constants.FPS;
    public final int UPS_SET = Constants.UPS;
    int frames = 0;
    long lastCheck = 0;

    int updates = 0;

    public static JFrame jFrame;
    public static int screenWidthJ;
    public static int screenHeightJ;

    public static Font font;

    public Project() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        font = new Font("Arial", Font.PLAIN, 24);

        jFrame = new JFrame();

        jFrame.setTitle("CT Project");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.setUndecorated(true);

        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(jFrame);
        }

        screenWidthJ = jFrame.getWidth();
        screenHeightJ = jFrame.getHeight();

        ProjectPanel.getScreenDimensions();

        projectPanel = new ProjectPanel();
        projectPanel.setPanelSize();

        jFrame.add(projectPanel);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);

        projectPanel.setFocusable(true);
        projectPanel.requestFocus();

        startProjectLoop();
    }

    private void startProjectLoop() {
        projectThread = new Thread(this);
        projectThread.start();
    }

    public void updateProject() {
        projectPanel.updateProject();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;

            previousTime = currentTime;

            if (deltaU >= 1) {
                updateProject();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                projectPanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }
}