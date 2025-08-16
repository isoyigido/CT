package graphics;

import data.Data;
import utils.Constants;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Project.font;

public class HeatMap {
//    The text color of texture and feature names
    public static final Color OUTER_TEXT_COLOR = Color.BLACK;
//    The text color of the cell values
    public static final Color INNER_TEXT_COLOR = Color.WHITE;

//    Max value in the data
    public static final float MAX_VALUE = 1.0f;

//    Colors for presenting low, mid, and high values
    public static final Color LOW_COLOR = new Color(80, 120, 80); // muted green
    public static final Color MID_COLOR = new Color(245, 245, 220); // beige
    public static final Color HIGH_COLOR = new Color(180, 50, 50); // muted red

//    High and low texts above and below the spectrum
    public static final String HIGH_TEXT = "High";
    public static final String LOW_TEXT = "Low";

//    The gap between the spectrum and the high/low texts
    public static final int VERTICAL_BAR_TEXT_GAP = 16;

//    Cell width and height
    public static final int CELL_WIDTH = 192;
    public static final int CELL_HEIGHT = 64;

//    Helper values
    public static final int CELL_X_CENTER = CELL_WIDTH / 2;
    public static final int CELL_Y_CENTER = CELL_HEIGHT / 2;

//    The gap between the feature/texture names and the heatmap
    public static final int HORIZONTAL_LABEL_GAP = 64;
    public static final int VERTICAL_LABEL_GAP = 32;

//    The gap between the spectrum and the heatmap
    public static final int BAR_GAP = 128;

//    The width and height of the spectrum bar
    public static final int BAR_WIDTH = 96;
    public static final int BAR_HEIGHT = 1024;

//    Total heatmap width and height (with the labels and spectrum)
    public final int HEATMAP_WIDTH, HEATMAP_HEIGHT,
//    Helper values
            HEATMAP_X_CENTER, HEATMAP_Y_CENTER,
//    The x and y coordinates of the heatmap on the rendered screen
            HEATMAP_X, HEATMAP_Y,
//    Where the cells start on the heatmap
            HEATMAP_CELLS_X,
//    The x and y coordinates of the spectrum bar on the heatmap
            BAR_X, BAR_Y;

    public static final FontMetrics fontMetrics;

//    The input data
    Data data;

    BufferedImage figure_image;

//    Number of cell rows and columns
    int cell_row, cell_col;

    static {
//        Loading the font metrics for the default font
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(font);
        fontMetrics = graphics2D.getFontMetrics();
        graphics2D.dispose();
    }

    public HeatMap(Data data_input) {
        data = data_input;

        cell_row = data.values.length;
        cell_col = data.values[0].length;

//        Determining the widest feature name to place the heatmap accordingly
        int maxFeatureNameWidth = 0;
        for(String feature_name : data.features) {
            int feature_name_width = fontMetrics.stringWidth(feature_name);
            if(feature_name_width > maxFeatureNameWidth) {
                maxFeatureNameWidth = feature_name_width;
            }
        }

        HEATMAP_CELLS_X = maxFeatureNameWidth + HORIZONTAL_LABEL_GAP;

        HEATMAP_WIDTH = HEATMAP_CELLS_X + cell_col * CELL_WIDTH + BAR_GAP + BAR_WIDTH;
        HEATMAP_HEIGHT = cell_row * CELL_HEIGHT + VERTICAL_LABEL_GAP + fontMetrics.getHeight();

        HEATMAP_X_CENTER = HEATMAP_WIDTH / 2;
        HEATMAP_Y_CENTER = HEATMAP_HEIGHT / 2;

        HEATMAP_X = Constants.SCREEN_X_CENTER - HEATMAP_X_CENTER;
        HEATMAP_Y = Constants.SCREEN_Y_CENTER - HEATMAP_Y_CENTER;

        BAR_X = HEATMAP_CELLS_X + cell_col * CELL_WIDTH + BAR_GAP;
        BAR_Y = HEATMAP_Y_CENTER - (BAR_HEIGHT / 2);

//        Generates the figure image
        generateFigureImage();

//        Saves the figure image on the output folder with the set margins
        Utils.saveAsJPG(figure_image, "output/figure.jpg", 64, 64);
    }

    public void generateFigureImage() {
        figure_image = new BufferedImage(HEATMAP_WIDTH, HEATMAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = figure_image.createGraphics();
        graphics2D.setFont(font);

//        Rendering the cells
        for(int row = 0; row < data.values.length; row++) {
            for(int col = 0; col < data.values[0].length; col++) {
//                For each value
                float value = data.values[row][col];
//                The value as String (3 decimals)
                String text = String.format("%.3f", value);

//                Rendering the cells with their corresponding colors
                int rect_x = HEATMAP_CELLS_X + col * CELL_WIDTH;
                int rect_y = row * CELL_HEIGHT;

                graphics2D.setColor(colorOf(value));
                graphics2D.fillRect(rect_x, rect_y, CELL_WIDTH, CELL_HEIGHT);

//                Rendering the value texts inside the cells
                int text_x = rect_x + CELL_X_CENTER - (fontMetrics.stringWidth(text) / 2);
                int text_y = rect_y + CELL_Y_CENTER - (fontMetrics.getHeight() / 2) + fontMetrics.getAscent();

                graphics2D.setColor(INNER_TEXT_COLOR);
                graphics2D.drawString(text, text_x, text_y);
            }
        }

//        Rendering the spectrum bar
        for (int y = 0; y < BAR_HEIGHT; y++) {
            float t = (float) y / (BAR_HEIGHT - 1);

            Color color;
            if (t < 0.5f) {
                // Top half: high → mid
                float ratio = t / 0.5f;
                color = interpolateColor(HIGH_COLOR, MID_COLOR, ratio);
            } else {
                // Bottom half: mid → low
                float ratio = (t - 0.5f) / 0.5f;
                color = interpolateColor(MID_COLOR, LOW_COLOR, ratio);
            }

            graphics2D.setColor(color);
            graphics2D.drawLine(BAR_X, BAR_Y + y, BAR_X + BAR_WIDTH, BAR_Y + y); // Draw horizontal strip
        }

        graphics2D.setColor(OUTER_TEXT_COLOR);

//        Rendering the feature names
        for(int row = 0; row < data.features.length; row++) {
            String feature = data.features[row];

            int text_x = 0;
            int text_y = row * CELL_HEIGHT + CELL_Y_CENTER + fontMetrics.getAscent() - (fontMetrics.getHeight() / 2);

            graphics2D.drawString(feature, text_x, text_y);
        }

//        Rendering the texture names
        for(int col = 0; col < data.textures.length; col++) {
            String texture = data.textures[col];

            int text_x = HEATMAP_CELLS_X + col * CELL_WIDTH + CELL_X_CENTER - (fontMetrics.stringWidth(texture) / 2);
            int text_y = cell_row * CELL_HEIGHT + VERTICAL_LABEL_GAP + fontMetrics.getAscent();

            graphics2D.drawString(texture, text_x, text_y);
        }

//        Rendering the high and low texts above and below the spectrum bar
        int text_x = BAR_X + ((BAR_WIDTH - fontMetrics.stringWidth(HIGH_TEXT))/ 2);
        int text_y = BAR_Y - VERTICAL_BAR_TEXT_GAP;
        graphics2D.drawString(HIGH_TEXT, text_x, text_y);

        text_x = BAR_X + ((BAR_WIDTH - fontMetrics.stringWidth(LOW_TEXT))/ 2);
        text_y = BAR_Y + BAR_HEIGHT + VERTICAL_BAR_TEXT_GAP + fontMetrics.getAscent();
        graphics2D.drawString(LOW_TEXT, text_x, text_y);

        graphics2D.dispose();
    }

//    Returns the corresponding color for a float value
    public Color colorOf(float value) {
        if (value < 0.0f || value > MAX_VALUE) return Color.BLACK;

        // Maximum input is 0.25, scale to [0, 1]
        value = Math.min(value / MAX_VALUE, 1.0f);

        if (value <= 0.5f) {
            // Interpolate between lowColor and midColor
            float ratio = value / 0.5f;
            return interpolateColor(LOW_COLOR, MID_COLOR, ratio);
        } else {
            // Interpolate between midColor and highColor
            float ratio = (value - 0.5f) / 0.5f;
            return interpolateColor(MID_COLOR, HIGH_COLOR, ratio);
        }
    }

//    Color interpolation
    private Color interpolateColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed()   + t * (c2.getRed()   - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue()  + t * (c2.getBlue()  - c1.getBlue()));
        return new Color(r, g, b);
    }

//    Render method
    public void render(Graphics2D graphics2D) {
        graphics2D.drawImage(figure_image, HEATMAP_X, HEATMAP_Y, null);
    }
}
