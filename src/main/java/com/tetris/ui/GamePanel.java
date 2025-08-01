package com.tetris.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.tetris.game.Game;
import com.tetris.model.Tetromino;
import com.tetris.util.GameConstants;

/**
 * ゲーム画面の描画を担当するパネルクラス
 * ボード、落下中のテトリミノ、固定されたブロックなどを描画
 */
public class GamePanel extends JPanel {

    private Game game;

    /**
     * ゲームパネルのコンストラクタ
     * 
     * @param game ゲームインスタンス
     */
    public GamePanel(Game game) {
        this.game = game;
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

        // ボードの固定ブロックを描画
        drawBoard(g2d);

        // ゴーストピース（着地位置の予測表示）を描画
        if (game.getCurrentTetromino() != null && game.getGameState() == Game.GameState.PLAYING) {
            drawGhostPiece(g2d);
        }

        // 落下中のテトリミノを描画
        if (game.getCurrentTetromino() != null) {
            drawTetromino(g2d, game.getCurrentTetromino(), 1.0f);
        }

        // ゲームオーバー時のオーバーレイ
        if (game.getGameState() == Game.GameState.GAME_OVER) {
            drawGameOverOverlay(g2d);
        }
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
     * ボードの固定ブロックを描画する
     * 
     * @param g2d グラフィックスコンテキスト
     */
    private void drawBoard(Graphics2D g2d) {
        int[][] grid = game.getBoard().getGrid();

        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                if (grid[y][x] != 0) {
                    drawBlock(g2d, x, y, grid[y][x], 1.0f);
                }
            }
        }
    }

    /**
     * テトリミノを描画する
     * 
     * @param g2d       グラフィックスコンテキスト
     * @param tetromino 描画するテトリミノ
     * @param alpha     透明度（0.0〜1.0）
     */
    private void drawTetromino(Graphics2D g2d, Tetromino tetromino, float alpha) {
        int[][] shape = tetromino.getShape();
        int colorIndex = tetromino.getColorIndex();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (shape[row][col] != 0) {
                    int x = tetromino.getX() + col;
                    int y = tetromino.getY() + row;

                    if (x >= 0 && x < GameConstants.BOARD_WIDTH &&
                            y >= 0 && y < GameConstants.BOARD_HEIGHT) {
                        drawBlock(g2d, x, y, colorIndex, alpha);
                    }
                }
            }
        }
    }

    /**
     * ゴーストピース（着地位置の予測表示）を描画する
     * 
     * @param g2d グラフィックスコンテキスト
     */
    private void drawGhostPiece(Graphics2D g2d) {
        Tetromino ghost = new Tetromino(game.getCurrentTetromino());

        // ゴーストピースの位置を計算（最下部まで落下）
        while (game.getBoard().canPlace(ghost)) {
            ghost.moveDown();
        }
        ghost.setY(ghost.getY() - 1);

        // 半透明で描画
        drawTetromino(g2d, ghost, 0.3f);
    }

    /**
     * ブロックを描画する
     * 
     * @param g2d        グラフィックスコンテキスト
     * @param x          X座標（ブロック単位）
     * @param y          Y座標（ブロック単位）
     * @param colorIndex 色のインデックス
     * @param alpha      透明度（0.0〜1.0）
     */
    private void drawBlock(Graphics2D g2d, int x, int y, int colorIndex, float alpha) {
        if (colorIndex <= 0 || colorIndex >= GameConstants.TETROMINO_COLORS.length) {
            return;
        }

        int pixelX = x * GameConstants.BLOCK_SIZE;
        int pixelY = y * GameConstants.BLOCK_SIZE;

        // 透明度を適用した色を作成
        Color baseColor = GameConstants.TETROMINO_COLORS[colorIndex];
        Color color = new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                (int) (255 * alpha));

        // ブロックの塗りつぶし
        g2d.setColor(color);
        g2d.fillRect(pixelX, pixelY, GameConstants.BLOCK_SIZE, GameConstants.BLOCK_SIZE);

        // ブロックの枠線（立体感を出すため）
        Color brighterColor = new Color(
                Math.min(255, baseColor.getRed() + 50),
                Math.min(255, baseColor.getGreen() + 50),
                Math.min(255, baseColor.getBlue() + 50),
                (int) (255 * alpha));
        g2d.setColor(brighterColor);
        g2d.drawLine(pixelX, pixelY, pixelX + GameConstants.BLOCK_SIZE - 1, pixelY);
        g2d.drawLine(pixelX, pixelY, pixelX, pixelY + GameConstants.BLOCK_SIZE - 1);

        Color darkerColor = new Color(
                Math.max(0, baseColor.getRed() - 50),
                Math.max(0, baseColor.getGreen() - 50),
                Math.max(0, baseColor.getBlue() - 50),
                (int) (255 * alpha));
        g2d.setColor(darkerColor);
        g2d.drawLine(pixelX + GameConstants.BLOCK_SIZE - 1, pixelY,
                pixelX + GameConstants.BLOCK_SIZE - 1, pixelY + GameConstants.BLOCK_SIZE - 1);
        g2d.drawLine(pixelX, pixelY + GameConstants.BLOCK_SIZE - 1,
                pixelX + GameConstants.BLOCK_SIZE - 1, pixelY + GameConstants.BLOCK_SIZE - 1);
    }

    /**
     * ゲームオーバー時のオーバーレイを描画
     * 
     * @param g2d グラフィックスコンテキスト
     */
    private void drawGameOverOverlay(Graphics2D g2d) {
        // 半透明の黒いオーバーレイ
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // ゲームオーバーテキスト
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 36));
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        int textX = (getWidth() - textWidth) / 2;
        int textY = getHeight() / 2;
        g2d.drawString(gameOverText, textX, textY);

        // 再スタート指示
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
        String restartText = "Press ENTER to restart";
        textWidth = g2d.getFontMetrics().stringWidth(restartText);
        textX = (getWidth() - textWidth) / 2;
        g2d.drawString(restartText, textX, textY + 40);
    }
}