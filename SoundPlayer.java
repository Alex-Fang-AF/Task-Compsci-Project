import javax.sound.sampled.*;

/**
 * Very small SoundPlayer that generates simple tones when WAV files are not available.
 * Methods run playback on a background thread so they don't block the UI.
 */
public class SoundPlayer {

    // Play a single tone (frequency in Hz) for durationMs milliseconds.
    public static void playTone(double freq, int durationMs) {
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                int numSamples = (int)((durationMs / 1000.0) * sampleRate);
                byte[] buf = new byte[2 * numSamples]; // 16-bit

                for (int i = 0; i < numSamples; i++) {
                    double angle = 2.0 * Math.PI * i * freq / sampleRate;
                    short val = (short)(Math.sin(angle) * Short.MAX_VALUE);
                    buf[2*i] = (byte)(val & 0xff);
                    buf[2*i+1] = (byte)((val >> 8) & 0xff);
                }

                AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
                    line.open(af);
                    line.start();
                    line.write(buf, 0, buf.length);
                    line.drain();
                    line.stop();
                }
            } catch (Exception e) {
                // silent fail - sound is non-critical
            }
        }).start();
    }

    // Play a short bell sound (descending two-tone)
    public static void playBell() {
        new Thread(() -> {
            try {
                playToneSync(880, 80);
                Thread.sleep(40);
                playToneSync(660, 120);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Play a short comedic tri-beep
    public static void playComedic() {
        new Thread(() -> {
            try {
                playToneSync(900, 80);
                Thread.sleep(90);
                playToneSync(1100, 80);
                Thread.sleep(90);
                playToneSync(1300, 80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Helper to play tone synchronously on the current thread (used inside background threads)
    private static void playToneSync(double freq, int durationMs) {
        try {
            float sampleRate = 44100f;
            int numSamples = (int)((durationMs / 1000.0) * sampleRate);
            byte[] buf = new byte[2 * numSamples]; // 16-bit

            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i * freq / sampleRate;
                short val = (short)(Math.sin(angle) * Short.MAX_VALUE);
                buf[2*i] = (byte)(val & 0xff);
                buf[2*i+1] = (byte)((val >> 8) & 0xff);
            }

            AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
                line.open(af);
                line.start();
                line.write(buf, 0, buf.length);
                line.drain();
                line.stop();
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
