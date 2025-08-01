package com.tetris.util;

import java.awt.Color;

/**
 * ゲーム全体で使用する定数を定義するクラス
 * ボードサイズ、ブロックサイズ、色、速度などの設定値を管理
 */
public class GameConstants {

    // ボード設定
    public static final int BOARD_WIDTH = 10; // ボードの幅（ブロック数）
    public static final int BOARD_HEIGHT = 20; // ボードの高さ（ブロック数）
    public static final int BLOCK_SIZE = 30; // 1ブロックのピクセルサイズ

    // ウィンドウ設定
    public static final int WINDOW_WIDTH = BOARD_WIDTH * BLOCK_SIZE + 200; // サイドパネル用の余白を含む
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT * BLOCK_SIZE + 50; // 上部余白を含む

    // ゲーム速度設定（ミリ秒）
    public static final int INITIAL_DELAY = 800; // 初期落下速度
    public static final int MIN_DELAY = 100; // 最速落下速度
    public static final int SOFT_DROP_DELAY = 50; // ソフトドロップ時の速度
    public static final int LEVEL_SPEED_INCREMENT = 50; // レベルアップごとの速度増加

    // スコア設定
    public static final int[] LINE_SCORES = { 0, 100, 300, 500, 800 }; // 0〜4ライン消去時の基本スコア
    public static final int LINES_PER_LEVEL = 10; // レベルアップに必要なライン数

    // テトリミノの色
    public static final Color[] TETROMINO_COLORS = {
            new Color(0, 0, 0), // 0: 空（黒）
            new Color(0, 240, 240), // 1: I - シアン
            new Color(240, 240, 0), // 2: O - 黄色
            new Color(160, 0, 240), // 3: T - 紫
            new Color(0, 240, 0), // 4: S - 緑
            new Color(240, 0, 0), // 5: Z - 赤
            new Color(0, 0, 240), // 6: J - 青
            new Color(240, 160, 0) // 7: L - オレンジ
    };

    // その他の色
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    public static final Color GRID_COLOR = new Color(50, 50, 50);
    public static final Color TEXT_COLOR = Color.WHITE;

    // プライベートコンストラクタ（インスタンス化を防ぐ）
    private GameConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}