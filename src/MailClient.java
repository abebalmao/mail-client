import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

public class MailClient extends JFrame {
    /*
        initializes the textfields
     */
    private JTextField hostField;
    private JTextField emailField;
    private JTextField pwdField;
    private JTextField fromField;
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea messageField;
    private JButton submitButton;
    private JButton recieveButton;
    private JButton senderTab;
    private JButton recieverTab;
    private JPanel bottomButtons;
    private JPanel sendTop;
    private JScrollPane sendCenter;
    private JPanel recieveTop;
    private JScrollPane recieveCenter;
    private Properties propSend;
    private Properties propRecieve;


    public static void main(String[] args) {
        /*
            runs constructor
         */
        new MailClient();
    }

    public void initBottomButtons() {
        /*
            initializes and show the bottom buttons which act as navigation buttons
         */
        bottomButtons = new JPanel(new GridLayout(1,2));
        this.senderTab = new JButton("Send");
        this.recieverTab = new JButton("Receive");
        bottomButtons.add(senderTab);
        this.senderTab.addActionListener(new MailClient.SendTabButtonPressed());
        bottomButtons.add(recieverTab);
        this.recieverTab.addActionListener(new MailClient.RecieveTabButtonPressed());
        this.getContentPane().add("South", bottomButtons);
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public MailClient() {
        /*
            method run when exe gets started for the first time
         */
        initBottomButtons();
    }

    public void resetUI() {
        /*
            resets the UI if they are printed on the screen
         */
        if (sendTop != null) {
            this.getContentPane().remove(sendTop);
        }
        if (sendCenter != null) {
            this.getContentPane().remove(sendCenter);
        }
        if (recieveTop != null) {
            this.getContentPane().remove(recieveTop);
        }
        if (recieveCenter != null) {
            this.getContentPane().remove(recieveCenter);
        }
        this.repaint();
    }

    public void initSender() {
        /*
            builds the Sender part of the program
         */
        resetUI(); // resets ui first
        this.hostField = new JTextField();
        this.emailField = new JTextField();
        this.pwdField = new JTextField();
        this.fromField = new JTextField();
        this.toField = new JTextField();
        this.subjectField = new JTextField();
        this.messageField = new JTextArea();
        this.submitButton = new JButton("Submit");
        propSend = new Properties();

        sendTop = new JPanel(new GridLayout(7, 2));
        sendTop.add(new JLabel("Host: "));
        sendTop.add(this.hostField);
        sendTop.add(new JLabel("Email: "));
        sendTop.add(this.emailField);

        sendTop.add(new JLabel("Password: "));
        sendTop.add(this.pwdField);
        sendTop.add(new JLabel("From: "));
        sendTop.add(this.fromField);
        sendTop.add(new JLabel("To: "));
        sendTop.add(this.toField);
        sendTop.add(new JLabel("Subject: "));
        sendTop.add(this.subjectField);
        sendTop.add(this.submitButton);
        this.submitButton.addActionListener(new MailClient.SubmitButtonPressed());

        sendCenter = new JScrollPane(this.messageField);
        this.getContentPane().add("North", sendTop);
        this.getContentPane().add("Center", sendCenter);
        this.getContentPane().add("South", bottomButtons);
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public void initReciever() {
        /*
            builds the Receiver part of the program
         */
        resetUI(); // resets ui first
        this.hostField = new JTextField();
        this.emailField = new JTextField();
        this.pwdField = new JTextField();
        this.messageField = new JTextArea();
        this.recieveButton = new JButton("Receive");

        recieveTop = new JPanel(new GridLayout(4, 2));
        recieveTop.add(new JLabel("Server: "));
        recieveTop.add(this.hostField);
        recieveTop.add(new JLabel("Email: "));
        recieveTop.add(this.emailField);
        recieveTop.add(new JLabel("Password"));
        recieveTop.add(this.pwdField);
        recieveTop.add(this.recieveButton);
        this.recieveButton.addActionListener(new MailClient.RecieveButtonPressed());
        recieveCenter = new JScrollPane(this.messageField);

        this.getContentPane().add("North", recieveTop);
        this.getContentPane().add("Center", recieveCenter);
        this.getContentPane().add("South", bottomButtons);
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public void send() throws Exception{
        /*
            Connects to the smtp server,
            gets the message provided from the user
            and sends it
         */
        propSend.put("mail.smtp.auth", true);
        propSend.put("mail.smtp.host", hostField.getText());
        propSend.put("mail.smtp.port", 465);
        propSend.put("mail.smtp.starttls.enable", true);
        propSend.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        propSend.put("mail.transport.protocol", "smtp");
        Session session = Session.getInstance(propSend, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailField.getText(), pwdField.getText());
            }
        });

        Message message = new MimeMessage(session);
        message.setSubject(subjectField.getText());
        message.setText(messageField.getText());

        Address address = new InternetAddress(toField.getText());
        message.setRecipient(Message.RecipientType.TO, address);
        Transport.send(message);
    }

    public void receive() {
        /*
            connects to the smtp server,
            gets the inbox of the user
            and prints out the content of each
            email to a JTextArea displayed
            on the program
         */
        propRecieve = new Properties();
        propRecieve.put("mail.pop3.host", emailField.getText());
        propRecieve.put("mail.pop3.port", "465");
        propRecieve.put("mail.pop3.starttls.enable", "true");
        propRecieve.put("mail.store.protocol", "pop3s");

        Session session = Session.getInstance(propRecieve);
        try {
            Store mailStore = session.getStore();
            mailStore.connect(this.hostField.getText(), this.emailField.getText(), this.pwdField.getText());
            Folder folder = mailStore.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message[] emailMessages = folder.getMessages();
            for (int i = 0; i < emailMessages.length; i++) {
                Message message = emailMessages[i];
                messageField.append("From: " + message.getFrom()[0] + "\n");
                messageField.append("Subject: " + message.getSubject() + "\n");
                Part objMessage = message;
                Object attatchments = objMessage.getContent();
                if (attatchments instanceof Multipart) {
                    objMessage = ((Multipart)attatchments).getBodyPart(0);
                }
                String contentType = (objMessage).getContentType();
                if (contentType.startsWith("text/plain") || contentType.startsWith("text/html") || contentType.startsWith("TEXT/PLAIN") || contentType.startsWith("TEXT/HTML")) {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((objMessage).getInputStream()));
                    for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
                        messageField.append(str + "\n");
                    }
                }

                messageField.append("-----\n");

            }
            folder.close();
            mailStore.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SubmitButtonPressed implements ActionListener {
        /*
            When the Submit button is pressed,
            authenticate the login to the
            smtp server, then send the mail
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                MailClient.this.send();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class RecieveButtonPressed implements ActionListener {
        /*
            When button is pressed, run the recieve method
            which prints out the emails in the inbox
            to a JTextArea displayed on the screen
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                MailClient.this.receive();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class SendTabButtonPressed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            /*
                if Send button is pressed,
                run this method which builds
                the Sender part of the program

             */
            initSender();
        }
    }

    class RecieveTabButtonPressed implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            /*
                if Receive button is pressed,
                run this method which builds
                the Receiver part of the program
             */
            initReciever();
        }
    }

}
