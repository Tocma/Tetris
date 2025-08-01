package com.tetris.effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ゲーム内のアニメーション効果を管理するクラス
 * ライン消去、レベルアップ、パーティクルエフェクトなどの視覚効果を制御
 */
public class AnimationManager {

    private List<LineAnimation> lineAnimations;
    private List<Particle> particles;
    private LevelUpAnimation levelUpAnimation;
    private long animationStartTime;

    /**
     * アニメーション管理クラスのコンストラクタ
     */
    public AnimationManager() {
        lineAnimations = new ArrayList<>();
        particles = new ArrayList<>();
        levelUpAnimation = null;
    }

    /**
     * アニメーションを更新する
     * 
     * @param currentTime 現在の時刻（ミリ秒）
     */
    public void update(long currentTime) {
        // ラインアニメーションの更新
        lineAnimations.removeIf(anim -> anim.isFinished(currentTime));

        // パーティクルの更新
        particles.forEach(p -> p.update());
        particles.removeIf(Particle::isDead);

        // レベルアップアニメーションの更新
        if (levelUpAnimation != null && levelUpAnimation.isFinished(currentTime)) {
            levelUpAnimation = null;
        }
    }

    /**
     * ライン消去アニメーションを開始
     * 
     * @param lines 消去するライン番号のリスト
     */
    public void startLineAnimation(List<Integer> lines) {
        long startTime = System.currentTimeMillis();
        for (int line : lines) {
            lineAnimations.add(new LineAnimation(line, startTime));
        }
    }

    /**
     * レベルアップアニメーションを開始
     * 
     * @param newLevel 新しいレベル
     */
    public void startLevelUpAnimation(int newLevel) {
        levelUpAnimation = new LevelUpAnimation(newLevel, System.currentTimeMillis());
    }

    /**
     * パーティクルエフェクトを追加
     * 
     * @param x     X座標（ピクセル）
     * @param y     Y座標（ピクセル）
     * @param color パーティクルの色
     * @param count パーティクル数
     */
    public void addParticleEffect(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    /**
     * ブロック固定時のパーティクルエフェクトを追加
     * 
     * @param blockX    ブロックのX座標（グリッド単位）
     * @param blockY    ブロックのY座標（グリッド単位）
     * @param blockSize ブロックのサイズ（ピクセル）
     * @param color     パーティクルの色
     */
    public void addBlockPlaceEffect(int blockX, int blockY, int blockSize, Color color) {
        int centerX = blockX * blockSize + blockSize / 2;
        int centerY = blockY * blockSize + blockSize / 2;
        addParticleEffect(centerX, centerY, color, 5);
    }

    /**
     * アニメーションを描画する
     * 
     * @param g2d         グラフィックスコンテキスト
     * @param boardWidth  ボードの幅（ピクセル）
     * @param boardHeight ボードの高さ（ピクセル）
     * @param blockSize   ブロックサイズ（ピクセル）
     */
    public void render(Graphics2D g2d, int boardWidth, int boardHeight, int blockSize) {
        long currentTime = System.currentTimeMillis();

        // ラインアニメーションの描画
        for (LineAnimation anim : lineAnimations) {
            anim.render(g2d, boardWidth, blockSize, currentTime);
        }

        // パーティクルの描画
        for (Particle particle : particles) {
            particle.render(g2d);
        }

        // レベルアップアニメーションの描画
        if (levelUpAnimation != null) {
            levelUpAnimation.render(g2d, boardWidth, boardHeight, currentTime);
        }
    }

    /**
     * ライン消去アニメーションクラス
     */
    private static class LineAnimation {
        private final int line;
        private final long startTime;
        private static final long DURATION = 500; // アニメーション時間（ミリ秒）

        public LineAnimation(int line, long startTime) {
            this.line = line;
            this.startTime = startTime;
        }

        public boolean isFinished(long currentTime) {
            return currentTime - startTime > DURATION;
        }

        public void render(Graphics2D g2d, int boardWidth, int blockSize, long currentTime) {
            float progress = Math.min(1.0f, (currentTime - startTime) / (float) DURATION);

            // フラッシュ効果
            float flashIntensity = (float) Math.sin(progress * Math.PI);
            int alpha = (int) (255 * flashIntensity * (1 - progress));

            g2d.setColor(new Color(255, 255, 255, alpha));
            g2d.fillRect(0, line * blockSize, boardWidth, blockSize);

            // 横に広がる光の効果
            if (progress < 0.5f) {
                int expandWidth = (int) (boardWidth * progress * 2);
                int centerX = boardWidth / 2;

                GradientPaint gradient = new GradientPaint(
                        centerX - expandWidth / 2, 0, new Color(255, 255, 100, alpha),
                        centerX + expandWidth / 2, 0, new Color(255, 255, 100, 0));

                g2d.setPaint(gradient);
                g2d.fillRect(centerX - expandWidth / 2, line * blockSize,
                        expandWidth, blockSize);
            }
        }
    }

    /**
     * レベルアップアニメーションクラス
     */
    private static class LevelUpAnimation {
        private final int level;
        private final long startTime;
        private static final long DURATION = 2000; // アニメーション時間（ミリ秒）

        public LevelUpAnimation(int level, long startTime) {
            this.level = level;
            this.startTime = startTime;
        }

        public boolean isFinished(long currentTime) {
            return currentTime - startTime > DURATION;
        }

        public void render(Graphics2D g2d, int width, int height, long currentTime) {
            float progress = Math.min(1.0f, (currentTime - startTime) / (float) DURATION);

            // 画面全体のフラッシュ
            if (progress < 0.2f) {
                float flashProgress = progress / 0.2f;
                int alpha = (int) (100 * (1 - flashProgress));
                g2d.setColor(new Color(255, 255, 255, alpha));
                g2d.fillRect(0, 0, width, height);
            }

            // レベルアップテキスト
            if (progress < 0.8f) {
                float textProgress = progress / 0.8f;
                int alpha = (int) (255 * Math.min(1, 2 * (1 - textProgress)));

                g2d.setColor(new Color(255, 255, 0, alpha));
                g2d.setFont(new Font("Arial", Font.BOLD, 48));

                String text = "LEVEL " + level;
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textX = (width - textWidth) / 2;
                int textY = height / 2;

                // テキストの拡大効果
                float scale = 1.0f + 0.5f * (float) Math.sin(textProgress * Math.PI);
                g2d.translate(width / 2, height / 2);
                g2d.scale(scale, scale);
                g2d.drawString(text, -textWidth / 2, 0);
                g2d.scale(1 / scale, 1 / scale);
                g2d.translate(-width / 2, -height / 2);
            }
        }
    }

    /**
     * パーティクルクラス
     */
    private static class Particle {
        private float x, y;
        private float vx, vy;
        private float life;
        private final Color color;
        private static final Random random = new Random();

        public Particle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.life = 1.0f;

            // ランダムな速度を設定
            float angle = (float) (random.nextDouble() * Math.PI * 2);
            float speed = 1 + random.nextFloat() * 3;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed - 2; // 上向きの傾向
        }

        public void update() {
            x += vx;
            y += vy;
            vy += 0.2f; // 重力
            life -= 0.02f;
            vx *= 0.98f; // 空気抵抗
        }

        public boolean isDead() {
            return life <= 0;
        }

        public void render(Graphics2D g2d) {
            int alpha = (int) (255 * life);
            int size = (int) (5 * life);

            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2d.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }
    }
}