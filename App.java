import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Sprite Can");
        // frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappySpriteCan flappySpriteCan = new FlappySpriteCan();
        frame.add(flappySpriteCan);
        frame.pack();
        flappySpriteCan.requestFocus();
        frame.setVisible(true);
    }
}
