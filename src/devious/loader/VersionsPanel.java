package devious.loader;

import devious.loader.res.Res;
import devious.loader.updater.ClientUpdater;
import devious.loader.updater.ClientVersion;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VersionsPanel extends JPanel implements ActionListener {

    private final boolean remote;

    private final JButton refreshButton;

    private final JComboBox nameBox;
    private final DefaultComboBoxModel nameModel;

    private final JComboBox versionBox;
    private final DefaultComboBoxModel versionModel;

    private final JButton playButton;

    private List<ClientVersion> list;

    private final Loader loader;

    public VersionsPanel(final Loader loader, final boolean remote) {
        super(new BorderLayout(5, 0));
        setBorder(new TitledBorder((remote ? "Remote" : "Local") + " Client Versions"));
        this.loader = loader;
        this.remote = remote;

        refreshButton = new JButton(Res.REFRESH_16);
        refreshButton.addActionListener(this);

        nameModel = new DefaultComboBoxModel();
        nameModel.addElement("--- No Names ---");

        nameBox = new JComboBox(nameModel);
        nameBox.setEnabled(false);
        nameBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                final String name = (String) nameBox.getSelectedItem();
                final List<String> matches = new ArrayList<String>();
                if(list != null && name != null){
                    for(final ClientVersion cv : list)
                        if(cv.name.equals(name))
                            matches.add(cv.version);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        versionModel.removeAllElements();
                        if(matches.isEmpty()){
                            versionModel.addElement("--- No Versions ---");
                            versionBox.setEnabled(false);
                        }else{
                            for(final String m : matches)
                                versionModel.addElement(m);
                            versionBox.setEnabled(true);
                        }
                        versionBox.repaint();
                    }
                });
            }
        });

        final JLabel nameLabel = new JLabel("Name:", JLabel.RIGHT);
        nameLabel.setPreferredSize(new Dimension(70, nameLabel.getPreferredSize().height));

        final JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameBox, BorderLayout.CENTER);

        versionModel = new DefaultComboBoxModel();
        versionModel.addElement("--- No Versions ---");

        versionBox = new JComboBox(versionModel);
        versionBox.setEnabled(false);

        final JLabel versionLabel = new JLabel("Version:", JLabel.RIGHT);
        versionLabel.setPreferredSize(new Dimension(70, versionLabel.getPreferredSize().height));

        final JPanel versionPanel = new JPanel(new BorderLayout(5, 0));
        versionPanel.add(versionLabel, BorderLayout.WEST);
        versionPanel.add(versionBox, BorderLayout.CENTER);

        final JPanel boxPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        boxPanel.add(namePanel);
        boxPanel.add(versionPanel);

        playButton = new JButton(Res.PLAY_16);
        playButton.addActionListener(this);

        add(refreshButton, BorderLayout.WEST);
        add(boxPanel, BorderLayout.CENTER);
        add(playButton, BorderLayout.EAST);

        refreshButton.doClick();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object source = e.getSource();
        if(source.equals(refreshButton)){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshButton.setEnabled(false);
                    refreshButton.repaint();
                    nameModel.removeAllElements();
                    nameModel.addElement("--- No Names ---");
                    nameBox.setEnabled(false);
                    nameBox.repaint();
                    versionModel.removeAllElements();
                    versionModel.addElement("--- No Versions ---");
                    versionBox.setEnabled(false);
                    versionBox.repaint();
                }
            });
            try{
                list = remote ? ClientUpdater.loadRemoteVersions() : ClientUpdater.loadLocalVersions();
                for(int i = 0; i < list.size(); i++){
                    final int fi = i;
                    final ClientVersion v = list.get(i);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if(fi == 0){
                                nameModel.removeAllElements();
                                nameBox.setEnabled(true);
                            }
                            nameModel.addElement(v.name);
                            nameBox.repaint();
                        }
                    });

                }
            }catch(Exception ex){
                ex.printStackTrace();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Error refreshing versions");
                    }
                });
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshButton.setEnabled(true);
                    refreshButton.repaint();
                }
            });
        }else if(source.equals(playButton)){
            final String name = (String) nameBox.getSelectedItem();
            final String version = (String) versionBox.getSelectedItem();
            if(list == null || name == null || version == null || !nameBox.isEnabled() || !versionBox.isEnabled()){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "No client selected!");
                    }
                });
                return;
            }
            ClientVersion cv = null;
            for(final ClientVersion c : list){
                if(c.name.equals(name) && c.version.equals(version)){
                    cv = c;
                    break;
                }
            }
            if(cv == null){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Invalid client version");
                    }
                });
                return;
            }
            try{
                final File jar = new File(ClientUpdater.CLIENTS_DIR, cv.fileName);
                if(!jar.exists())
                    ClientUpdater.download(cv);
                ClientStarter.start(jar);
                loader.dispose();
            }catch(Exception ex){
                ex.printStackTrace();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Error starting client");
                    }
                });
            }
        }
    }
}
