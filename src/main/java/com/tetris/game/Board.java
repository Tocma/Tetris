package main.java.com.tetris.game;

import main.java.com.tetris.model.Tetromino;
import main.java.com.tetris.util.GameConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * ゲームボード（盤面）を管理するクラス
 * 固定されたブロックの管理、衝突判定、ライン消去などを行う
 */
public class Board {
    
    private int[][] grid;  // ボードのグリッド（0:空、1-7:各テトリミノの色）
    
    /**
     * ボードのコンストラクタ
     * グリッドを初期化する
     */
    public Board() {
        initializeBoard();
    }
    
    /**
     * ボードを初期化する
     */
    private void initializeBoard() {
        grid = new int[GameConstants.BOARD_HEIGHT][GameConstants.BOARD_WIDTH];
        clearBoard();
    }
    
    /**
     * ボードをクリアする
     */
    public void clearBoard() {
        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                grid[y][x] = 0;
            }
        }
    }
    
    /**
     * 指定位置のセルの値を取得
     * @param x X座標
     * @param y Y座標
     * @return セルの値（0:空、1-7:色インデックス）
     */
    public int getCell(int x, int y) {
        if (isValidPosition(x, y)) {
            return grid[y][x];
        }
        return -1;  // 無効な位置
    }
    
    /**
     * 指定位置にブロックを設置
     * @param x X座標
     * @param y Y座標
     * @param value 設置する値（色インデックス）
     */
    public void setCell(int x, int y, int value) {
        if (isValidPosition(x, y)) {
            grid[y][x] = value;
        }
    }
    
    /**
     * テトリミノをボードに固定する
     * @param tetromino 固定するテトリミノ
     */
    public void placeTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int colorIndex = tetromino.getColorIndex();
        
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (shape[row][col] != 0) {
                    int boardX = tetromino.getX() + col;
                    int boardY = tetromino.getY() + row;
                    setCell(boardX, boardY, colorIndex);
                }
            }
        }
    }
    
    /**
     * テトリミノが配置可能かチェック
     * @param tetromino チェックするテトリミノ
     * @return 配置可能な場合true
     */
    public boolean canPlace(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int tetrominoX = tetromino.getX();
        int tetrominoY = tetromino.getY();
        
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (shape[row][col] != 0) {
                    int boardX = tetrominoX + col;
                    int boardY = tetrominoY + row;
                    
                    // ボード範囲外チェック
                    if (!isValidPosition(boardX, boardY)) {
                        return false;
                    }
                    
                    // 既存ブロックとの衝突チェック
                    if (grid[boardY][boardX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 完成したラインを検出して消去する
     * @return 消去したライン数
     */
    public int clearCompleteLines() {
        List<Integer> completeLines = new ArrayList<>();
        
        // 完成したラインを検出
        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            if (isLineFull(y)) {
                completeLines.add(y);
            }
        }
        
        // ラインを消去
        for (int lineIndex : completeLines) {
            removeLine(lineIndex);
        }
        
        return completeLines.size();
    }
    
    /**
     * 指定行が埋まっているかチェック
     * @param y チェックする行
     * @return 埋まっている場合true
     */
    private boolean isLineFull(int y) {
        for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
            if (grid[y][x] == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 指定行を削除し、上の行を下にずらす
     * @param lineToRemove 削除する行
     */
    private void removeLine(int lineToRemove) {
        // 削除する行より上の行を1行ずつ下にずらす
        for (int y = lineToRemove; y > 0; y--) {
            for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
                grid[y][x] = grid[y - 1][x];
            }
        }
        
        // 最上段をクリア
        for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
            grid[0][x] = 0;
        }
    }
    
    /**
     * 指定座標がボード内の有効な位置かチェック
     * @param x X座標
     * @param y Y座標
     * @return 有効な位置の場合true
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < GameConstants.BOARD_WIDTH && 
               y >= 0 && y < GameConstants.BOARD_HEIGHT;
    }
    
    /**
     * ゲームオーバー状態かチェック（最上段にブロックがあるか）
     * @return ゲームオーバーの場合true
     */
    public boolean isGameOver() {
        // 最上段（非表示エリア）にブロックがあるかチェック
        for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
            if (grid[0][x] != 0 || grid[1][x] != 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ボードのグリッドを取得（読み取り専用）
     * @return グリッドのコピー
     */
    public int[][] getGrid() {
        int[][] copy = new int[GameConstants.BOARD_HEIGHT][GameConstants.BOARD_WIDTH];
        for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
            System.arraycopy(grid[y], 0, copy[y], 0, GameConstants.BOARD_WIDTH);
        }
        return copy;
    }
}
