package com.tetris.game;

import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.Timer;

import com.tetris.effects.AnimationManager;
import com.tetris.model.Tetromino;
import com.tetris.util.GameConstants;

/**
 * ゲームロジックの中心クラス
 * ゲーム状態、スコア、レベル、テトリミノの生成と管理を行う
 */
public class Game {

    /**
     * ゲームの状態を表す列挙型
     */
    public enum GameState {
        READY, // ゲーム開始前
        PLAYING, // プレイ中
        PAUSED, // 一時停止中
        GAME_OVER // ゲームオーバー
    }

    private Board board; // ゲームボード
    private Tetromino currentTetromino; // 現在操作中のテトリミノ
    private Tetromino nextTetromino; // 次のテトリミノ
    private GameState gameState; // ゲーム状態
    private Timer gameTimer; // ゲームタイマー
    private Random random; // 乱数生成器
    private AnimationManager animationManager; // アニメーション管理

    private int score; // スコア
    private int level; // レベル
    private int lines; // 消去したライン数
    private int currentDelay; // 現在の落下速度（ミリ秒）

    /**
     * ゲームのコンストラクタ
     */
    public Game() {
        initializeGame();
    }

    /**
     * ゲームを初期化する
     */
    private void initializeGame() {
        board = new Board();
        random = new Random();
        gameState = GameState.READY;
        animationManager = new AnimationManager();

        // タイマーの初期化
        gameTimer = new Timer(GameConstants.INITIAL_DELAY, this::gameUpdate);
        gameTimer.setCoalesce(true);

        resetGameStats();
    }

    /**
     * ゲーム統計をリセット
     */
    private void resetGameStats() {
        score = 0;
        level = 1;
        lines = 0;
        currentDelay = GameConstants.INITIAL_DELAY;
    }

    /**
     * ゲームを開始する
     */
    public void startGame() {
        if (gameState != GameState.PLAYING) {
            board.clearBoard();
            resetGameStats();

            // 最初のテトリミノを生成
            nextTetromino = createRandomTetromino();
            spawnNextTetromino();

            gameState = GameState.PLAYING;
            gameTimer.setDelay(currentDelay);
            gameTimer.start();
        }
    }

    /**
     * ゲームを一時停止/再開する
     */
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            gameTimer.stop();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            gameTimer.start();
        }
    }

    /**
     * ゲームを停止する
     */
    public void stopGame() {
        gameTimer.stop();
        gameState = GameState.GAME_OVER;
    }

    /**
     * ゲームの更新処理（タイマーから呼ばれる）
     * 
     * @param e アクションイベント
     */
    private void gameUpdate(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            moveTetrominoDown();
        }
    }

    /**
     * ランダムなテトリミノを生成
     * 
     * @return 新しいテトリミノ
     */
    private Tetromino createRandomTetromino() {
        Tetromino.Type[] types = Tetromino.Type.values();
        Tetromino.Type randomType = types[random.nextInt(types.length)];
        return new Tetromino(randomType);
    }

    /**
     * 次のテトリミノを生成して配置
     */
    private void spawnNextTetromino() {
        currentTetromino = nextTetromino;
        nextTetromino = createRandomTetromino();

        // 初期位置でテトリミノが配置できない場合はゲームオーバー
        if (!board.canPlace(currentTetromino)) {
            stopGame();
        }
    }

    /**
     * テトリミノを下に移動
     */
    public void moveTetrominoDown() {
        if (currentTetromino == null || gameState != GameState.PLAYING) {
            return;
        }

        currentTetromino.moveDown();

        if (!board.canPlace(currentTetromino)) {
            // 移動できない場合は元に戻して固定
            currentTetromino.setY(currentTetromino.getY() - 1);
            placeCurrentTetromino();
        }
    }

    /**
     * テトリミノを左に移動
     */
    public void moveTetrominoLeft() {
        if (currentTetromino == null || gameState != GameState.PLAYING) {
            return;
        }

        currentTetromino.moveLeft();
        if (!board.canPlace(currentTetromino)) {
            currentTetromino.moveRight(); // 元に戻す
        }
    }

    /**
     * テトリミノを右に移動
     */
    public void moveTetrominoRight() {
        if (currentTetromino == null || gameState != GameState.PLAYING) {
            return;
        }

        currentTetromino.moveRight();
        if (!board.canPlace(currentTetromino)) {
            currentTetromino.moveLeft(); // 元に戻す
        }
    }

    /**
     * テトリミノを回転
     */
    public void rotateTetromino() {
        if (currentTetromino == null || gameState != GameState.PLAYING) {
            return;
        }

        currentTetromino.rotateClockwise();
        if (!board.canPlace(currentTetromino)) {
            currentTetromino.rotateCounterClockwise(); // 元に戻す
        }
    }

    /**
     * テトリミノをハードドロップ（即座に落下）
     */
    public void hardDrop() {
        if (currentTetromino == null || gameState != GameState.PLAYING) {
            return;
        }

        while (true) {
            currentTetromino.moveDown();
            if (!board.canPlace(currentTetromino)) {
                currentTetromino.setY(currentTetromino.getY() - 1);
                break;
            }
        }

        placeCurrentTetromino();
    }

    /**
     * 現在のテトリミノをボードに固定
     */
    private void placeCurrentTetromino() {
        board.placeTetromino(currentTetromino);

        // ライン消去処理
        int clearedLines = board.clearCompleteLines();
        if (clearedLines > 0) {
            updateScore(clearedLines);
            updateLevel();
        }

        // ゲームオーバーチェック
        if (board.isGameOver()) {
            stopGame();
        } else {
            // 次のテトリミノを生成
            spawnNextTetromino();
        }
    }

    /**
     * スコアを更新
     * 
     * @param clearedLines 消去したライン数
     */
    private void updateScore(int clearedLines) {
        lines += clearedLines;
        int baseScore = GameConstants.LINE_SCORES[clearedLines];
        score += baseScore * level;
    }

    /**
     * レベルを更新
     */
    private void updateLevel() {
        int newLevel = (lines / GameConstants.LINES_PER_LEVEL) + 1;
        if (newLevel != level) {
            level = newLevel;
            updateGameSpeed();
        }
    }

    /**
     * ゲーム速度を更新
     */
    private void updateGameSpeed() {
        currentDelay = Math.max(
                GameConstants.MIN_DELAY,
                GameConstants.INITIAL_DELAY - (level - 1) * GameConstants.LEVEL_SPEED_INCREMENT);
        gameTimer.setDelay(currentDelay);
    }

    // ゲッター
    public Board getBoard() {
        return board;
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public Tetromino getNextTetromino() {
        return nextTetromino;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getLines() {
        return lines;
    }
}