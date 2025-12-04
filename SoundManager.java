import javax.sound.sampled.*;

/**
 * Simple sound manager for playing beep/success sounds.
 * Access Building Blocks from Java Sound
 */
public class SoundManager {
    
    // Play a simple beep/success sound
    public static void playSuccessSound() {
        try {
            // Generate sound
            playTone(800, 200);
        } catch (Exception e) {
            // No sound played if failed/other scenarios
        }
    }
    
    // Play Tone after creating a task
    private static void playTone(int frequency, int durationMs) throws Exception {
        int sampleRate = 23000;
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
