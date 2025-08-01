package main.java.com.tetris.model;

/**
 * テトリミノ（ブロック）を表すクラス
 * 7種類のテトリミノの形状、回転状態、色を管理
 */
public class Tetromino {

    /**
     * テトリミノの種類を表す列挙型
     */
    public enum Type {
        I(1), O(2), T(3), S(4), Z(5), J(6), L(7);

        private final int colorIndex;

        Type(int colorIndex) {
            this.colorIndex = colorIndex;
        }

        public int getColorIndex() {
            return colorIndex;
        }
    }

    // 各テトリミノの形状定義（4x4の配列で表現）
    // 1が塗りつぶし、0が空白を表す
    private static final int[][][][] SHAPES = {
            // I型
            {
                    { { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 } },
                    { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 } }
            },
            // O型（回転しても形が変わらない）
            {
                    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }
            },
            // T型
            {
                    { { 0, 1, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } }
            },
            // S型
            {
                    { { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 1, 0, 0, 0 }, { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } }
            },
            // Z型
            {
                    { { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 1, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 0, 0 }, { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } }
            },
            // J型
            {
                    { { 1, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 } }
            },
            // L型
            {
                    { { 0, 0, 1, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 } },
                    { { 0, 0, 0, 0 }, { 1, 1, 1, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } },
                    { { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } }
            }
    };

    private Type type;
    private int rotation; // 0〜3の回転状態
    private int x; // ボード上のX座標
    private int y; // ボード上のY座標

    /**
     * テトリミノのコンストラクタ
     * 
     * @param type テトリミノの種類
     */
    public Tetromino(Type type) {
        this.type = type;
        this.rotation = 0;
        this.x = 3; // 初期位置（ボード中央上部）
        this.y = 0;
    }

    /**
     * テトリミノのコピーコンストラクタ
     * 
     * @param other コピー元のテトリミノ
     */
    public Tetromino(Tetromino other) {
        this.type = other.type;
        this.rotation = other.rotation;
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * 現在の回転状態での形状を取得
     * 
     * @return 4x4の形状配列
     */
    public int[][] getShape() {
        return SHAPES[type.ordinal()][rotation];
    }

    /**
     * 時計回りに90度回転
     */
    public void rotateClockwise() {
        rotation = (rotation + 1) % 4;
    }

    /**
     * 反時計回りに90度回転
     */
    public void rotateCounterClockwise() {
        rotation = (rotation + 3) % 4;
    }

    // ゲッターとセッター
    public Type getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColorIndex() {
        return type.getColorIndex();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 4;
    }

    /**
     * 左に移動
     */
    public void moveLeft() {
        x--;
    }

    /**
     * 右に移動
     */
    public void moveRight() {
        x++;
    }

    /**
     * 下に移動
     */
    public void moveDown() {
        y++;
    }
}