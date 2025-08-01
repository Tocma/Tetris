package com.tetris.effects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

/**
 * ゲーム内の音響効果を管理するクラス
 * 効果音の生成、再生、音量調整などを行う
 */
public class SoundManager {

    private Map<SoundType, byte[]> soundCache;
    private boolean soundEnabled;
    private float masterVolume;

    /**
     * サウンドの種類を表す列挙型
     */
    public enum SoundType {
        BLOCK_PLACE, // ブロック固定音
        LINE_CLEAR, // ライン消去音
        TETRIS, // 4ライン消去音
        LEVEL_UP, // レベルアップ音
        GAME_OVER, // ゲームオーバー音
        ROTATE, // 回転音
        MOVE // 移動音
    }

    /**
     * サウンド管理クラスのコンストラクタ
     */
    public SoundManager() {
        soundCache = new HashMap<>();
        soundEnabled = true;
        masterVolume = 0.7f;

        // サウンドを生成してキャッシュ
        generateSounds();
    }

    /**
     * プログラムで生成したサウンドをキャッシュに追加
     */
    private void generateSounds() {
        try {
            // 各種効果音を生成
            soundCache.put(SoundType.BLOCK_PLACE, generateBlockPlaceSound());
            soundCache.put(SoundType.LINE_CLEAR, generateLineClearSound());
            soundCache.put(SoundType.TETRIS, generateTetrisSound());
            soundCache.put(SoundType.LEVEL_UP, generateLevelUpSound());
            soundCache.put(SoundType.GAME_OVER, generateGameOverSound());
            soundCache.put(SoundType.ROTATE, generateRotateSound());
            soundCache.put(SoundType.MOVE, generateMoveSound());
        } catch (Exception e) {
            System.err.println("サウンド生成エラー: " + e.getMessage());
            soundEnabled = false;
        }
    }

    /**
     * サウンドを再生
     * 
     * @param soundType 再生するサウンドの種類
     */
    public void playSound(SoundType soundType) {
        if (!soundEnabled || !soundCache.containsKey(soundType)) {
            return;
        }

        // 新しいスレッドで再生（ゲームをブロックしないため）
        new Thread(() -> {
            try {
                byte[] soundData = soundCache.get(soundType);
                ByteArrayInputStream bais = new ByteArrayInputStream(soundData);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bais);

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // 音量を設定
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(masterVolume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }

                clip.start();

                // 再生終了を待つ
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (Exception e) {
                System.err.println("サウンド再生エラー: " + e.getMessage());
            }
        }).start();
    }

    /**
     * ブロック固定音を生成
     */
    private byte[] generateBlockPlaceSound() throws Exception {
        return generateTone(200, 50, 0.5f); // 低めの短い音
    }

    /**
     * ライン消去音を生成
     */
    private byte[] generateLineClearSound() throws Exception {
        // 上昇する音のシーケンス
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 3; i++) {
            byte[] tone = generateTone(400 + i * 200, 100, 0.6f);
            baos.write(tone);
        }
        return baos.toByteArray();
    }

    /**
     * テトリス（4ライン消去）音を生成
     */
    private byte[] generateTetrisSound() throws Exception {
        // より華やかな音のシーケンス
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int[] frequencies = { 523, 659, 784, 1047 }; // C, E, G, C (Cメジャーコード)
        for (int freq : frequencies) {
            byte[] tone = generateTone(freq, 150, 0.7f);
            baos.write(tone);
        }
        return baos.toByteArray();
    }

    /**
     * レベルアップ音を生成
     */
    private byte[] generateLevelUpSound() throws Exception {
        // ファンファーレ風の音
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int[] frequencies = { 523, 587, 659, 784 }; // C, D, E, G
        for (int freq : frequencies) {
            byte[] tone = generateTone(freq, 100, 0.8f);
            baos.write(tone);
        }
        return baos.toByteArray();
    }

    /**
     * ゲームオーバー音を生成
     */
    private byte[] generateGameOverSound() throws Exception {
        // 下降する音のシーケンス
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 4; i++) {
            byte[] tone = generateTone(400 - i * 50, 200, 0.6f);
            baos.write(tone);
        }
        return baos.toByteArray();
    }

    /**
     * 回転音を生成
     */
    private byte[] generateRotateSound() throws Exception {
        return generateTone(600, 30, 0.4f); // 高めの短い音
    }

    /**
     * 移動音を生成
     */
    private byte[] generateMoveSound() throws Exception {
        return generateTone(300, 20, 0.3f); // 短いクリック音
    }

    /**
     * 単純な正弦波トーンを生成
     * 
     * @param frequency 周波数（Hz）
     * @param duration  長さ（ミリ秒）
     * @param amplitude 振幅（0.0〜1.0）
     * @return 生成された音声データ
     */
    private byte[] generateTone(int frequency, int duration, float amplitude) throws Exception {
        float sampleRate = 44100;
        int sampleCount = (int) (sampleRate * duration / 1000);

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 正弦波を生成
        for (int i = 0; i < sampleCount; i++) {
            double angle = 2.0 * Math.PI * i * frequency / sampleRate;
            short sample = (short) (Math.sin(angle) * amplitude * 32767);

            // フェードイン/フェードアウト
            float fadeRatio = 1.0f;
            int fadeLength = (int) (sampleCount * 0.1); // 10%の長さでフェード

            if (i < fadeLength) {
                fadeRatio = (float) i / fadeLength;
            } else if (i > sampleCount - fadeLength) {
                fadeRatio = (float) (sampleCount - i) / fadeLength;
            }

            sample = (short) (sample * fadeRatio);

            // リトルエンディアンで書き込み
            baos.write(sample & 0xFF);
            baos.write((sample >> 8) & 0xFF);
        }

        byte[] audioData = baos.toByteArray();

        // AudioInputStreamに変換してから返す
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream ais = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, result);

        return result.toByteArray();
    }

    /**
     * サウンドの有効/無効を切り替え
     * 
     * @param enabled 有効にする場合true
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /**
     * マスター音量を設定
     * 
     * @param volume 音量（0.0〜1.0）
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    /**
     * サウンドが有効かどうかを取得
     * 
     * @return サウンドが有効な場合true
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}