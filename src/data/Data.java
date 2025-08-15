package data;

public class Data {
    public String[] textures;
    public String[] features;
    public float[][] values;

    public Data(String text) {
        extractTextures(text);
        extractFeatures(text);
        extractValues(text);
    }

//    Extracts the texture names from the text data
    private void extractTextures(String text_input) {
//        The line where the texture names are present
        String textures_line = text_input.split("\n")[0];
//        The comma separated values in the line
//        The first value is x-y label identifier, not a texture name
        String[] textures_line_values = textures_line.split(",");
//        The number of texture names in the line
        int texture_count = textures_line_values.length - 1;

        textures = new String[texture_count];

//        Copies the texture names starting at index 1 at the texture_line_values to the textures array
        System.arraycopy(textures_line_values, 1, textures, 0, texture_count);
    }

//    Extracts the feature names from the text data
    private void extractFeatures(String text_input) {
//        The lines of the text data
        String[] lines = text_input.split("\n");
//        The number of feature names
        int feature_count = lines.length - 1;

        features = new String[feature_count];

        for(int i = 0; i < feature_count; i++) {
//            Starts from the second line
//            The first line has texture names
            String line = lines[i + 1];
//            Extracts the first value in the line as the feature name
            features[i] = line.split(",")[0];
        }
    }

//    Extracts the numerical values from the text data
    private void extractValues(String text_input) {
//        The lines of the text data
        String[] lines = text_input.split("\n");
//        The cells of the text data
        String[][] cells = new String[lines.length][lines[0].split(",").length];
        for(int line_index = 0; line_index < cells.length; line_index++) {
            cells[line_index] = lines[line_index].split(",");
        }

//        The number of rows in the 2d array
//        The first line has labels, not numerical data
        int rows = lines.length - 1;
//        The number of columns in the 2d array
//        The first column has labels, not numerical data
        int cols = lines[0].split(",").length - 1;

        values = new float[rows][cols];

//        For each row and column in the values array
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
//                Extracts the numerical values from the cells skipping the first row and column as they are labels
                values[row][col] = Float.parseFloat(cells[row + 1][col + 1]);
            }
        }
    }
}