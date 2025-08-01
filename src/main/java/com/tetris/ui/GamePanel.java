package com.tetris.ui;

import com.tetris.util.GameConstants;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * ゲーム画面の描画を担当するパネルクラス
 * ボード、落下中のテトリミノ、固定されたブロックなどを描画
 */
public class GamePanel extends JPanel {

    /**
     * ゲームパネルのコンストラクタ
     * パネルの初期設定を行う
     */
    public GamePanel() {
        initializePanel();
    }

    /**
     * パネルの初期設定を行う
     */
    private void initializePanel() {
        setPreferredSize(new Dimension(
                GameConstants.BOARD_WIDTH * GameConstants.BLOCK_SIZE,
                GameConstants.BOARD_HEIGHT * GameConstants.BLOCK_SIZE));
        setBackground(GameConstants.BACKGROUND_COLOR);
        setFocusable(true);
        setDoubleBuffered(true); // ちらつき防止
    }

    /**
     * パネルの描画処理
     * 
     * @param g グラフィックスコンテキスト
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // グリッド線を描画
        drawGrid(g2d);

        // TODO: ボードの固定ブロックを描画

        // TODO: 落下中のテトリミノを描画

        // TODO: ゴーストピース（着地位置の予測表示）を描画
    }

    /**
     * グリッド線を描画する
     * 
     * @param g2d グラフィックスコンテキスト
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GameConstants.GRID_COLOR);

        // 縦線を描画
        for (int x = 0; x <= GameConstants.BOARD_WIDTH; x++) {
            g2d.drawLine(
                    x * GameConstants.BLOCK_SIZE, 0,
                    x * GameConstants.BLOCK_SIZE, GameConstants.BOARD_HEIGHT * GameConstants.BLOCK_SIZE);
        }

        // 横線を描画
        for (int y = 0; y <= GameConstants.BOARD_HEIGHT; y++) {
            g2d.drawLine(
                    0, y * GameConstants.BLOCK_SIZE,
                    GameConstants.BOARD_WIDTH * GameConstants.BLOCK_SIZE, y * GameConstants.BLOCK_SIZE);
        }
    }

    /**
     * ブロックを描画する
     * 
     * @param g2d        グラフィックスコンテキスト
     * @param x          X座標（ブロック単位）
     * @param y          Y座標（ブロック単位）
     * @param colorIndex 色のインデックス
     */
    private void drawBlock(Graphics2D g2d, int x, int y, int colorIndex) {
        if (colorIndex <= 0 || colorIndex >= GameConstants.TETROMINO_COLORS.length) {
            return; // 無効な色インデックスの場合は描画しない
        }

        int pixelX = x * GameConstants.BLOCK_SIZE;
        int pixelY = y * GameConstants.BLOCK_SIZE;

        // ブロックの塗りつぶし
        g2d.setColor(GameConstants.TETROMINO_COLORS[colorIndex]);
        g2d.fillRect(pixelX, pixelY, GameConstants.BLOCK_SIZE, GameConstants.BLOCK_SIZE);

        // ブロックの枠線（立体感を出すため）
        g2d.setColor(GameConstants.TETROMINO_COLORS[colorIndex].brighter());
        g2d.drawLine(pixelX, pixelY, pixelX + GameConstants.BLOCK_SIZE - 1, pixelY);
        g2d.drawLine(pixelX, pixelY, pixelX, pixelY + GameConstants.BLOCK_SIZE - 1);

        g2d.setColor(GameConstants.TETROMINO_COLORS[colorIndex].darker());
        g2d.drawLine(pixelX + GameConstants.BLOCK_SIZE - 1, pixelY,
                pixelX + GameConstants.BLOCK_SIZE - 1, pixelY + GameConstants.BLOCK_SIZE - 1);
        g2d.drawLine(pixelX, pixelY + GameConstants.BLOCK_SIZE - 1,
                pixelX + GameConstants.BLOCK_SIZE - 1, pixelY + GameConstants.BLOCK_SIZE - 1);
    }
}