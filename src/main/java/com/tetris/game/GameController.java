package com.tetris.game;

import com.tetris.ui.GamePanel;
import com.tetris.util.GameConstants;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * ゲームの操作を制御するクラス
 * キーボード入力の処理とゲームへのコマンド送信を行う
 */
public class GameController extends KeyAdapter {

    private Game game; // ゲームインスタンス
    private GamePanel gamePanel; // ゲームパネル
    private Timer softDropTimer; // ソフトドロップ用タイマー
    private boolean isSoftDropping; // ソフトドロップ中フラグ

    /**
     * ゲームコントローラーのコンストラクタ
     * 
     * @param game      ゲームインスタンス
     * @param gamePanel ゲームパネル
     */
    public GameController(Game game, GamePanel gamePanel) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.isSoftDropping = false;

        initializeSoftDropTimer();

        // キーリスナーを登録
        gamePanel.addKeyListener(this);
    }

    /**
     * ソフトドロップタイマーを初期化
     */
    private void initializeSoftDropTimer() {
        softDropTimer = new Timer(GameConstants.SOFT_DROP_DELAY, e -> {
            if (isSoftDropping && game.getGameState() == Game.GameState.PLAYING) {
                game.moveTetrominoDown();
                gamePanel.repaint();
            }
        });
    }

    /**
     * キー押下時の処理
     * 
     * @param e キーイベント
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // ゲームがプレイ中でない場合は一部のキーのみ受け付ける
        if (game.getGameState() != Game.GameState.PLAYING) {
            handleNonPlayingKeys(e);
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                game.moveTetrominoLeft();
                break;

            case KeyEvent.VK_RIGHT:
                game.moveTetrominoRight();
                break;

            case KeyEvent.VK_UP:
                game.rotateTetromino();
                break;

            case KeyEvent.VK_DOWN:
                startSoftDrop();
                break;

            case KeyEvent.VK_SPACE:
                game.hardDrop();
                break;

            case KeyEvent.VK_P:
                game.togglePause();
                break;

            case KeyEvent.VK_ESCAPE:
                game.stopGame();
                break;

            default:
                return; // 画面更新不要
        }

        // 画面を更新
        gamePanel.repaint();
    }

    /**
     * キー離した時の処理
     * 
     * @param e キーイベント
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            stopSoftDrop();
        }
    }

    /**
     * 非プレイ中のキー処理
     * 
     * @param e キーイベント
     */
    private void handleNonPlayingKeys(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                if (game.getGameState() == Game.GameState.READY ||
                        game.getGameState() == Game.GameState.GAME_OVER) {
                    game.startGame();
                    gamePanel.repaint();
                }
                break;

            case KeyEvent.VK_P:
                if (game.getGameState() == Game.GameState.PAUSED) {
                    game.togglePause();
                    gamePanel.repaint();
                }
                break;

            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    /**
     * ソフトドロップを開始
     */
    private void startSoftDrop() {
        if (!isSoftDropping) {
            isSoftDropping = true;
            softDropTimer.start();
        }
    }

    /**
     * ソフトドロップを停止
     */
    private void stopSoftDrop() {
        isSoftDropping = false;
        softDropTimer.stop();
    }

    /**
     * ゲームインスタンスを設定
     * 
     * @param game 新しいゲームインスタンス
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * コントローラーをクリーンアップ
     */
    public void cleanup() {
        stopSoftDrop();
        gamePanel.removeKeyListener(this);
    }
}
