package main;

import data.Data;
import data.DataLoader;
import graphics.HeatMap;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ProjectPanel extends JPanel {
    public static int screenWidth_displayed;
    public static int screenHeight_displayed;

    Dimension size;

    BufferedImage tempScreen;
    Graphics2D g2_temp;

    private static KeyboardInputs keyboardInputs;
    public static MouseInputs mouseInputs;

    public Data data;

    public HeatMap heatMap;

    public ProjectPanel() {
//        The screen buffer
        tempScreen = new BufferedImage(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2_temp = tempScreen.createGraphics();

        keyboardInputs = new KeyboardInputs(this);
        mouseInputs = new MouseInputs(this);

        addKeyListener(keyboardInputs);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        this.setDoubleBuffered(true);

        data = DataLoader.loadCSV("res/data/DCT_2D.csv");

        heatMap = new HeatMap(data);
    }

    public static void getScreenDimensions() {
        screenWidth_displayed = Project.screenWidthJ;
        screenHeight_displayed = Project.screenHeightJ;
    }

    public void setPanelSize() {
        size = new Dimension(screenWidth_displayed, screenHeight_displayed);

        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g2_temp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2_temp.setColor(Constants.BG_COLOR);
        g2_temp.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        heatMap.render(g2_temp);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(tempScreen, 0, 0, screenWidth_displayed, screenHeight_displayed, null);
    }

    public void updateProject() {
    }
}