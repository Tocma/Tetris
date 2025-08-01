package com.tetris;

import com.tetris.ui.MainWindow;
import javax.swing.SwingUtilities;

/**
 * テトリスゲームのエントリーポイント
 * アプリケーションの起動とメインウィンドウの初期化を行う
 */
public class Main {

    public static void main(String[] args) {
        // Swingコンポーネントの操作はイベントディスパッチスレッドで実行
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}