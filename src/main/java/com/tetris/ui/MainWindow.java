package com.tetris.ui;

import com.tetris.util.GameConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * ゲームのメインウィンドウクラス
 * ゲーム画面とサイドパネルを含むJFrameを管理
 */
public class MainWindow extends JFrame {

    private GamePanel gamePanel;
    private JPanel sidePanel;

    /**
     * メインウィンドウのコンストラクタ
     * ウィンドウの初期設定と各パネルの配置を行う
     */
    public MainWindow() {
        initializeWindow();
        createComponents();
        layoutComponents();
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
     * UIコンポーネントを作成する
     */
    private void createComponents() {
        // ゲーム画面パネルを作成
        gamePanel = new GamePanel();

        // サイドパネルを作成（スコア、レベル、次のピース表示用）
        sidePanel = new JPanel();
        sidePanel.setBackground(GameConstants.BACKGROUND_COLOR);
        // TODO: サイドパネルの内容を実装
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
     * ゲームを開始する
     */
    public void startGame() {
        // TODO: ゲーム開始処理を実装
        gamePanel.requestFocusInWindow();
    }
}