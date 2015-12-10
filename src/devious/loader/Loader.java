package devious.loader;

import devious.loader.res.Res;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.RavenSkin;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class Loader extends JFrame {

    public Loader() {
        super("Devious Loader");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        final JPanel center = new JPanel(new GridLayout(1, 2, 5, 5));
        center.add(new VersionsPanel(this, true));
        center.add(new VersionsPanel(this, false));

        add(new JLabel(Res.BANNER, JLabel.CENTER), BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        setIconImage(Res.ICON.getImage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SubstanceLookAndFeel.setSkin(new RavenSkin());
                final Loader loader = new Loader();
                loader.pack();
                loader.setLocationRelativeTo(null);
                loader.setVisible(true);
            }
        });
    }
}
