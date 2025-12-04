import javax.sound.sampled.*;

/**
 * Simple sound manager for playing beep/success sounds.
 * Uses Java's built-in audio capabilities (no external files needed).
 */
public class SoundManager {
    
    // Play a simple beep/success sound
    public static void playSuccessSound() {
        try {
            // Generate a simple success beep: 800Hz for 200ms
            playTone(800, 200);
        } catch (Exception e) {
            // Silently fail if audio is not available
        }
    }
    
    // Play a tone at a specific frequency for a duration
    private static void playTone(int frequency, int durationMs) throws Exception {
        int sampleRate = 44100;
        byte[] buffer = new byte[sampleRate * durationMs / 1000];
        
        // Generate sine wave
        for (int i = 0; i < buffer.length; i++) {
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            buffer[i] = (byte) (Math.sin(angle) * 100);
        }
        
        // Play the generated audio
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }
}
