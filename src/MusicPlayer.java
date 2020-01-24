import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;

public class MusicPlayer {

    static JFrame f = new JFrame("My First Music Video");
    static MyDrawPanel ml;

    class MyDrawPanel extends JPanel implements ControllerEventListener {
        boolean msg = false;

        // call repaint for every event
        public void controlChange(ShortMessage event) {
            msg = true;
            repaint();
        }

        // paint a random rectangle each time is called
        public void paintComponent(Graphics g) {
            if(msg) {
                Graphics2D g2d = (Graphics2D) g;

                int r = (int) (Math.random() * 250);
                int gr = (int) (Math.random() * 250);
                int b = (int) (Math.random() * 250);

                g.setColor(new Color(r,gr, b));

                int ht = (int) ((Math.random() * this.getWidth()/2) + 10);
                int width = (int) ((Math.random() * this.getHeight()/2) + 10);

                int x = (int) ((Math.random() * 0.7 * this.getWidth()) + 10);
                int y = (int) ((Math.random() * 0.7* this.getHeight()) + 10);

                g.fillRect(x, y, ht, width);
                msg = false;
            }
        }
    }

    // create a panel, set contents and visibility
    public void setUpGui() {
        ml = new MyDrawPanel();
        f.setContentPane(ml);
        f.setBounds(30, 30, 500, 500);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // companion method to generate an event easily
    public static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent  event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        }
        catch (InvalidMidiDataException ex) {
            System.out.println("failed to set message");
            ex.printStackTrace();
        }
        return event;
    }

    public void controlChange(ShortMessage event) {
        System.out.println("la");
    }

    public void run() {
        setUpGui();

        try {
            // create a sequencer, open it and add the event 127 to track
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addControllerEventListener(ml, new int[] {127});

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            // generate random notes to play and associated events of type 127
            int r = 0;
            for (int i = 0; i < 60; i+=4) {
                r = (int) ((Math.random() * 50) + 1);
                track.add(makeEvent(144, 1, r, 100, i));
                track.add(makeEvent(176, 1, 127, 0, i));
                track.add(makeEvent(128, 1, r, 100, i + 2));
            }

            sequencer.setSequence(seq);
            sequencer.start();
            sequencer.setTempoInBPM(120);

            // close the sequencer after finish running
            while (sequencer.isRunning()) {
                Thread.sleep(1);
            }
            Thread.sleep(500);
            sequencer.stop();
            sequencer.close();
        }
        catch (Exception ex) {ex.printStackTrace();}
    }

    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();
        player.run();
    }
}
