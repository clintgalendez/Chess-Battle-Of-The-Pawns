package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gui_components.ButtonSliders;
import com.gui_components.Clock;
import com.gui_components.RoundPanel;
import com.handlers.SettingsMenuHandler;
import com.loaders.GraphicsLoader;

public class SettingsUI extends JDialog {
    private JLayeredPane layeredPane;

    private static final Color CALICO = new Color(224, 190, 145);
    private static final Color ZEUS = new Color(47, 38, 29);

    private ButtonSliders resume;
    private ButtonSliders restart;
    private ButtonSliders help;
    private ButtonSliders sounds;

    private Clock clock;

    private GameUI GI;

    public SettingsUI(GameUI GI, Clock clock) {
        this.GI = GI;
        this.clock = clock;

        init();

        setLayout(null);
        setBackground(CALICO);
        setModal(true);
        setBounds(472, 153, 590, 584);

        getContentPane().add(layeredPane);
        
        setResizable(false);
        setUndecorated(true);

        setVisible(false);
    }

    public void init() {
        layeredPane = new JLayeredPane();
        layeredPane.setSize(590, 584);

        JPanel outerBorder = new JPanel();
        outerBorder.setBackground(CALICO);
        outerBorder.setSize(590, 584);

        JPanel innerBorder = new JPanel();
        innerBorder.setBackground(ZEUS);
        innerBorder.setBounds(2, 2, 586, 580);

        JLabel settingsBackground = new JLabel(new ImageIcon(GraphicsLoader.loadImage("resources/MenuBackground.png", 580, 574)));
        settingsBackground.setBounds(8, 8, 574, 566);

        resume = new ButtonSliders(new SettingsMenuHandler(this), "resources/Pause.png", "resources/Resume.png", this, GI, clock, 1);
        resume.setBounds(0, 0, 420, 91);

        JLabel resumeLabel = new JLabel("RESUME");
        resumeLabel.setFont(new Font("Verdana", Font.PLAIN,15));
        resumeLabel.setForeground(CALICO);
        resumeLabel.setHorizontalAlignment(JTextField.CENTER);
        resumeLabel.setSize(430, 91);

        RoundPanel resumePanel = new RoundPanel(ZEUS, 80, null);
        resumePanel.setSize(430, 91);
        resumePanel.add(resumeLabel);
        resumePanel.add(resume);

        restart = new ButtonSliders(new SettingsMenuHandler(this), "resources/Restart1.png", "resources/Restart2.png", this, GI, clock, 2);
        restart.setBounds(0, 0, 420, 91);
        
        JLabel restartLabel = new JLabel("RESTART");
        restartLabel.setFont(new Font("Verdana", Font.PLAIN,15));
        restartLabel.setForeground(CALICO);
        restartLabel.setHorizontalAlignment(JTextField.CENTER);
        restartLabel.setSize(430, 91);

        RoundPanel restartPanel = new RoundPanel(ZEUS, 80, null);
        restartPanel.setSize(430, 91);
        restartPanel.add(restart);
        restartPanel.add(restartLabel);

        help = new ButtonSliders(new SettingsMenuHandler(this), "resources/Help1.png", "resources/Help2.png", this, GI, clock, 3);
        help.setBounds(0, 0, 420, 91);

        JLabel helpLabel = new JLabel("HELP");
        helpLabel.setFont(new Font("Verdana", Font.PLAIN,15));
        helpLabel.setForeground(CALICO);
        helpLabel.setHorizontalAlignment(JTextField.CENTER);
        helpLabel.setSize(430, 91);

        RoundPanel helpPanel = new RoundPanel(ZEUS, 80, null);
        helpPanel.setSize(430, 91);
        helpPanel.add(help);
        helpPanel.add(helpLabel);

        sounds = new ButtonSliders(new SettingsMenuHandler(this), "resources/Sounds1.png", "resources/Sounds2.png", this, GI, clock, 4);
        sounds.setBounds(0, 0, 420, 91);

        JLabel soundsLabel = new JLabel("SOUNDS");
        soundsLabel.setFont(new Font("Verdana", Font.PLAIN,15));
        soundsLabel.setForeground(CALICO);
        soundsLabel.setHorizontalAlignment(JTextField.CENTER);
        soundsLabel.setSize(430, 91);

        RoundPanel playSoundsPanel = new RoundPanel(ZEUS, 80, null);
        playSoundsPanel.setSize(430, 91);
        playSoundsPanel.add(sounds);
        playSoundsPanel.add(soundsLabel);

        JPanel buttonSlidersPanel = new JPanel(new GridLayout(4, 0, 0, 30));
        buttonSlidersPanel.setOpaque(false);
        buttonSlidersPanel.setBounds(80, 80, 430, 424);
        buttonSlidersPanel.add(resumePanel);
        buttonSlidersPanel.add(restartPanel);
        buttonSlidersPanel.add(helpPanel);
        buttonSlidersPanel.add(playSoundsPanel);

        layeredPane.add(outerBorder, Integer.valueOf(0));
        layeredPane.add(innerBorder, Integer.valueOf(1));
        layeredPane.add(settingsBackground, Integer.valueOf(2));
        layeredPane.add(buttonSlidersPanel, Integer.valueOf(3));
    }

    public ButtonSliders getResume() {
        return resume;
    }

    public ButtonSliders getRestart() {
        return restart;
    }

    public ButtonSliders getHelp() {
        return help;
    }

    public ButtonSliders getSounds() {
        return sounds;
    }
}
