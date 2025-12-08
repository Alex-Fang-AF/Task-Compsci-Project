import javax.sound.sampled.*;

/**
 * Very small SoundPlayer that generates simple tones when WAV files are not available.
 * Methods run playback on a background thread so they don't block the UI.
 */
public class SoundPlayer {

    // Holds reference to the currently playing line so it can be stopped from other threads
    private static volatile SourceDataLine currentLine = null;

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
                SourceDataLine line = null;
                try {
                    line = AudioSystem.getSourceDataLine(af);
                    synchronized (SoundPlayer.class) { currentLine = line; }
                    line.open(af);
                    line.start();
                    line.write(buf, 0, buf.length);
                    line.drain();
                    line.stop();
                } finally {
                    if (line != null) {
                        try { line.close(); } catch (Throwable t) { }
                        synchronized (SoundPlayer.class) {
                            if (currentLine == line) currentLine = null;
                        }
                    }
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

    // Start a looping bell playback on a background daemon thread.
    // Returns the Thread instance so callers can interrupt() it to stop looping.
    public static Thread startBellLoop() {
        Thread t = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // play a short bell sequence synchronously
                    playToneSync(880, 80);
                    try { Thread.sleep(40); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                    playToneSync(660, 120);
                    // pause between repeats
                    try { Thread.sleep(800); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
            } catch (Throwable t2) {
                // silent fail
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
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

    // Helper to play tone
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
            SourceDataLine line = null;
            try {
                line = AudioSystem.getSourceDataLine(af);
                synchronized (SoundPlayer.class) { currentLine = line; }
                line.open(af);
                line.start();
                line.write(buf, 0, buf.length);
                line.drain();
                line.stop();
            } finally {
                if (line != null) {
                    try { line.close(); } catch (Throwable t) { }
                    synchronized (SoundPlayer.class) {
                        if (currentLine == line) currentLine = null;
                    }
                }
            }
        } catch (Exception e) {
            // silent fail - sound is non-critical
        }
    }

    // Stop any currently playing tone immediately.
    public static void stopAll() {
        SourceDataLine line = currentLine;
        if (line != null) {
            synchronized (SoundPlayer.class) {
                line = currentLine;
                if (line != null) {
                    try {
                        line.stop();
                        line.flush();
                        line.close();
                    } catch (Throwable t) { /* ignore */ }
                    currentLine = null;
                }
            }
        }
    }
}
