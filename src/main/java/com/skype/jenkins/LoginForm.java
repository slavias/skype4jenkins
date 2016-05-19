package com.skype.jenkins;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.samczsun.skype4j.internal.StreamUtils;

public class LoginForm extends JFrame {

    private JLabel loginLabel = new JLabel("Login:");
    private JTextField loginInput = new JTextField("", 5);
    private JLabel passLabel = new JLabel("Password:");
    private JPasswordField passInput = new JPasswordField("", 5);
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    
    String[] data = new String[2];
    
    public LoginForm() {
        super ("Skype login");
        
        this.setBounds(100,100,250,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initBaseField();
        
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(3,2,2,2));
        container.add(loginLabel);
        container.add(loginInput);
        container.add(passLabel);
        container.add(passInput);

        ActionListener buttonListener = new ButtonEventListener(this);
        okButton.addActionListener(buttonListener);
        container.add(okButton);
        cancelButton.addActionListener(buttonListener);
        container.add(cancelButton);
    }
    
    public void initBaseField() {
        String[] data = null;
        try {
            data = StreamUtils.readFully(new FileInputStream("credentials")).split(":");
            loginInput.setText(data[0]);
            passInput.setText(data[1]);
        } catch (IOException e) {
            //Logger.out.error(e);
        }
        
    }
    
    public void close(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    public String[] getData() {
        return data;
    }

    class ButtonEventListener implements ActionListener {
        
        private LoginForm frame;
        
        public ButtonEventListener(LoginForm frame) {
            this.frame = frame;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ("OK".equals(e.getActionCommand())){
                data[0] = loginInput.getText();
                data[1] = new String(passInput.getPassword());
            }
            if ("Cancel".equals(e.getActionCommand())){
                frame.close();
            }
            frame.dispose();
        }
    }
    
    /*class WindowEventListener implements WindowListener {

        @Override
        public void windowActivated(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowClosed(WindowEvent e) {
            Logger.out.debug("closed "+Thread.currentThread().getName());
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowClosing(WindowEvent e) {
            Logger.out.debug("closing "+Thread.currentThread().getName());
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowIconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowOpened(WindowEvent e) {
            Logger.out.debug(Thread.currentThread().getName());
            // TODO Auto-generated method stub
            
        }
        
    }*/
}
