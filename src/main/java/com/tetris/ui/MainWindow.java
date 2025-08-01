package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.tetris.game.Game;
import com.tetris.game.GameController;
import com.tetris.util.GameConstants;

/**
 * ゲームのメインウィンドウクラス
 * ゲーム画面とサイドパネルを含むJFrameを管理
 */
public class MainWindow extends JFrame {

    private GamePanel gamePanel;
    private JPanel sidePanel;
    private Game game;
    private GameController gameController;

    // サイドパネルのコンポーネント
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel linesLabel;
    private JLabel statusLabel;
    private JPanel nextPiecePanel;

    /**
     * メインウィンドウのコンストラクタ
     * ウィンドウの初期設定と各パネルの配置を行う
     */
    public MainWindow() {
        initializeWindow();
        createGame();
        createComponents();
        layoutComponents();
        startGameLoop();
    }

    /**
     * ウィンドウの基本設定を行う
     */
    private void initializeWindow() {
        setTitle("クラシック・テトリス");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // 画面中央に配置
        setResizable(false); // サイズ変更不可
    }

    /**
     * ゲームインスタンスを作成する
     */
    private void createGame() {
        game = new Game();
    }

    /**
     * UIコンポーネントを作成する
     */
    private void createComponents() {
        // ゲーム画面パネルを作成
        gamePanel = new GamePanel(game);

        // ゲームコントローラーを作成
        gameController = new GameController(game, gamePanel);

        // サイドパネルを作成
        createSidePanel();
    }

    /**
     * サイドパネルを作成する
     */
    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setBackground(GameConstants.BACKGROUND_COLOR);
        sidePanel.setPreferredSize(new Dimension(200, GameConstants.WINDOW_HEIGHT));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // タイトル
        JLabel titleLabel = createLabel("TETRIS", 24);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createVerticalStrut(30));

        // スコア表示
        sidePanel.add(createLabel("SCORE", 14));
        scoreLabel = createLabel("0", 18);
        sidePanel.add(scoreLabel);
        sidePanel.add(Box.createVerticalStrut(20));

        // レベル表示
        sidePanel.add(createLabel("LEVEL", 14));
        levelLabel = createLabel("1", 18);
        sidePanel.add(levelLabel);
        sidePanel.add(Box.createVerticalStrut(20));

        // ライン数表示
        sidePanel.add(createLabel("LINES", 14));
        linesLabel = createLabel("0", 18);
        sidePanel.add(linesLabel);
        sidePanel.add(Box.createVerticalStrut(30));

        // 次のピース表示
        sidePanel.add(createLabel("NEXT", 14));
        nextPiecePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // TODO: 次のピースを描画
            }
        };
        nextPiecePanel.setBackground(Color.BLACK);
        nextPiecePanel.setPreferredSize(new Dimension(120, 80));
        nextPiecePanel.setMaximumSize(new Dimension(120, 80));
        nextPiecePanel.setBorder(BorderFactory.createLineBorder(GameConstants.GRID_COLOR));
        sidePanel.add(nextPiecePanel);
        sidePanel.add(Box.createVerticalStrut(30));

        // ステータス表示
        statusLabel = createLabel("PRESS ENTER TO START", 12);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(statusLabel);
        sidePanel.add(Box.createVerticalGlue());

        // 操作説明
        sidePanel.add(createLabel("CONTROLS", 12));
        sidePanel.add(createLabel("← → : Move", 10));
        sidePanel.add(createLabel("↑ : Rotate", 10));
        sidePanel.add(createLabel("↓ : Soft Drop", 10));
        sidePanel.add(createLabel("Space : Hard Drop", 10));
        sidePanel.add(createLabel("P : Pause", 10));
    }

    /**
     * ラベルを作成する
     */
    private JLabel createLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(GameConstants.TEXT_COLOR);
        label.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * コンポーネントをレイアウトに配置する
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }

    /**
     * ゲームループを開始する
     */
    private void startGameLoop() {
        // 画面更新用タイマー（60FPS）
        Timer updateTimer = new Timer(16, e -> {
            updateUI();
            gamePanel.repaint();
            nextPiecePanel.repaint();
        });
        updateTimer.start();

        // 初期フォーカスをゲームパネルに設定
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    /**
     * UI要素を更新する
     */
    private void updateUI() {
        scoreLabel.setText(String.valueOf(game.getScore()));
        levelLabel.setText(String.valueOf(game.getLevel()));
        linesLabel.setText(String.valueOf(game.getLines()));

        // ゲーム状態に応じたステータス表示
        switch (game.getGameState()) {
            case READY:
                statusLabel.setText("PRESS ENTER TO START");
                break;
            case PLAYING:
                statusLabel.setText("PLAYING");
                break;
            case PAUSED:
                statusLabel.setText("PAUSED");
                break;
            case GAME_OVER:
                statusLabel.setText("GAME OVER");
                break;
        }
    }
}